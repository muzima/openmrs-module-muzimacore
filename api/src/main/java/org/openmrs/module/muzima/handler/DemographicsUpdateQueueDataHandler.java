/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
@Component
@Handler(supports = QueueData.class, order = 6)
public class DemographicsUpdateQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "json-demographics-update";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Log log = LogFactory.getLog(DemographicsUpdateQueueDataHandler.class);
    private QueueProcessorException queueProcessorException;
    private Patient unsavedPatient;

    /**
     * 
     * @param queueData - QueueData
     * @throws QueueProcessorException 
     */
    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing demographics update form data: " + queueData.getUuid());
        try {
            if (validate(queueData)) {
                Context.getPatientService().savePatient(unsavedPatient);
            }
        } catch (Exception e) {
            if (!e.getClass().equals(QueueProcessorException.class)) {
                queueProcessorException.addException(e);
            }
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    /**
     * 
     * @param queueData
     * @return boolean
     */
    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing registration form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();

        try {
            Object patientObject = JsonUtils.readAsObject(queueData.getPayload(), "$['patient']");
            processPatient(patientObject);
            return true;

        } catch (Exception e) {
            queueProcessorException.addException(e);
            return false;
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    /**
     * 
     * @param patientObject
     * @throws QueueProcessorException
     */
    private void processPatient(final Object patientObject) throws QueueProcessorException {
        String patientPayload = patientObject.toString();
        String uuid = JsonUtils.readAsString(patientPayload, "$['patient.uuid']");
        unsavedPatient = Context.getPatientService().getPatientByUuid(uuid);
        PatientService patientService = Context.getPatientService();
        PatientIdentifierType defaultIdentifierType = patientService.getPatientIdentifierType(1);
        String identifier = JsonUtils.readAsString(patientPayload, "$['patient.medical_record_number']");
        String identifierTypeUuid = JsonUtils.readAsString(patientPayload, "$['patient.identifier_type']");
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        PatientIdentifierType patientIdentifierType = StringUtils.isNotBlank(identifierTypeUuid) ?
                patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid) : defaultIdentifierType;
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patientIdentifier.setIdentifier(identifier);
        unsavedPatient.addIdentifier(patientIdentifier);
        Date birthdate = JsonUtils.readAsDate(patientPayload, "$['patient.birthdate']");
        boolean birthdateEstimated = JsonUtils.readAsBoolean(patientPayload, "$['patient.birthdate_estimated']");
        String gender = JsonUtils.readAsString(patientPayload, "$['patient.sex']");
        unsavedPatient.setBirthdate(birthdate);
        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
        unsavedPatient.setGender(gender);
        String givenName = JsonUtils.readAsString(patientPayload, "$['patient.given_name']");
        String middleName = JsonUtils.readAsString(patientPayload, "$['patient.middle_name']");
        String familyName = JsonUtils.readAsString(patientPayload, "$['patient.family_name']");
        PersonName personName = unsavedPatient.getPersonName();
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);
        String patientsFinger = JsonUtils.readAsString(patientPayload, "$['patient.finger']");
        String patientsFingerprint = JsonUtils.readAsString(patientPayload, "$['patient.fingerprint']");
        PersonService personService = Context.getPersonService();
        PersonAttributeType fingerprintAttributeType = personService.getPersonAttributeTypeByName("fingerprint");
        PersonAttributeType fingerAttributeType = personService.getPersonAttributeTypeByName("finger");
        PersonAttribute personAttributeFingerprint = unsavedPatient.getAttribute("fingerprint");
        PersonAttribute personAttributeFinger = unsavedPatient.getAttribute("finger");
        if (personAttributeFingerprint != null && personAttributeFinger != null) {
            personAttributeFinger.setAttributeType(fingerAttributeType);
            personAttributeFinger.setValue(patientsFinger);
            personAttributeFingerprint.setAttributeType(fingerprintAttributeType);
            personAttributeFingerprint.setValue(patientsFingerprint);
        } else if (fingerprintAttributeType != null && fingerAttributeType != null) {
            PersonAttribute fingerAttribute = new PersonAttribute();
            fingerAttribute.setAttributeType(fingerAttributeType);
            fingerAttribute.setValue(patientsFinger);
            PersonAttribute fingerprintAttribute = new PersonAttribute();
            fingerprintAttribute.setAttributeType(fingerprintAttributeType);
            fingerprintAttribute.setValue(patientsFingerprint);
            unsavedPatient.addAttribute(fingerprintAttribute);
        }
    }

    /**
     * 
     * @param queueData
     * @return
     */
    
    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}
