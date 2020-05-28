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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.openmrs.module.muzima.utils.Constants.MuzimaSettings.DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY;

/**
 * TODO brief class description.
 */
@Component
@Handler(supports = QueueData.class, order = 5)
public class JsonEncounterQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "json-encounter";

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private final Log log = LogFactory.getLog(JsonEncounterQueueDataHandler.class);

    private static final String DEFAULT_ENCOUNTER_ROLE_UUID = "a0b03050-c99b-11e0-9572-0800200c9a66";

    private QueueProcessorException queueProcessorException;

    private Encounter encounter;

    /**
     * 
     * @param queueData
     * @return
     */
    @Override
    public boolean validate(QueueData queueData) {
        try {
            queueProcessorException = new QueueProcessorException();
            log.info("Processing encounter form data: " + queueData.getUuid());
            encounter = new Encounter();
            String payload = queueData.getPayload();

            //Object patientObject = JsonUtils.readAsObject(queueData.getPayload(), "$['patient']");
            processPatient(encounter, payload);

            //Object encounterObject = JsonUtils.readAsObject(queueData.getPayload(), "$['encounter']");
            processEncounter(encounter, payload);

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
                Set<Obs> obss =  encounter.getAllObs();
                boolean allObsValid = true;
                for(Obs obs:obss){
                    if(!isValidObs(obs)){
                        allObsValid = false;
                        queueProcessorException.addException(new Exception("Unable to process obs for concept with id: " + obs.getConcept().getConceptId()));
                    }

                }

                if(allObsValid) {
                    Context.getEncounterService().saveEncounter(encounter);
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
     * @param encounter
     * @param patientObject
     */
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
            candidatePatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
        } else {
            List<Patient> patients = Context.getPatientService().getPatients(unsavedPatient.getPersonName().getFullName());
            candidatePatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, unsavedPatient);
        }

        if (candidatePatient == null) {
            queueProcessorException.addException(new Exception("Unable to uniquely identify patient for this encounter form data. "));
            //+ ToStringBuilder.reflectionToString(unsavedPatient)));
        } else {
            encounter.setPatient(candidatePatient);
        }
    }

    /**
     * 
     * @param encounter - Encounter
     * @param parentObs - Obs
     * @param obsObject - Object
     */
    private void processObs(final Encounter encounter, final Obs parentObs, final Object obsObject) {
        if (obsObject instanceof JSONObject) {
            JSONObject obsJsonObject = (JSONObject) obsObject;
            for (String conceptQuestion : obsJsonObject.keySet()) {
                String[] conceptElements = StringUtils.split(conceptQuestion, "\\^");
                if (conceptElements.length < 3)
                    continue;
                Concept concept = getConceptByUuidOrId(conceptElements[0]);
                if (concept == null) {
                    queueProcessorException.addException(new Exception("Unable to find Concept for Question with ID: " + conceptElements[0]));
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

    /**
     * 
     * @param encounter - Encounter
     * @param parentObs - Obs
     * @param concept - Concept
     * @param o - java.lang.Object
     */
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
        }else if(o instanceof JSONObject){
            JSONObject obj = (JSONObject) o;
            if(obj.containsKey("obs_value")){
                value = (String)obj.get("obs_value");
            }
            if(obj.containsKey("obs_datetime")){
                String dateString = (String)obj.get("obs_datetime");
                Date obsDateTime = parseDate(dateString);
                obs.setObsDatetime(obsDateTime);
            }
        }
        else{
            value = o.toString();
        }
        // find the obs value :)
        if (concept.getDatatype().isNumeric()) {
            obs.setValueNumeric(Double.parseDouble(value));
        } else if (concept.getDatatype().isDate()
                || concept.getDatatype().isTime()
                || concept.getDatatype().isDateTime()) {
            obs.setValueDatetime(parseDate(value));
        } else if (concept.getDatatype().isCoded() || concept.getDatatype().isBoolean() ) {
            String[] valueCodedElements = StringUtils.split(value, "\\^");

            Concept valueCoded = null;
            if(valueCodedElements.length >= 3){
                valueCoded = getConceptByUuidOrId(valueCodedElements[0]);
            }

            if (valueCoded == null) {
                queueProcessorException.addException(new Exception("Unable to find concept for value coded with identifier: " + valueCodedElements[0]));
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

    private Concept getConceptByUuidOrId(String uuidOrId){
        Concept concept;
        if (StringUtils.isNumeric(uuidOrId)) {
            int conceptId = Integer.parseInt(uuidOrId);
            concept = Context.getConceptService().getConcept(conceptId);
        } else {
            concept = Context.getConceptService().getConceptByUuid(uuidOrId);
        }
        return concept;
    }

    /**
     * 
     * @param encounter - Encounter
     * @param parentObs Obs
     * @param childObsObject - java.lang.Object
     */
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

    /**
     * 
     * @param encounter - Encounter
     * @param encounterObject - java.lang.Object
     * @throws QueueProcessorException
     */
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

        String encounterRoleString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.provider_role_uuid']");
        EncounterRole encounterRole = null;

        if(StringUtils.isBlank(encounterRoleString)){
            encounterRole = Context.getEncounterService().getEncounterRoleByUuid(DEFAULT_ENCOUNTER_ROLE_UUID);
        } else {
            encounterRole = Context.getEncounterService().getEncounterRoleByUuid(encounterRoleString);
        }

        if(encounterRole == null){
            queueProcessorException.addException(new Exception("Unable to find encounter role using the uuid: ["
                    + encounterRoleString + "] or the default role [" + DEFAULT_ENCOUNTER_ROLE_UUID +"]"));
        }

        String providerString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.provider_id']");
        Provider provider = Context.getProviderService().getProviderByIdentifier(providerString);
        if (provider == null) {
            queueProcessorException.addException(new Exception("Unable to find provider using the id: " + providerString));
        } else {
            encounter.setProvider(encounterRole,provider);
        }

        String userString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.user_system_id']");
        User user = Context.getUserService().getUserByUsername(userString);

        if(user == null ){
            user = Context.getUserService().getUserByUsername(providerString);
        }
        if(user == null) {
            queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + userString + " or Provider Id: "+providerString));
        } else {
            encounter.setCreator(user);
        }

        String locationString = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.location_id']");
        int locationId = NumberUtils.toInt(locationString, -999);
        Location location = Context.getLocationService().getLocation(locationId);
        if (location == null) {
            queueProcessorException.addException(new Exception("Unable to find encounter location using the id: " + locationString));
        } else {
            encounter.setLocation(location);
        }

        String jsonPayloadTimezone = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.device_time_zone']");
        Date encounterDatetime = JsonUtils.readAsDateTime(encounterPayload, "$['encounter']['encounter.encounter_datetime']",dateTimeFormat,jsonPayloadTimezone);
        encounter.setEncounterDatetime(encounterDatetime);

        VisitService visitService = Context.getService(VisitService.class);
        List<Visit> patientVisit =  visitService.getVisitsByPatient(encounter.getPatient(),true,false);
        Visit encounterVisit = null;
        Collections.sort(patientVisit, visitDateTimeComparator);
        for(Visit visit:patientVisit){
            if(visit.getStopDatetime() == null){
                if(encounterDatetime.compareTo(visit.getStartDatetime())>=0){
                    encounterVisit = visit;
                    break;
                }
            } else if (encounterDatetime.compareTo(visit.getStartDatetime())>=0 && (encounterDatetime.compareTo(visit.getStopDatetime())<=0)){
                encounterVisit = visit;
                break;
            }
        }

        if(encounterVisit == null) {
            MuzimaSetting defaultMuzimaVisitTypeSetting = getDefaultMuzimaVisitType(encounterPayload);
            String defaultMuzimaVisitTypeUuid = "";
            if (defaultMuzimaVisitTypeSetting != null) {
                defaultMuzimaVisitTypeUuid = defaultMuzimaVisitTypeSetting.getValueString();
                if (!defaultMuzimaVisitTypeUuid.isEmpty()) {
                    VisitType visitType = visitService.getVisitTypeByUuid(defaultMuzimaVisitTypeUuid);
                    if (visitType != null) {
                        String uuid = UUID.randomUUID().toString();
                        Calendar encounterDate = Calendar.getInstance();
                        encounterDate.setTime(encounterDatetime);
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.YEAR,encounterDate.get(Calendar.YEAR));
                        startTime.set(Calendar.MONDAY,encounterDate.get(Calendar.MONTH));
                        startTime.set(Calendar.DATE,encounterDate.get(Calendar.DATE));
                        startTime.set(Calendar.HOUR_OF_DAY,0);
                        startTime.set(Calendar.MINUTE,0);
                        startTime.set(Calendar.SECOND,0);

                        Calendar endTime = Calendar.getInstance();
                        endTime.set(Calendar.YEAR,encounterDate.get(Calendar.YEAR));
                        endTime.set(Calendar.MONDAY,encounterDate.get(Calendar.MONTH));
                        endTime.set(Calendar.DATE,encounterDate.get(Calendar.DATE));
                        endTime.set(Calendar.HOUR_OF_DAY,23);
                        endTime.set(Calendar.MINUTE,59);
                        endTime.set(Calendar.SECOND,59);

                        Visit visit = new Visit();
                        visit.setPatient(encounter.getPatient());
                        visit.setVisitType(visitType);
                        visit.setStartDatetime(startTime.getTime());
                        visit.setStopDatetime(endTime.getTime());
                        visit.setCreator(user);
                        visit.setDateCreated(new Date());
                        visit.setUuid(uuid);
                        visitService.saveVisit(visit);
                        encounterVisit = visitService.getVisitByUuid(uuid);
                    } else {
                        queueProcessorException.addException(new Exception("Unable to find default visit type with uuid " + defaultMuzimaVisitTypeUuid));
                    }
                } else {
                    queueProcessorException.addException(new Exception("Unable to find default visit type. Default visit type setting not set. "));
                }

            } else {
                queueProcessorException.addException(new Exception("Unable to find default visit type. Default visit type setting not set. "));
            }
        }

        encounter.setVisit(encounterVisit);
    }

    private final Comparator<Visit> visitDateTimeComparator = new Comparator<Visit>() {
        @Override
        public int compare(Visit lhs, Visit rhs) {
            return -lhs.getStartDatetime().compareTo(rhs.getStartDatetime());
        }
    };

    /**
     * 
     * @param dateValue - String representation of the date value.
     * @return java.util.Date Object
     */
    private Date parseDate(final String dateValue) {
        Date date = null;
        if(StringUtils.isNumeric(dateValue))
        {
            long timestamp = Long.parseLong(dateValue);
            date = new Date(timestamp);
        }else {
            try {
                date = dateFormat.parse(dateValue);
            } catch (ParseException e) {
                log.error("Unable to parse date data for encounter!", e);
            }
        }
        return date;
    }

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }

    /**
     * Checks if all obs have concepts with valid datatype.
     * @param obs
     * @return boolean
     */
    public boolean isValidObs(Obs obs){
        return (obs.getConcept().getDatatype().isBoolean() || obs.getConcept().getDatatype().isNumeric() || obs.getConcept().getDatatype().isDate() || obs.getConcept().getDatatype().isTime() || obs.getConcept().getDatatype().isDateTime() || obs.getConcept().getDatatype().isCoded() || obs.getConcept().getDatatype().isText() || (obs.getConcept().isSet() && obs.isObsGrouping()));
    }

    public MuzimaSetting getDefaultMuzimaVisitType(String encounterPayload){
        MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting defaultMuzimaVisitTypeSetting = null;
        String activeSetupConfigUuid = JsonUtils.readAsString(encounterPayload, "$['encounter']['encounter.setup_config_uuid']");
        if(StringUtils.isNotBlank(activeSetupConfigUuid)){
            MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
            MuzimaConfig config = configService.getConfigByUuid(activeSetupConfigUuid);
            if(config != null){
                defaultMuzimaVisitTypeSetting = config.getConfigMuzimaSettingByProperty(DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY);
                if(defaultMuzimaVisitTypeSetting == null){
                    defaultMuzimaVisitTypeSetting = settingService.getMuzimaSettingByProperty(DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY);
                }
            } else {
                defaultMuzimaVisitTypeSetting = settingService.getMuzimaSettingByProperty(DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY);
            }
        }else{
            defaultMuzimaVisitTypeSetting = settingService.getMuzimaSettingByProperty(DEFAULT_MUZIMA_VISIT_TYPE_SETTING_PROPERTY);
        }

        return defaultMuzimaVisitTypeSetting;
    }
}
