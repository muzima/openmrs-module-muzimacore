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
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static org.openmrs.module.muzima.utils.PersonCreationUtils.createPersonPayloadStubFromIndexPatientStub;

/**
 */
@Component
@Handler(supports = QueueData.class, order = 9)
public class ObsQueueDataHandler implements QueueDataHandler {

    public static final String DISCRIMINATOR_VALUE = "json-individual-obs";

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private final Log log = LogFactory.getLog(ObsQueueDataHandler.class);
    private QueueProcessorException queueProcessorException;
    private List<Obs> individualObsList;
    private boolean isPersonObs = false;
    private String deviceTimeZone;

    /**
     * 
     * @param queueData - QueueData
     * @throws QueueProcessorException
     */
    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing encounter form data: " + queueData.getUuid());
        try {
            if (validate(queueData)) {
                for(Obs individualObs: individualObsList) {
                    Context.getObsService().saveObs(individualObs, "Individual Obs");
                }

                Object indexObsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['index_obs']");
                Object indexPatientOsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['index_patient']");
                if (indexObsObject != null && indexPatientOsObject != null) {

                    JSONObject indexPatientPayload = new JSONObject();
                    JSONObject patientObject = createPersonPayloadStubFromIndexPatientStub(queueData.getPayload());
                    indexPatientPayload.put("patient",patientObject);
                    indexPatientPayload.put("observation",indexObsObject);
                    indexPatientPayload.put("encounter",JsonUtils.readAsObject(queueData.getPayload(), "$['encounter']"));


                    QueueData encounterQueueData = new QueueData();
                    encounterQueueData.setPayload(indexPatientPayload.toJSONString());
                    encounterQueueData.setDiscriminator("json-encounter");
                    encounterQueueData.setDataSource(queueData.getDataSource());
                    encounterQueueData.setCreator(queueData.getCreator());
                    encounterQueueData.setDateCreated(queueData.getDateCreated());
                    encounterQueueData.setUuid(UUID.randomUUID().toString());
                    encounterQueueData.setFormName(queueData.getFormName());
                    encounterQueueData.setLocation(queueData.getLocation());
                    encounterQueueData.setProvider(queueData.getProvider());
                    encounterQueueData.setPatientUuid(queueData.getPatientUuid());
                    encounterQueueData.setFormDataUuid(queueData.getFormDataUuid());
                    Context.getService(DataService.class).saveQueueData(encounterQueueData);
                }
            }
        } catch (Exception e) {
            if (!e.getClass().equals(QueueProcessorException.class))
                queueProcessorException.addException(e);
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
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }

    /**
     * 
     * @param queueData - QueueDate
     * @return boolean
     */
    @Override
    public boolean validate(QueueData queueData) {
        try {
            queueProcessorException = new QueueProcessorException();
            log.info("Processing encounter form data: " + queueData.getUuid());
            String payload = queueData.getPayload();
            individualObsList = new ArrayList();

            Object obsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['observation']");
            processObs(null, obsObject);

            String userString = JsonUtils.readAsString(payload, "$['encounter']['encounter.user_system_id']");
            User user = Context.getUserService().getUserByUsername(userString);
            if(user == null) {
                queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + userString));
            }

            deviceTimeZone = JsonUtils.readAsString(payload, "$['encounter']['encounter.device_time_zone']");
            Date encounterDatetime = JsonUtils.readAsDateTime(payload, "$['encounter']['encounter.encounter_datetime']",dateTimeFormat, deviceTimeZone);
            for(Obs obs:individualObsList){
                if(obs.getObsDatetime() == null){
                    obs.setObsDatetime(encounterDatetime);
                }
                obs.setCreator(user);
            }

            processPatient(payload);

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

