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
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.MuzimaForm;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Component
@Handler(supports = QueueData.class, order = 5)
public class JsonEncounterQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "json-encounter";

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private final Log log = LogFactory.getLog(JsonEncounterQueueDataHandler.class);

    private QueueProcessorException queueProcessorException;

    private Encounter encounter;

    @Override
    public boolean validate(QueueData queueData) {
        try {
            queueProcessorException = new QueueProcessorException();
            log.info("Processing encounter form data: " + queueData.getUuid());
            encounter = new Encounter();
            String payload = queueData.getPayload();

            //Object encounterObject = JsonUtils.readAsObject(queueData.getPayload(), "$['encounter']");
            processEncounter(encounter, payload);

            //Object patientObject = JsonUtils.readAsObject(queueData.getPayload(), "$['patient']");
            processPatient(encounter, payload);

            Object obsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['observation']");
            processObs(encounter, null, obsObject);

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

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {

        try {
            if (validate(queueData)) {
                Context.getEncounterService().saveEncounter(encounter);
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

    private void processPatient(final Encounter encounter, final Object patientObject) {
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
                locationService.getLocationByUuid(locationUuid) : encounter.getLocation();
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

        Patient candidatePatient;
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
        } else if (!StringUtils.isBlank(patientIdentifier.getIdentifier())) {
            List<Patient> patients = Context.getPatientService().getPatients(patientIdentifier.getIdentifier());
            candidatePatient = findPatient(patients, unsavedPatient);
        } else {
            List<Patient> patients = Context.getPatientService().getPatients(unsavedPatient.getPersonName().getFullName());
            candidatePatient = findPatient(patients, unsavedPatient);
        }

        if (candidatePatient == null) {
            queueProcessorException.addException(new Exception("Unable to uniquely identify patient for this encounter form data. "));
            //+ ToStringBuilder.reflectionToString(unsavedPatient)));
        } else {
            encounter.setPatient(candidatePatient);
        }
    }

    private Patient findPatient(final List<Patient> patients, final Patient unsavedPatient) {
        String unsavedGivenName = unsavedPatient.getGivenName();
        String unsavedFamilyName = unsavedPatient.getFamilyName();
        PersonName unsavedPersonName = unsavedPatient.getPersonName();
        for (Patient patient : patients) {
            // match it using the person name and gender, what about the dob?
            PersonName savedPersonName = patient.getPersonName();
            if (StringUtils.isNotBlank(savedPersonName.getFullName())
                    && StringUtils.isNotBlank(unsavedPersonName.getFullName())) {
                String savedGivenName = savedPersonName.getGivenName();
                int givenNameEditDistance = StringUtils.getLevenshteinDistance(
                        StringUtils.lowerCase(savedGivenName),
                        StringUtils.lowerCase(unsavedGivenName));
                String savedFamilyName = savedPersonName.getFamilyName();
                int familyNameEditDistance = StringUtils.getLevenshteinDistance(
                        StringUtils.lowerCase(savedFamilyName),
                        StringUtils.lowerCase(unsavedFamilyName));
                if (givenNameEditDistance < 3 && familyNameEditDistance < 3) {
                    if (StringUtils.equalsIgnoreCase(patient.getGender(), unsavedPatient.getGender())) {
                        if (patient.getBirthdate() != null && unsavedPatient.getBirthdate() != null
                                && DateUtils.isSameDay(patient.getBirthdate(), unsavedPatient.getBirthdate())) {
                            return patient;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void processObs(final Encounter encounter, final Obs parentObs, final Object obsObject) {
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
                        processObsObject(encounter, obsGroup, childObsObject);
                        if (parentObs != null) {
                            parentObs.addGroupMember(obsGroup);
                        }
                    } else {
                        Object valueObject = obsJsonObject.get(conceptQuestion);
                        if (valueObject instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) valueObject;
                            for (Object arrayElement : jsonArray) {
                                createObs(encounter, parentObs, concept, arrayElement);
                            }
                        } else {
                            createObs(encounter, parentObs, concept, valueObject);
                        }
                    }
                }
            }
        }else if(obsObject instanceof LinkedHashMap){
            Object obsAsJsonObject = new JSONObject((Map<String,?>)obsObject);
            processObs(encounter, parentObs, obsAsJsonObject);
        }
    }

    private void createObs(final Encounter encounter, final Obs parentObs, final Concept concept, final Object o) {
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
        // only add if the value is not empty :)
        encounter.addObs(obs);
        if (parentObs != null) {
            parentObs.addGroupMember(obs);
        }
    }

    private void processObsObject(final Encounter encounter, final Obs parentObs, final Object childObsObject) {
        //Object o = JsonUtils.readAsObject(childObsObject.toString(), "$");
        if (childObsObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) childObsObject;
            for (Object arrayElement : jsonArray) {
                Obs obsGroup = new Obs();
                obsGroup.setConcept(parentObs.getConcept());
                processObs(encounter, obsGroup, arrayElement);
                encounter.addObs(obsGroup);
            }
        } else if (childObsObject instanceof JSONObject) {
            processObs(encounter, parentObs, childObsObject);
            encounter.addObs(parentObs);
        }else if (childObsObject instanceof LinkedHashMap) {
            Object childObsAsJsonObject = new JSONObject((Map<String,?>)childObsObject);
            processObs(encounter, parentObs, childObsAsJsonObject);
            encounter.addObs(parentObs);
        }
    }

    private void processEncounter(final Encounter encounter, final Object encounterObject) throws QueueProcessorException {
        String encounterPayload = encounterObject.toString();

        String formUuid = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.form_uuid']");
        Form form = Context.getFormService().getFormByUuid(formUuid);
        if (form == null) {
            MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
            MuzimaForm muzimaForm = muzimaFormService.findByUniqueId(formUuid);
            if (muzimaForm != null) {
                Form formDefinition = Context.getFormService().getFormByUuid(muzimaForm.getForm());
                encounter.setForm(formDefinition);
                encounter.setEncounterType(formDefinition.getEncounterType());
            } else {
                log.info("Unable to find form using the uuid: " + formUuid + ". Setting the form field to null!");
                String encounterTypeString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.type_id']");
                int encounterTypeId = NumberUtils.toInt(encounterTypeString, -999);
                EncounterType encounterType = Context.getEncounterService().getEncounterType(encounterTypeId);
                if (encounterType == null) {
                    queueProcessorException.addException(new Exception("Unable to find encounter type using the id: " + encounterTypeString));
                } else {
                    encounter.setEncounterType(encounterType);
                }
            }
        } else {
            encounter.setForm(form);
            encounter.setEncounterType(form.getEncounterType());
        }

        String providerString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.provider_id']");
        User user = Context.getUserService().getUserByUsername(providerString);
        if (user == null) {
            queueProcessorException.addException(new Exception("Unable to find user using the id: " + providerString));
        } else {
            encounter.setCreator(user);
            encounter.setProvider(user);
        }

        String locationString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.location_id']");
        int locationId = NumberUtils.toInt(locationString, -999);
        Location location = Context.getLocationService().getLocation(locationId);
        if (location == null) {
            queueProcessorException.addException(new Exception("Unable to find encounter location using the id: " + locationString));
        } else {
            encounter.setLocation(location);
        }

        Date encounterDatetime = JsonUtils.readAsDate(encounterPayload, "$['encounter']['encounter.encounter_datetime']");
        encounter.setEncounterDatetime(encounterDatetime);
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

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}

