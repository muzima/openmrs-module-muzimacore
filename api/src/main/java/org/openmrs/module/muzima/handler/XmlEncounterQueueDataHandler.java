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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
@Component
@Handler(supports = QueueData.class, order = 2)
public class XmlEncounterQueueDataHandler implements QueueDataHandler {

    
    private static final String DISCRIMINATOR_VALUE = "xml-encounter";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(XmlEncounterQueueDataHandler.class);

    private QueueProcessorException queueProcessorException;

    private Encounter encounter;

    /**
     * 
     * @param queueData - QueueData
     * @throws QueueProcessorException
     */
    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {

        log.info("Processing registration form data: " + queueData.getUuid());
        encounter = new Encounter();
        try {
            if (validate(queueData)) {
                Context.getEncounterService().saveEncounter(encounter);
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
     * @param queueData - QueueData
     * @return boolean
     */
    @Override
    public boolean validate(QueueData queueData) {

        log.info("Processing encounter form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();

        String payload = queueData.getPayload();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(new ByteArrayInputStream(payload.getBytes("utf-8"))));

            Element element = document.getDocumentElement();
            element.normalize();

            // we need to get the form id to get the encounter type associated with this form from the form record.
            encounter.setEncounterType(Context.getEncounterService().getEncounterType(1));

            processPatient(encounter, document.getElementsByTagName("patient"));
            processEncounter(encounter, document.getElementsByTagName("encounter"));
            processObs(encounter, document.getElementsByTagName("obs"));
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

    /**
     * 
     * @return - String Discriminator
     */
    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    /**
     * 
     * @param encounter - Encounter
     * @param patientNodeList NodeList
     * @throws QueueProcessorException
     */
    private void processPatient(final Encounter encounter, final NodeList patientNodeList) throws QueueProcessorException {
        Node patientNode = patientNodeList.item(0);
        NodeList patientElementNodes = patientNode.getChildNodes();

        Patient unsavedPatient = new Patient();
        PersonName personName = new PersonName();
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        for (int i = 0; i < patientElementNodes.getLength(); i++) {
            Node patientElementNode = patientElementNodes.item(i);
            if (patientElementNode.getNodeType() == Node.ELEMENT_NODE) {
                Element patientElement = (Element) patientElementNode;
                if (patientElement.getTagName().equals("patient.middle_name")) {
                    personName.setMiddleName(patientElement.getTextContent());
                } else if (patientElement.getTagName().equals("patient.given_name")) {
                    personName.setGivenName(patientElement.getTextContent());
                } else if (patientElement.getTagName().equals("patient.family_name")) {
                    personName.setFamilyName(patientElement.getTextContent());
                } else if (patientElement.getTagName().equals("patient_identifier.identifier_type_id")) {
                    int identifierTypeId = Integer.parseInt(patientElement.getTextContent());
                    PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(identifierTypeId);
                    patientIdentifier.setIdentifierType(identifierType);
                } else if (patientElement.getTagName().equals("patient.medical_record_number")) {
                    patientIdentifier.setIdentifier(patientElement.getTextContent());
                } else if (patientElement.getTagName().equals("patient.sex")) {
                    unsavedPatient.setGender(patientElement.getTextContent());
                } else if (patientElement.getTagName().equals("patient.birthdate")) {
                    Date dob = parseDate(patientElement.getTextContent());
                    unsavedPatient.setBirthdate(dob);
                } else if (patientElement.getTagName().equals("patient.uuid")) {
                    unsavedPatient.setUuid(patientElement.getTextContent());
                }
            }
        }

        unsavedPatient.addName(personName);
        unsavedPatient.addIdentifier(patientIdentifier);

        Patient candidatePatient;
        if (StringUtils.isNotEmpty(unsavedPatient.getUuid())) {
            candidatePatient = Context.getPatientService().getPatientByUuid(unsavedPatient.getUuid());
            if (candidatePatient == null) {
                String temporaryUuid = unsavedPatient.getUuid();
                RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                candidatePatient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
            }
        } else if (!StringUtils.isBlank(patientIdentifier.getIdentifier())) {
            List<Patient> patients = Context.getPatientService().getPatients(patientIdentifier.getIdentifier());
            candidatePatient = PatientSearchUtils.findPatient(patients, unsavedPatient);
        } else {
            List<Patient> patients = Context.getPatientService().getPatients(unsavedPatient.getPersonName().getFullName());
            candidatePatient = PatientSearchUtils.findPatient(patients, unsavedPatient);
        }

        if (candidatePatient == null) {
            queueProcessorException.addException(new Exception("Unable to uniquely identify patient for this encounter form data. "
                    + ToStringBuilder.reflectionToString(unsavedPatient)));
        }

        encounter.setPatient(candidatePatient);
    }

    /**
     * 
     * @param name - String
     * @param node - Node
     * @return Node
     */
    private Node findSubNode(final String name, final Node node) {
        if (!node.hasChildNodes()) {
            return null;
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node subNode = list.item(i);
            if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                if (subNode.getNodeName().equals(name))
                    return subNode;
            }
        }
        return null;
    }

    /**
     * 
     * @param encounter - Encounter
     * @param obsNodeList - NodeList
     * @throws QueueProcessorException
     */
    private void processObs(final Encounter encounter, final NodeList obsNodeList) throws QueueProcessorException {
        Node obsNode = obsNodeList.item(0);
        NodeList obsElementNodes = obsNode.getChildNodes();
        for (int i = 0; i < obsElementNodes.getLength(); i++) {
            Node obsElementNode = obsElementNodes.item(i);
            // skip all top level obs nodes without child element or without attribute
            // no attribute: temporary elements
            // no child: element with no answer
            if (obsElementNode.hasAttributes() && obsElementNode.hasChildNodes()) {
                processObsNode(encounter, null, obsElementNode);
            }
        }
    }

    /**
     * 
     * @param encounter -Encounter
     * @param parentObs - Obs
     * @param obsElementNode -  Node
     */
    private void processObsNode(final Encounter encounter, final Obs parentObs, final Node obsElementNode) {
        Element obsElement = (Element) obsElementNode;
        String[] conceptElements = StringUtils.split(obsElement.getAttribute("concept"), "\\^");
        int conceptId = Integer.parseInt(conceptElements[0]);
        Concept concept = Context.getConceptService().getConcept(conceptId);

        if (concept == null) {
            log.info("Skipping obs creation, " + obsElement.getAttribute("concept") + " is not valid or not available.");
            return;
        }

        if (concept.isSet()) {
            Obs obsGroup = new Obs();
            obsGroup.setConcept(concept);
            obsGroup.setCreator(encounter.getCreator());
            NodeList nodeList = obsElementNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node subNode = nodeList.item(i);
                // only process sub node with attribute and it is a tag
                if (subNode.hasAttributes() && subNode.getNodeType() == Node.ELEMENT_NODE) {
                    // need to do recursive because we might have nested sets structure
                    encounter.addObs(obsGroup);
                    processObsNode(encounter, obsGroup, subNode);
                }
            }
        } else {
            Node valueNode = findSubNode("value", obsElementNode);
            if (valueNode != null) {
                String value = StringUtils.trim(valueNode.getTextContent());
                if (StringUtils.isNotEmpty(value)) {
                    Obs obs = new Obs();
                    obs.setConcept(concept);
                    obs.setEncounter(encounter);
                    obs.setPerson(encounter.getPatient());
                    obs.setObsDatetime(encounter.getEncounterDatetime());
                    obs.setLocation(encounter.getLocation());
                    obs.setCreator(encounter.getCreator());
                    // find the obs value :)
                    if (concept.getDatatype().isNumeric()) {
                        obs.setValueNumeric(Double.parseDouble(value));
                    } else if (concept.getDatatype().isDate()
                            || concept.getDatatype().isTime()
                            || concept.getDatatype().isDateTime()) {
                        obs.setValueDatetime(parseDate(value));
                    } else if (concept.getDatatype().isCoded()) {
                        String[] valueCodedElements = StringUtils.split(value, "\\^");
                        int valueCodedId = Integer.parseInt(valueCodedElements[0]);
                        Concept valueCoded = Context.getConceptService().getConcept(valueCodedId);
                        if (valueCoded == null) {
                            queueProcessorException.addException(new Exception("Unable to find concept for value coded with id: " + valueCodedId));
                        }
                        obs.setValueCoded(valueCoded);
                    } else if (concept.getDatatype().isText()) {
                        obs.setValueText(value);
                    }
                    // only add if the value is not empty :)
                    encounter.addObs(obs);
                    if (parentObs != null) {
                        parentObs.addGroupMember(obs);
                    }
                }
            } else {
                Node xformValuesNode = findSubNode("xforms_value", obsElementNode);
                if (xformValuesNode != null) {
                    String[] xformValues = StringUtils.split(StringUtils.trim(xformValuesNode.getTextContent()));
                    for (String xformValue : xformValues) {
                        Node xformValueNode = findSubNode(xformValue, obsElementNode);
                        if (xformValueNode != null && xformValueNode.hasAttributes()) {
                            Obs obs = new Obs();
                            obs.setConcept(concept);
                            obs.setEncounter(encounter);
                            obs.setPerson(encounter.getPatient());
                            obs.setObsDatetime(encounter.getEncounterDatetime());
                            obs.setLocation(encounter.getLocation());
                            obs.setCreator(encounter.getCreator());

                            Element xformValueElement = (Element) xformValueNode;
                            String[] valueCodedElements = StringUtils.split(xformValueElement.getAttribute("concept"), "\\^");
                            int valueCodedId = Integer.parseInt(valueCodedElements[0]);
                            Concept valueCoded = Context.getConceptService().getConcept(valueCodedId);
                            if (valueCoded == null) {
                                queueProcessorException.addException(new Exception("Unable to find concept for value coded with id: " + valueCodedId));
                            }
                            obs.setValueCoded(valueCoded);

                            encounter.addObs(obs);
                            if (parentObs != null) {
                                parentObs.addGroupMember(obs);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 
     * @param encounter - Encounter
     * @param encounterNodeList - NodeList
     * @throws QueueProcessorException
     */
    private void processEncounter(final Encounter encounter, final NodeList encounterNodeList) throws QueueProcessorException {
        Node encounterNode = encounterNodeList.item(0);
        NodeList encounterElementNodes = encounterNode.getChildNodes();
        for (int i = 0; i < encounterElementNodes.getLength(); i++) {
            Node encounterElementNode = encounterElementNodes.item(i);
            if (encounterElementNode.getNodeType() == Node.ELEMENT_NODE) {
                Element encounterElement = (Element) encounterElementNode;
                String encounterElementValue = encounterElement.getTextContent();
                if (encounterElement.getTagName().equals("encounter.encounter_datetime")) {
                    Date date = parseDate(encounterElementValue);
                    encounter.setEncounterDatetime(date);
                } else if (encounterElement.getTagName().equals("encounter.location_id")) {
                    int locationId = NumberUtils.toInt(encounterElementValue, -999);
                    Location location = Context.getLocationService().getLocation(locationId);
                    if (location == null) {
                        queueProcessorException.addException(new Exception("Unable to find encounter location using the id: " + encounterElementValue));
                    }
                    encounter.setLocation(location);
                } else if (encounterElement.getTagName().equals("encounter.provider_id")) {
                    User user = Context.getUserService().getUserByUsername(encounterElementValue);
                    if (user == null) {
                        queueProcessorException.addException(new Exception("Unable to find user using the id: " + encounterElementValue));
                    }
                    encounter.setProvider(user);
                    encounter.setCreator(user);
                } else if (encounterElement.getTagName().equals("encounter.form_uuid")) {
                    Form form = Context.getFormService().getFormByUuid(encounterElementValue);
                    if (form == null) {
                        MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
                        MuzimaForm muzimaForm = muzimaFormService.getFormByUuid(encounterElementValue);
                        if (muzimaForm != null) {
                            Form formDefinition = Context.getFormService().getFormByUuid(muzimaForm.getForm());
                            encounter.setForm(formDefinition);
                            encounter.setEncounterType(formDefinition.getEncounterType());
                        } else {
                            log.info("Unable to find form using the uuid: " + encounterElementValue + ". Setting the form field to null!");
                        }
                    } else {
                        encounter.setForm(form);
                        encounter.setEncounterType(form.getEncounterType());
                    }
                } else if (encounterElement.getTagName().equals("encounter.encounter_type")) {
                    if (encounter.getEncounterType() == null) {
                        int encounterTypeId = NumberUtils.toInt(encounterElementValue, -999);
                        EncounterType encounterType = Context.getEncounterService().getEncounterType(encounterTypeId);
                        if (encounterType == null) {
                            queueProcessorException.addException(new Exception("Unable to find encounter type using the id: " + encounterElementValue));
                        }
                        encounter.setEncounterType(encounterType);
                    }
                }
            }
        }
    }

    /**
     * 
     * @param dateValue - String representation of the date 
     * @return java.util.Date
     */
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
     * @param queueData -QueueData
     * @return boolean
     */
    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}
