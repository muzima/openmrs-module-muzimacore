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
import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
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
                assignToVisit(encounter);
                Context.getEncounterService().saveEncounter(encounter);
/*
                VisitService vs = Context.getVisitService();
                List<Visit> visits = vs.getActiveVisitsByPatient(encounter.getPatient());
                if(!visits.isEmpty()) {
                    for (Visit visit : visits) {
                        Date visitDate = visit.getStartDatetime();
                        Date encounterDate = encounter.getEncounterDatetime();
                        if ( dateFormat.format(visitDate).equals(dateFormat.format(encounterDate))) {
                            vs.saveVisit(visit);
                            setVisitOfEncounter(visit, encounter);
                            break;
                        }
                    }
                }
                else{
                    useNewVisit(encounter);
                }*/

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
            MuzimaForm muzimaForm = muzimaFormService.getFormByUuid(formUuid);
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

    protected static void useNewVisit(Encounter encounter) {
        String VISIT_SOURCE_FORM = "8bfab185-6947-4958-b7ab-dfafae1a3e3d";
        Visit visit = new Visit();
        visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(encounter.getEncounterDatetime()));
        visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime()));
        visit.setLocation(encounter.getLocation());
        visit.setPatient(encounter.getPatient());
        visit.setVisitType(Context.getVisitService().getVisitTypeByUuid("3371a4d4-f66f-4454-a86d-92c7b3da990c"));

        VisitAttribute sourceAttr = new VisitAttribute();
        sourceAttr.setAttributeType(Context.getVisitService().getVisitAttributeTypeByUuid(VISIT_SOURCE_FORM));
        sourceAttr.setOwner(visit);
        sourceAttr.setValue(encounter.getForm());
        visit.addAttribute(sourceAttr);

        Context.getVisitService().saveVisit(visit);

        setVisitOfEncounter(visit, encounter);
    }

    protected static void setVisitOfEncounter(Visit visit, Encounter encounter) {
        // Remove from old visit
        if (encounter.getVisit() != null) {
            encounter.getVisit().getEncounters().remove(encounter);
        }

        // Set to new visit
        encounter.setVisit(visit);

        if (visit != null) {
            visit.addEncounter(encounter);
        }
    }

    /**
     * Checks whether a date has any time value
     * @param date the date
     * @return true if the date has time
     * @should return true only if date has time
     */
    protected  boolean dateHasTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0 || cal.get(Calendar.MILLISECOND) != 0;
    }

    /**
     * Uses an existing visit for the given encounter
     * @param encounter the encounter
     * @return true if a suitable visit was found
     */
    protected boolean useExistingVisit(Encounter encounter) {
        // If encounter has time, then we need an exact fit for an existing visit
            List<Visit> visits = Context.getVisitService().getVisits(null, Collections.singletonList(encounter.getPatient()), null, null, null,
                    encounter.getEncounterDatetime(), null, null, null, true, false);

            for (Visit visit : visits) {
                // Skip visits which ended before the encounter date
                if (visit.getStopDatetime() != null && visit.getStopDatetime().before(encounter.getEncounterDatetime())) {
                    continue;
                }

                if (checkLocations(visit, encounter)) {
                    setVisitOfEncounter(visit, encounter);
                    return true;
                }
            }
        return false;
    }

    /**
     * Convenience method to check whether the location of a visit and an encounter are compatible
     * @param visit the visit
     * @param encounter the encounter
     * @return true if locations won't conflict
     */
    protected boolean checkLocations(Visit visit, Encounter encounter) {
        return visit.getLocation() == null || Location.isInHierarchy(encounter.getLocation(), visit.getLocation());
    }

    /**
     * Does the actual assignment of the encounter to a visit
     * @param encounter the encounter
     */
    protected void assignToVisit(Encounter encounter) {
        // Do nothing if the encounter already belongs to a visit and can't be moved
        if (encounter.getVisit() != null) {
            return;
        }

        // Try using an existing visit
        if (!useExistingVisit(encounter)) {
                useNewVisit(encounter);

        }
    }

}