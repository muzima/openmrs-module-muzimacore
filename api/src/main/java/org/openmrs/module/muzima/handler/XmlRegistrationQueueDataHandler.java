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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
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
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
@Handler(supports = QueueData.class, order = 1)
public class XmlRegistrationQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "xml-registration";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(XmlRegistrationQueueDataHandler.class);

    private PatientService patientService;

    private String temporaryPatientUuid;

    private QueueProcessorException queueProcessorException;

    private Patient unsavedPatient;

    /**
     * Implementation of how the queue data should be processed.
     *
     * @param queueData the queued data.
     * @should create new patient from well formed registration data
     * @should skip already processed registration data
     */
    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing registration form data: " + queueData.getUuid());

        try {
            if (validate(queueData)) {
                saveRegistrationData();
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

    private void saveRegistrationData() {

        RegistrationDataService registrationDataService = Context.getService(RegistrationDataService.class);
        RegistrationData registrationData;
        if (StringUtils.isNotEmpty(unsavedPatient.getUuid())) {
            registrationData = registrationDataService.getRegistrationDataByTemporaryUuid(getTemporaryPatientUuid());
            if (registrationData == null) {
                // we can't find registration data for this uuid, process the registration form.
                patientService = Context.getPatientService();

                Patient savedPatient = null;
                // check whether we already have similar patients!
                if (unsavedPatient.getNames().isEmpty()) {
                    PatientIdentifier identifier = unsavedPatient.getPatientIdentifier();
                    if (identifier != null) {
                        List<Patient> patients = patientService.getPatients(identifier.getIdentifier());
                        savedPatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
                    }
                } else {
                    PersonName personName = unsavedPatient.getPersonName();
                    List<Patient> patients = patientService.getPatients(personName.getFullName());
                    savedPatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
                }

                registrationData = new RegistrationData();
                registrationData.setTemporaryUuid(getTemporaryPatientUuid());
                String assignedUuid;
                // for a new patient we will create mapping:
                // * temporary uuid --> uuid of the newly created patient
                // for existing patient we will create mapping:
                // * temporary uuid --> uuid of the existing patient
                if (savedPatient != null) {
                    // if we have a patient already saved with the characteristic found in the registration form:
                    // * we will map the temporary uuid to the existing uuid.
                    assignedUuid = savedPatient.getUuid();
                } else {
                    patientService.savePatient(unsavedPatient);
                    assignedUuid = unsavedPatient.getUuid();
                }
                registrationData.setAssignedUuid(assignedUuid);
                registrationDataService.saveRegistrationData(registrationData);
            }
        }
    }

    /**
     *
     * @param queueData - QueueData
     * @return boolean
     */
    @Override
    public boolean validate(QueueData queueData) {
        log.info("validating registration form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();

        try {
            String payload = queueData.getPayload();
            unsavedPatient = createPatientFromPayload(payload);
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
     * Flag whether the current queue data handler can handle the queue data.
     *
     * @param queueData the queue data.
     * @return true when the handler can handle the queue data.
     */
    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }

    private Date parseDate(final String dateValue) {
        Date date = null;
        try {
            date = dateFormat.parse(dateValue);
        } catch (ParseException e) {
            log.error("Unable to parse date data for encounter!", e);
        }
        return date;
    }

    /**
     *
     * @param payload - String representation of the payload
     * @return Patient
     */
    private Patient createPatientFromPayload(final String payload) {
        Patient unsavedPatient = new Patient();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(new ByteArrayInputStream(payload.getBytes("utf-8"))));

            Element element = document.getDocumentElement();
            element.normalize();

            Node patientNode = document.getElementsByTagName("patient").item(0);
            NodeList patientElementNodes = patientNode.getChildNodes();

            PersonName personName = new PersonName();
            PatientIdentifier patientIdentifier = new PatientIdentifier();
            for (int i = 0; i < patientElementNodes.getLength(); i++) {
                Node patientElementNode = patientElementNodes.item(i);
                if (patientElementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element patientElement = (Element) patientElementNode;
                    String tagName = patientElement.getTagName();
                    if (tagName.equals("patient.middle_name")) {
                        personName.setMiddleName(patientElement.getTextContent());
                    } else if (tagName.equals("patient.given_name")) {
                        personName.setGivenName(patientElement.getTextContent());
                    } else if (tagName.equals("patient.family_name")) {
                        personName.setFamilyName(patientElement.getTextContent());
                    } else if (tagName.equals("patient_identifier.identifier_type_id")) {
                        int identifierTypeId = Integer.parseInt(patientElement.getTextContent());
                        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(identifierTypeId);
                        if (identifierType == null) {
                            queueProcessorException.addException(new Exception("Unable to find patient identifier type with id: " + identifierTypeId));
                        } else {
                            patientIdentifier.setIdentifierType(identifierType);
                        }
                    } else if (tagName.equals("patient.medical_record_number")) {
                        patientIdentifier.setIdentifier(patientElement.getTextContent());
                    } else if (tagName.equals("patient.sex")) {
                        unsavedPatient.setGender(patientElement.getTextContent());
                    } else if (tagName.equals("patient.birthdate")) {
                        Date dob = parseDate(patientElement.getTextContent());
                        unsavedPatient.setBirthdate(dob);
                    } else if (tagName.equals("patient.uuid")) {
                        unsavedPatient.setUuid(patientElement.getTextContent());
                        setTemporaryPatientUuid(patientElement.getTextContent());
                    } else if (tagName.equals("patient.finger")) {
                        savePatientsFinger(unsavedPatient, patientElement.getTextContent());
                    } else if (tagName.equals("patient.fingerprint")) {
                        savePatientsFingerprint(unsavedPatient, patientElement.getTextContent());
                    } else if (tagName.equals("amrs_medical_record_number_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "AMRS Medical Record Number");
                    } else if (tagName.equals("ccc_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "CCC Number ");
                    } else if (tagName.equals("hct_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "HCT ID");
                    } else if (tagName.equals("kni_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "KENYAN NATIONAL ID NUMBER");
                    } else if (tagName.equals("mtct_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "MTCT Plus ID");
                    } else if (tagName.equals("mtrh_hospital_number_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "MTRH Hospital Number");
                    } else if (tagName.equals("old_amrs_number_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "Old AMPATH Medical Record Number");
                    } else if (tagName.equals("pmtc_identifier_type")) {
                        extractIdentifier(unsavedPatient, patientElement, "pMTCT ID");
                    } else if (tagName.startsWith("person_attribute")) {
                        PersonService personService = Context.getPersonService();

                        int personAttributeTypeId = NumberUtils.toInt(tagName.replace("person_attribute", ""));
                        PersonAttributeType personAttributeType = personService.getPersonAttributeType(personAttributeTypeId);
                        if (personAttributeType == null) {
                            queueProcessorException.addException(new Exception("Unable to find attribute type with id: " + personAttributeTypeId));
                        } else {
                            PersonAttribute personAttribute = new PersonAttribute();
                            personAttribute.setAttributeType(personAttributeType);
                            personAttribute.setValue(patientElement.getTextContent());
                            unsavedPatient.addAttribute(personAttribute);
                        }
                    }
                }
            }

            Node encounterNode = document.getElementsByTagName("encounter").item(0);
            NodeList encounterElementNodes = encounterNode.getChildNodes();

            for (int i = 0; i < encounterElementNodes.getLength(); i++) {
                Node encounterElementNode = encounterElementNodes.item(i);
                if (encounterElementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element encounterElement = (Element) encounterElementNode;
                    if (encounterElement.getTagName().equals("encounter.location_id")) {
                        int locationId = Integer.parseInt(encounterElement.getTextContent());
                        Location location = Context.getLocationService().getLocation(locationId);
                        if (location == null) {
                            queueProcessorException.addException(new Exception("Unable to find location with id: " + locationId));
                        } else {
                            patientIdentifier.setLocation(location);
                        }
                        for (PatientIdentifier identifier : unsavedPatient.getIdentifiers()) {
                            identifier.setLocation(location);
                        }
                    }
                }
            }

            unsavedPatient.addName(personName);
            unsavedPatient.addIdentifier(patientIdentifier);
        } catch (ParserConfigurationException e) {
            queueProcessorException.addException(new Exception(e.getMessage()));
        } catch (SAXException e) {
            queueProcessorException.addException(new Exception(e.getMessage()));
        } catch (IOException e) {
            queueProcessorException.addException(new Exception(e.getMessage()));
        }
        return unsavedPatient;
    }

    /**
     * Sets initializes the TemporaryUuid 
     * 
     * @param temporaryUuid - String representation of the temporaryUuid
     */
    private void setTemporaryPatientUuid(String temporaryUuid) {
        this.temporaryPatientUuid = temporaryUuid;
    }

    private String getTemporaryPatientUuid() {
        return temporaryPatientUuid;
    }

    /**
     * 
     * @param unsavedPatient -Patient
     * @param patientElement - Element
     * @param typeName - String type name
     */
    private void extractIdentifier(final Patient unsavedPatient, final Element patientElement, final String typeName) {
        boolean identical = true;
        String identifierValue = StringUtils.EMPTY;
        NodeList identifierValueNodeList = patientElement.getChildNodes();
        for (int j = 0; j < identifierValueNodeList.getLength(); j++) {
            Node identifierValueNode = identifierValueNodeList.item(j);
            if (identifierValueNode.getNodeType() == Node.ELEMENT_NODE) {
                if (StringUtils.isEmpty(identifierValue)) {
                    identifierValue = identifierValueNode.getTextContent();
                } else {
                    if (!StringUtils.equalsIgnoreCase(identifierValue, identifierValueNode.getTextContent())) {
                        identical = false;
                        break;
                    }
                }
            }
        }
        if (identical && StringUtils.isNotEmpty(identifierValue)) {
            PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierTypeByName(typeName);
            if (identifierType != null) {
                PatientIdentifier patientIdentifier = new PatientIdentifier();
                patientIdentifier.setIdentifierType(identifierType);
                patientIdentifier.setIdentifier(identifierValue);
                unsavedPatient.addIdentifier(patientIdentifier);
            } else {
                queueProcessorException.addException(new Exception("Unable to find identifier type with name: " + typeName));
            }
        }
    }/**
     *
     * @param unsavedPatient -Patient
     * @param value -String
     */
    private void savePatientsFinger(final Patient unsavedPatient, final String value) {
        PersonService personService = Context.getPersonService();
        PersonAttributeType fingerAttributeType = personService.getPersonAttributeTypeByName("finger");
        PersonAttribute fingerAttribute = new PersonAttribute();
        fingerAttribute.setAttributeType(fingerAttributeType);
        fingerAttribute.setValue(value);
        unsavedPatient.addAttribute(fingerAttribute);
    }

    /**
     * 
     * @param unsavedPatient -Patient
     * @param value - String
     */
    private void savePatientsFingerprint(final Patient unsavedPatient, final String value) {
        PersonService personService = Context.getPersonService();
        PersonAttributeType fingerprintAttributeType = personService.getPersonAttributeTypeByName("fingerprint");
        PersonAttribute fingerprintAttribute = new PersonAttribute();
        fingerprintAttribute.setAttributeType(fingerprintAttributeType);
        fingerprintAttribute.setValue(value);
        unsavedPatient.addAttribute(fingerprintAttribute);

    }
}