    private void processObs(final Obs parentObs, final Object obsObject) {
        if (obsObject instanceof JSONObject) {
            JSONObject obsJsonObject = (JSONObject) obsObject;
            for (String conceptQuestion : obsJsonObject.keySet()) {
                String[] conceptElements = StringUtils.split(conceptQuestion, "\\^");
                if (conceptElements.length < 3)
                    continue;
                int conceptId = Integer.parseInt(conceptElements[0]);
                Concept concept = Context.getConceptService().getConcept(conceptId);
                if (concept == null) {
                    queueProcessorException.addException(new Exception("Unable to find Concept for Question with ID: " + conceptId));
                } else {
                    if (concept.isSet()) {
                        Obs obsGroup = new Obs();
                        obsGroup.setConcept(concept);
                        Object childObsObject = obsJsonObject.get(conceptQuestion);
                        processObsObject(obsGroup, childObsObject);
                        if (parentObs != null) {
                            parentObs.addGroupMember(obsGroup);
                        }
                    } else {
                        Object valueObject = obsJsonObject.get(conceptQuestion);
                        if (valueObject instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) valueObject;
                            for (Object arrayElement : jsonArray) {
                                createObs(parentObs, concept, arrayElement);
                            }
                        } else {
                            createObs(parentObs, concept, valueObject);
                        }
                    }
                }
            }
        }else if(obsObject instanceof LinkedHashMap){
            Object obsAsJsonObject = new JSONObject((Map<String,?>)obsObject);
            processObs(parentObs, obsAsJsonObject);
        }
    }

    /**
     * @param concept - Concept
     * @param o - java.lang.Object
     */
    private void createObs(final Obs parentObs, final Concept concept, final Object o) {
        String value=null;
        Obs obs = new Obs();
        obs.setConcept(concept);

        //check and parse if obs_value / obs_datetime object
        if(o instanceof LinkedHashMap){
            LinkedHashMap obj = (LinkedHashMap)o;
            if(obj.containsKey("obs_value")){
                value = (String)obj.get("obs_value");
            }
            if(obj.containsKey("obs_datetime")){
                String dateString = (String)obj.get("obs_datetime");
                Date obsDateTime = parseDate(dateString);
                obs.setObsDatetime(obsDateTime);
            }
        }else{
            value = o.toString();
        }
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
            } else {
                obs.setValueCoded(valueCoded);
            }
        } else if (concept.getDatatype().isText()) {
            obs.setValueText(value);
        }
        individualObsList.add(obs);
        if (parentObs != null) {
            parentObs.addGroupMember(obs);
        }
    }

    /**
     *
     * @param parentObs Obs
     * @param childObsObject - java.lang.Object
     */
    private void processObsObject(final Obs parentObs, final Object childObsObject) {
        //Object o = JsonUtils.readAsObject(childObsObject.toString(), "$");
        if (childObsObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) childObsObject;
            for (Object arrayElement : jsonArray) {
                Obs obsGroup = new Obs();
                obsGroup.setConcept(parentObs.getConcept());
                processObs(obsGroup, arrayElement);
                individualObsList.add(obsGroup);
            }
        } else if (childObsObject instanceof JSONObject) {
            processObs(parentObs, childObsObject);
            individualObsList.add(parentObs);
        }else if (childObsObject instanceof LinkedHashMap) {
            Object childObsAsJsonObject = new JSONObject((Map<String,?>)childObsObject);
            processObs(parentObs, childObsAsJsonObject);
            individualObsList.add(parentObs);
        }
    }
    /**
     * 
     * @return String
     */
    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    private void processPatient(final Object patientObject) {
        Patient unsavedPatient = new Patient();
        String patientPayload = patientObject.toString();

        String uuid = JsonUtils.readAsString(patientPayload, "$['patient']['patient.uuid']");
        unsavedPatient.setUuid(uuid);

        PatientService patientService = Context.getPatientService();
        LocationService locationService = Context.getLocationService();
        PatientIdentifierType defaultIdentifierType = patientService.getPatientIdentifierType(1);

        String identifier = JsonUtils.readAsString(patientPayload, "$['patient']['patient.medical_record_number']");
        String identifierTypeUuid = JsonUtils.readAsString(patientPayload, "$['patient']['patient.identifier_type']");
        String locationUuid = JsonUtils.readAsString(patientPayload, "$['patient']['patient.identifier_location']");

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        Location location = StringUtils.isNotBlank(locationUuid) ?
                locationService.getLocationByUuid(locationUuid) : null;
        patientIdentifier.setLocation(location);
        PatientIdentifierType patientIdentifierType = StringUtils.isNotBlank(identifierTypeUuid) ?
                patientService.getPatientIdentifierTypeByUuid(identifierTypeUuid) : defaultIdentifierType;
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patientIdentifier.setIdentifier(identifier);
        unsavedPatient.addIdentifier(patientIdentifier);

        Date birthdate = JsonUtils.readAsDate(patientPayload, "$['patient']['patient.birth_date']");
        boolean birthdateEstimated = JsonUtils.readAsBoolean(patientPayload, "$['patient']['patient.birthdate_estimated']");
        String gender = JsonUtils.readAsString(patientPayload, "$['patient']['patient.sex']");

        unsavedPatient.setBirthdate(birthdate);
        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
        unsavedPatient.setGender(gender);

        String givenName = JsonUtils.readAsString(patientPayload, "$['patient']['patient.given_name']");
        String middleName = JsonUtils.readAsString(patientPayload, "$['patient']['patient.middle_name']");
        String familyName = JsonUtils.readAsString(patientPayload, "$['patient']['patient.family_name']");

        PersonName personName = new PersonName();
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);

        unsavedPatient.addName(personName);
        unsavedPatient.addIdentifier(patientIdentifier);

        Patient candidatePatient = null;
        Person candidatePerson = null;
        if (StringUtils.isNotEmpty(unsavedPatient.getUuid())) {
            candidatePatient = Context.getPatientService().getPatientByUuid(unsavedPatient.getUuid());
            if (candidatePatient == null) {
                String temporaryUuid = unsavedPatient.getUuid();
                RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                if(registrationData!=null) {
                    candidatePatient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
                }
            }

            if(candidatePatient == null) {
                candidatePerson = Context.getPersonService().getPersonByUuid(unsavedPatient.getUuid());
                if (candidatePerson == null) {
                    String temporaryUuid = unsavedPatient.getUuid();
                    RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                    RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                    if (registrationData != null) {
                        candidatePerson = Context.getPersonService().getPersonByUuid(registrationData.getAssignedUuid());
                    }
                }
                if (candidatePerson != null) {
                    isPersonObs = true;
                    candidatePatient = new Patient();
                    candidatePatient.setId(candidatePerson.getId());
                }
            }
        } else if (!StringUtils.isBlank(patientIdentifier.getIdentifier())) {
            List<Patient> patients = Context.getPatientService().getPatients(patientIdentifier.getIdentifier());
            candidatePatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
        } else {
            List<Patient> patients = Context.getPatientService().getPatients(unsavedPatient.getPersonName().getFullName());
            candidatePatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
        }

        if (candidatePatient == null) {
            queueProcessorException.addException(new Exception("Unable to uniquely identify patient for this Individual Obs form data. "));
        } else {
            if(candidatePerson != null){
                for(Obs obs:individualObsList){
                    obs.setPerson(candidatePerson);
                }
            } else {
                for (Obs obs : individualObsList) {
                    obs.setPerson(candidatePatient);
                }
            }
        }
    }

    /**
     *
     * @param dateValue - String representation of the date value.
     * @return java.util.Date Object
     */
    private Date parseDate(final String dateValue) {
        Date date = null;
        try {
            String dateAsString = dateValue;
            if(deviceTimeZone != null) {
                if(dateValue.length()==10){
                    date = dateFormat.parse(dateAsString);
                } else {
                    dateTimeFormat.setTimeZone(TimeZone.getTimeZone(deviceTimeZone));
                    date = dateTimeFormat.parse(dateAsString);
                }
            } else {
                if(dateValue.length()==10){
                    date = dateFormat.parse(dateAsString);
                } else {
                    date = dateTimeFormat.parse(dateAsString);
                }
            }
        } catch (ParseException e) {
            log.error("Unable to parse date data for encounter!", e);
        }
        return date;
    }
}
