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

import net.minidev.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.muzima.utils.PatientSearchUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *  This Handler processes relationships received from {@link org.openmrs.module.muzima.model.DataSource} = "mobile"
 *  The handler will:
 *      <b>Create a new relationship between to persons based on their uuid</b>
 *      <b>Update a relationship between to persons based on their uuid</b>
 *      <b>Delete a relationship between to persons based on their uuid</b>
 *      <b>Throw an error to the {@link org.openmrs.module.muzima.model.ErrorData} where any of this fails</b>
 *
 * @author sthaiya
 */

@Handler(supports = QueueData.class, order = 1)
public class RelationshipQueueDataHandler implements QueueDataHandler {

    public static final String DISCRIMINATOR_VALUE = "json-relationship";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(RelationshipQueueDataHandler.class);

    private Person relatedPerson;
    private String payload;
    private QueueProcessorException queueProcessorException;

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing relationship data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            if (validate(queueData)) {
                registerUnsavedPatient();
            }
        } catch (Exception e) {
            /*Custom exception thrown by the validate function should not be added again into @queueProcessorException.
             It should add the runtime dao Exception while saving the data into @queueProcessorException collection */
            if (!e.getClass().equals(QueueProcessorException.class)) {
                queueProcessorException.addException(new Exception("Exception while processing relationship payload ",e));
            }
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    @Override
    public boolean validate(QueueData queueData) {
//        log.info("Processing relationship form data: " + queueData.getUuid());
//        queueProcessorException = new QueueProcessorException();
//        try {
//            payload = queueData.getPayload();
//            unsavedPatient = new Patient();
//            populateUnsavedPatientFromPayload();
//            validateUnsavedPatient();
//            return true;
//        } catch (Exception e) {
//            queueProcessorException.addException(new Exception("Exception while validating payload ",e));
//            return false;
//        } finally {
//            if (queueProcessorException.anyExceptions()) {
//                throw queueProcessorException;
//            }
//        }
        return true;
    }



    private void validateUnsavedPatient() {
        if(!JsonUtils.readAsBoolean(payload, "$['skipPatientMatching']")) {
//            Patient savedPatient = findSimilarSavedPatient();
//            if (savedPatient != null) {
//                queueProcessorException.addException(
//                        new Exception(
//                                "Found a patient with similar characteristic :  patientId = " + savedPatient.getPatientId()
//                                        + " Identifier Id = " + savedPatient.getPatientIdentifier().getIdentifier()
//                        )
//                );
//            }
        }
    }

    private void populateUnsavedPatientFromPayload() {
//        setPatientBirthDateFromPayload();
//        setPatientBirthDateEstimatedFromPayload();
//        setPatientGenderFromPayload();
//        setPatientNameFromPayload();
//        setUnsavedPatientCreatorFromPayload();
    }

    private void setPatientBirthDateFromPayload(){
//        Date birthDate = JsonUtils.readAsDate(payload, "$['patient']['patient.birth_date']");
//        unsavedPatient.setBirthdate(birthDate);
    }

    private void setPatientBirthDateEstimatedFromPayload(){
//        boolean birthdateEstimated = JsonUtils.readAsBoolean(payload, "$['patient']['patient.birthdate_estimated']");
//        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
    }

    private void setPatientGenderFromPayload(){
//        String gender = JsonUtils.readAsString(payload, "$['patient']['patient.sex']");
//        unsavedPatient.setGender(gender);
    }

    private void setPatientNameFromPayload(){
//        String givenName = JsonUtils.readAsString(payload, "$['patient']['patient.given_name']");
//        String familyName = JsonUtils.readAsString(payload, "$['patient']['patient.family_name']");
//        String middleName="";
//        try{
//            middleName= JsonUtils.readAsString(payload, "$['patient']['patient.middle_name']");
//        } catch(Exception e){
//            log.error(e);
//        }
//
//        PersonName personName = new PersonName();
//        personName.setGivenName(givenName);
//        personName.setMiddleName(middleName);
//        personName.setFamilyName(familyName);
//        unsavedPatient.addName(personName);
    }

    private void registerUnsavedPatient() {
//        RegistrationDataService registrationDataService = Context.getService(RegistrationDataService.class);
//        String temporaryUuid = getPatientUuidFromPayload();
//        RegistrationData registrationData = registrationDataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
//        if (registrationData == null) {
//            registrationData = new RegistrationData();
//            registrationData.setTemporaryUuid(temporaryUuid);
//            Context.getPatientService().savePatient(unsavedPatient);
//            String assignedUuid = unsavedPatient.getUuid();
//            registrationData.setAssignedUuid(assignedUuid);
//            registrationDataService.saveRegistrationData(registrationData);
//        }
    }

    private String getPatientUuidFromPayload(){
//        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
        return null;
    }

    private  void setUnsavedPatientCreatorFromPayload(){
//        String userString = JsonUtils.readAsString(payload, "$['encounter']['encounter.user_system_id']");
//        String providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");
//
//        User user = Context.getUserService().getUserByUsername(userString);
//        if (user == null) {
//            providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");
//            user = Context.getUserService().getUserByUsername(providerString);
//        }
//        if (user == null) {
//            queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + userString + " or Provider Id: "+providerString));
//        } else {
//            unsavedPatient.setCreator(user);
//        }
    }

    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}