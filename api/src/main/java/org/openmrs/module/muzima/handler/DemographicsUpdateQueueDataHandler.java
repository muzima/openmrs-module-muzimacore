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

import com.jayway.jsonpath.InvalidPathException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.muzima.utils.MuzimaSettingUtils;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static org.openmrs.module.muzima.utils.Constants.MuzimaSettings.DEMOGRAPHICS_UPDATE_MANUAL_REVIEW_SETTING_PROPERTY;
import static org.openmrs.module.muzima.utils.JsonUtils.getElementFromJsonObject;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.copyPersonAddress;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.createPersonPayloadStubForPerson;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAddressFromJsonObject;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAttributeFromJsonObject;

/**
 */
@Component
@Handler(supports = QueueData.class, order = 6)
public class DemographicsUpdateQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "json-demographics-update";

    private final Log log = LogFactory.getLog(DemographicsUpdateQueueDataHandler.class);

    private Patient unsavedPatient;
    private Patient savedPatient;
    private String payload;
    private QueueProcessorException queueProcessorException;

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing demographics update form data: " + queueData.getUuid());
        try {
            if (validate(queueData)) {
                if(isDemographicsUpdateStubDefined()) {
                    updateSavedPatientDemographics();
                    Context.getPatientService().savePatient(savedPatient);
                    String temporaryUuid = getTemporaryPatientUuidFromPayload();
                    if (StringUtils.isNotEmpty(temporaryUuid)) {
                        saveRegistrationData(temporaryUuid);
                    }
                }

                Object obsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['observation']");
                if (obsObject != null) {
                    //Recreate payload to reflect updated person demographics and eliminate index_patient obs, if any
                    JSONObject payload = new JSONObject();
                    payload.put("patient",createPersonPayloadStubForPerson(savedPatient));
                    payload.put("observation",obsObject);
                    payload.put("encounter",JsonUtils.readAsObject(queueData.getPayload(), "$['encounter']"));

                    QueueData encounterQueueData = new QueueData();
                    encounterQueueData.setPayload(payload.toJSONString());
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
            if (!e.getClass().equals(QueueProcessorException.class)) {
                queueProcessorException.addException(e);
            }
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    private String getTemporaryPatientUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.temporal_patient_uuid']");
    }

    private void saveRegistrationData(String temporaryUuid){
        RegistrationDataService registrationDataService = Context.getService(RegistrationDataService.class);
        RegistrationData registrationData = registrationDataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
        if (registrationData == null) {
            registrationData = new RegistrationData();
            registrationData.setTemporaryUuid(temporaryUuid);
            String assignedUuid = savedPatient.getUuid();
            registrationData.setAssignedUuid(assignedUuid);
            registrationDataService.saveRegistrationData(registrationData);
        }
    }

    private void updateSavedPatientDemographics(){
        if(unsavedPatient.getIdentifiers() != null){
            for(final PatientIdentifier identifier : unsavedPatient.getIdentifiers() ) {
                boolean identifierExists = false;
                for(PatientIdentifier savedPatientIdentifier: savedPatient.getIdentifiers()) {
                    if (savedPatientIdentifier.getIdentifierType().equals(identifier.getIdentifierType())
                            && savedPatientIdentifier.getIdentifier().equalsIgnoreCase(identifier.getIdentifier())) {
                        identifierExists = true;
                        break;
                    }
                }
                if(!identifierExists){
                    savedPatient.addIdentifier(identifier);
                }
            }
        }
        if(unsavedPatient.getPersonName() != null) {
            savedPatient.addName(unsavedPatient.getPersonName());
        }
        if(StringUtils.isNotBlank(unsavedPatient.getGender())) {
            savedPatient.setGender(unsavedPatient.getGender());
        }
        if(unsavedPatient.getBirthdate() != null) {
            savedPatient.setBirthdate(unsavedPatient.getBirthdate());
            savedPatient.setBirthdateEstimated(unsavedPatient.getBirthdateEstimated());
        }

        if(unsavedPatient.getAddresses() != null) {
            for(PersonAddress unsavedAddress:unsavedPatient.getAddresses()) {
                boolean savedAddressFound = false;

                if(StringUtils.isNotBlank(unsavedAddress.getUuid())) {
                    for (PersonAddress savedAddress : savedPatient.getAddresses()) {
                        if (StringUtils.equals(unsavedAddress.getUuid(), savedAddress.getUuid())) {
                            savedAddressFound = true;
                            try {
                                copyPersonAddress(unsavedAddress, savedAddress);
                            } catch (Exception e) {
                                queueProcessorException.addException(e);
                            }
                            break;
                        }
                    }
                }
                if(!savedAddressFound){
                    savedPatient.getAddresses().add(unsavedAddress);
                }
            }
        }
        if(unsavedPatient.getAttributes() != null) {
            Set<PersonAttribute> attributes = unsavedPatient.getAttributes();
            Iterator<PersonAttribute> iterator = attributes.iterator();
            while(iterator.hasNext()) {
                savedPatient.addAttribute(iterator.next());
            }
        }
        if(unsavedPatient.getChangedBy() != null) {
            savedPatient.setChangedBy(unsavedPatient.getChangedBy());
        }
    }

    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing demographics Update form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            payload = queueData.getPayload();
            Patient candidatePatient = getCandidatePatientFromPayload();
            savedPatient = PatientSearchUtils.findSavedPatient(candidatePatient,true);
            if(savedPatient == null){
                queueProcessorException.addException(new Exception("Unable to uniquely identify patient for this " +
                        "demographic update form data. "));
            } else {
                unsavedPatient = new Patient();
                populateUnsavedPatientDemographicsFromPayload();
            }
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

    private Patient getCandidatePatientFromPayload(){
        Patient candidatePatient = new Patient();

        String uuid = getCandidatePatientUuidFromPayload();
        candidatePatient.setUuid(uuid);

        PatientIdentifier medicalRecordNumber = getMedicalRecordNumberFromPayload();
        if(medicalRecordNumber != null) {
            medicalRecordNumber.setPreferred(true);
            candidatePatient.addIdentifier(medicalRecordNumber);
        }

        PersonName personName = getCandidatePatientPersonNameFromPayload();
        candidatePatient.addName(personName);

        String gender = getCandidatePatientGenderFromPayload();
        candidatePatient.setGender(gender);

        Date birthDate = getCandidatePatientBirthDateFromPayload();
        candidatePatient.setBirthdate(birthDate);

        return candidatePatient;
    }

    private String getCandidatePatientUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
    }

    private PatientIdentifier getMedicalRecordNumberFromPayload() {
        JSONObject medicalRecordNumberObject = (JSONObject) JsonUtils.readAsObject(payload, "$['patient']['patient.medical_record_number']");
        return createPatientIdentifier(medicalRecordNumberObject);
    }

    private PersonName getCandidatePatientPersonNameFromPayload(){
        PersonName personName = new PersonName();
        String givenName = JsonUtils.readAsString(payload, "$['patient']['patient.given_name']");
        if(StringUtils.isNotBlank(givenName)){
            personName.setGivenName(givenName);
        }
        String familyName = JsonUtils.readAsString(payload, "$['patient']['patient.family_name']");
        if(StringUtils.isNotBlank(familyName)){
            personName.setFamilyName(familyName);
        }

        String middleName= JsonUtils.readAsString(payload, "$['patient']['patient.middle_name']");
        if(StringUtils.isNotBlank(middleName)){
            personName.setMiddleName(middleName);
        }

        return personName;
    }

    private String getCandidatePatientGenderFromPayload(){
        return JsonUtils.readAsString(payload, "$['patient']['patient.sex']");
    }

    private Date getCandidatePatientBirthDateFromPayload(){
        return JsonUtils.readAsDate(payload, "$['patient']['patient.birth_date']");
    }

    private void populateUnsavedPatientDemographicsFromPayload() {
        if(isDemographicsUpdateStubDefined()) {
            setUnsavedPatientIdentifiersFromPayload();
            setUnsavedPatientBirthDateFromPayload();
            setUnsavedPatientBirthDateEstimatedFromPayload();
            setUnsavedPatientGenderFromPayload();
            setUnsavedPatientNameFromPayload();
            setUnsavedPatientAddressesFromPayload();
            setUnsavedPatientPersonAttributesFromPayload();
            setUnsavedPatientChangedByFromPayload();
        }
    }

    private boolean isDemographicsUpdateStubDefined(){
        return JsonUtils.containsKey(payload,"$['demographicsupdate']");
    }

    private void setUnsavedPatientIdentifiersFromPayload() {
        List<PatientIdentifier> demographicsUpdateIdentifiers = getDemographicsUpdatePatientIdentifiersFromPayload();
        if (!demographicsUpdateIdentifiers.isEmpty()) {
            Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
            patientIdentifiers.addAll(demographicsUpdateIdentifiers);
            setIdentifierTypeLocation(patientIdentifiers);
            unsavedPatient.addIdentifiers(patientIdentifiers);
        }
    }

    private List<PatientIdentifier> getDemographicsUpdatePatientIdentifiersFromPayload() {
        List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>();
        PatientIdentifier demographicsUpdateMedicalRecordNumberIdentifier = getDemographicsUpdateMedicalRecordNumberIdentifierFromPayload();
        if(demographicsUpdateMedicalRecordNumberIdentifier != null){
            identifiers.add(demographicsUpdateMedicalRecordNumberIdentifier);
        }

        identifiers.addAll(getOtherDemographicsUpdatePatientIdentifiersFromPayload());
        identifiers.addAll(getLegacyOtherDemographicsUpdatePatientIdentifiersFromPayload());
        return identifiers;
    }

    private PatientIdentifier getDemographicsUpdateMedicalRecordNumberIdentifierFromPayload(){
        PatientIdentifier medicalRecordNumber = null;
        Object medicalRecordNumberObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.medical_record_number']");
        if(medicalRecordNumberObject instanceof JSONObject) {
            medicalRecordNumber = createPatientIdentifier((JSONObject)medicalRecordNumberObject);
        } else if (medicalRecordNumberObject instanceof String){

            //process as legacy demographics update medical record number
            String medicalRecordNumberValueString = (String)medicalRecordNumberObject;
            if(StringUtils.isNotEmpty(medicalRecordNumberValueString)) {
                String identifierTypeName = "AMRS Universal ID";
                PatientIdentifier preferredPatientIdentifier = createPatientIdentifier(identifierTypeName, medicalRecordNumberValueString);
                if (preferredPatientIdentifier != null) {
                    preferredPatientIdentifier.setPreferred(true);
                    medicalRecordNumber = preferredPatientIdentifier;
                }
            }
        }
        return medicalRecordNumber;
    }

    private List<PatientIdentifier> getOtherDemographicsUpdatePatientIdentifiersFromPayload() {
        List<PatientIdentifier> otherIdentifiers = new ArrayList<PatientIdentifier>();
        try {
            Object otheridentifierObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.otheridentifier']");
            if (JsonUtils.isJSONArrayObject(otheridentifierObject)) {
                for (Object otherIdentifier : (JSONArray) otheridentifierObject) {
                    PatientIdentifier identifier = createPatientIdentifier((JSONObject) otherIdentifier);
                    if (identifier != null) {
                        otherIdentifiers.add(identifier);
                    }
                }
            } else {
                PatientIdentifier identifier = createPatientIdentifier((JSONObject) otheridentifierObject);
                if (identifier != null) {
                    otherIdentifiers.add(identifier);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.otheridentifier^")){
                    PatientIdentifier identifier = createPatientIdentifier((JSONObject) patientObject.get(key));
                    if (identifier != null) {
                        otherIdentifiers.add(identifier);
                    }
                }
            }
        } catch (InvalidPathException e) {
            log.error( "Error while parsing other identifiers ",e);
        }
        return otherIdentifiers;
    }

    private List<PatientIdentifier> getLegacyOtherDemographicsUpdatePatientIdentifiersFromPayload() {
        List<PatientIdentifier> legacyIdentifiers = new ArrayList<PatientIdentifier>();
        Object identifierTypeNameObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.other_identifier_type']");
        Object identifierValueObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.other_identifier_value']");

        if (identifierTypeNameObject instanceof JSONArray) {
            JSONArray identifierTypeName = (JSONArray) identifierTypeNameObject;
            JSONArray identifierValue = (JSONArray) identifierValueObject;
            for (int i = 0; i < identifierTypeName.size(); i++) {
                PatientIdentifier identifier = createPatientIdentifier(identifierTypeName.get(i).toString(),
                        identifierValue.get(i).toString());
                if (identifier != null) {
                    legacyIdentifiers.add(identifier);
                }
            }
        } else if (identifierTypeNameObject instanceof String) {
            String identifierTypeName = (String) identifierTypeNameObject;
            String identifierValue = (String) identifierValueObject;
            PatientIdentifier identifier = createPatientIdentifier(identifierTypeName, identifierValue);
            if (identifier != null) {
                legacyIdentifiers.add(identifier);
            }
        }

        return legacyIdentifiers;
    }

    private PatientIdentifier createPatientIdentifier(JSONObject identifierObject) {
        if(identifierObject == null){
            return null;
        }

        String identifierTypeName = (String) getElementFromJsonObject(identifierObject,"identifier_type_name");
        String identifierUuid = (String) getElementFromJsonObject(identifierObject,"identifier_type_uuid");
        String identifierValue = (String) getElementFromJsonObject(identifierObject,"identifier_value");

        return createPatientIdentifier(identifierUuid,identifierTypeName, identifierValue);
    }

    private PatientIdentifier createPatientIdentifier(String identifierTypeName, String identifierValue) {
        return createPatientIdentifier(null, identifierTypeName, identifierValue);
    }

    private PatientIdentifier createPatientIdentifier(String identifierTypeUuid, String identifierTypeName, String identifierValue) {
        if(StringUtils.isBlank(identifierTypeUuid) && StringUtils.isBlank(identifierTypeName)) {
            queueProcessorException.addException(
                    new Exception("Cannot create identifier. Identifier type name or uuid must be supplied"));
        }

        if(StringUtils.isBlank(identifierValue)) {
            queueProcessorException.addException(
                    new Exception("Cannot create identifier. Supplied identifier value is blank for identifier type name:'"
                            + identifierTypeName + "', uuid:'" + identifierTypeUuid + "'"));
        }

        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierTypeByUuid(identifierTypeUuid);
        if (identifierType == null) {
            identifierType = Context.getPatientService()
                    .getPatientIdentifierTypeByName(identifierTypeName);
        }
        if (identifierType == null) {
            queueProcessorException.addException(
                    new Exception("Unable to find identifier type with name:'"
                            + identifierTypeName + "', uuid:'" + identifierTypeUuid + "'"));
        } else {
            PatientIdentifier patientIdentifier = new PatientIdentifier();
            patientIdentifier.setIdentifierType(identifierType);
            patientIdentifier.setIdentifier(identifierValue);
            return patientIdentifier;
        }
        return null;
    }

    private void setIdentifierTypeLocation(final Set<PatientIdentifier> patientIdentifiers) {
        String locationIdString = JsonUtils.readAsString(payload, "$['encounter']['encounter.location_id']");
        Location location = null;
        int locationId;

        if (locationIdString != null) {
            locationId = Integer.parseInt(locationIdString);
            location = Context.getLocationService().getLocation(locationId);
        }

        if (location == null) {
            queueProcessorException.addException(
                    new Exception("Unable to find encounter location using the id: " + locationIdString));
        } else {
            Iterator<PatientIdentifier> iterator = patientIdentifiers.iterator();
            while (iterator.hasNext()) {
                PatientIdentifier identifier = iterator.next();
                identifier.setLocation(location);
            }
        }
    }

    private void setUnsavedPatientBirthDateFromPayload(){
        Date birthDate = JsonUtils.readAsDate(payload, "$['demographicsupdate']['demographicsupdate.birth_date']");
        if(birthDate != null){
            if(!isDemographicsUpdateManualReviewRequired() || isBirthDateChangeValidated()){
                unsavedPatient.setBirthdate(birthDate);
            }else{
                queueProcessorException.addException(
                        new Exception("Change of Birth Date requires manual review"));
            }
        }
    }

    private void setUnsavedPatientBirthDateEstimatedFromPayload(){
        boolean birthdateEstimated = JsonUtils.readAsBoolean(payload,
                "$['demographicsupdate']['demographicsupdate.birthdate_estimated']");
        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
    }

    private void setUnsavedPatientGenderFromPayload(){
        String gender = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.sex']");
        if(StringUtils.isNotBlank(gender)){
            if(!isDemographicsUpdateManualReviewRequired() || isGenderChangeValidated()){
                unsavedPatient.setGender(gender);
            }else{
                queueProcessorException.addException(
                        new Exception("Change of Gender requires manual review"));
            }
        }
    }

    private void setUnsavedPatientNameFromPayload(){
        PersonName personName = new PersonName();
        String givenName = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.given_name']");
        if(StringUtils.isNotBlank(givenName)){
            personName.setGivenName(givenName);
        }
        String familyName = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.family_name']");
        if(StringUtils.isNotBlank(familyName)){
            personName.setFamilyName(familyName);
        }

        String middleName= JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.middle_name']");
        if(StringUtils.isNotBlank(middleName)){
            personName.setMiddleName(middleName);
        }

        if(StringUtils.isNotBlank(personName.getFullName())) {
            unsavedPatient.addName(personName);
        }
    }

    private void setUnsavedPatientAddressesFromPayload() {
        Set<PersonAddress> addresses = new TreeSet<PersonAddress>();

        try {
            Object patientAddressObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.personaddress']");
            if (JsonUtils.isJSONArrayObject(patientAddressObject)) {
                for (Object personAddressJSONObject:(JSONArray) patientAddressObject) {
                    PersonAddress patientAddress = getPersonAddressFromJsonObject((JSONObject) personAddressJSONObject);
                    if(patientAddress != null){
                        addresses.add(patientAddress);
                    }
                }
            } else {
                PersonAddress patientAddress = getPersonAddressFromJsonObject((JSONObject) patientAddressObject);
                if(patientAddress != null){
                    addresses.add(patientAddress);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.personaddress^")){
                    PersonAddress patientAddress = getPersonAddressFromJsonObject((JSONObject) patientObject.get(key));
                    if(patientAddress != null){
                        addresses.add(patientAddress);
                    }
                }
            }

            PersonAddress legacyPersonAddress = getLegacyPatientAddressFromPayload();
            if(legacyPersonAddress != null){
                addresses.add(legacyPersonAddress);
            }

        } catch (InvalidPathException e) {
            log.error("Error while parsing person address", e);
        }

        if(!addresses.isEmpty()) {
            unsavedPatient.setAddresses(addresses);
        }
    }

    private PersonAddress getLegacyPatientAddressFromPayload(){
        PersonAddress personAddress = null;

        String county = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.county']");
        if(StringUtils.isNotEmpty(county)) {
            if(personAddress == null) personAddress = new PersonAddress();
            personAddress.setStateProvince(county);
        }

        String location = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.location']");
        if(StringUtils.isNotEmpty(location)) {
            if (personAddress == null) personAddress = new PersonAddress();
            personAddress.setAddress6(location);
        }

        String subLocation = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.sub_location']");
        if(StringUtils.isNotEmpty(subLocation)) {
            if (personAddress == null) personAddress = new PersonAddress();
            personAddress.setAddress5(subLocation);
        }

        String village = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.village']");
        if(StringUtils.isNotEmpty(village)) {
            if (personAddress == null) personAddress = new PersonAddress();
            personAddress.setCityVillage(village);
        }
        return personAddress;
    }

    private void setUnsavedPatientPersonAttributesFromPayload() {
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        try {
            Object patientAttributeObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.personattribute']");
            if (JsonUtils.isJSONArrayObject(patientAttributeObject)) {
                for (Object personAdttributeJSONObject:(JSONArray) patientAttributeObject) {
                    try {
                        PersonAttribute personAttribute = getPersonAttributeFromJsonObject((JSONObject) personAdttributeJSONObject);
                        if (personAttribute != null) {
                            attributes.add(personAttribute);
                        }
                    } catch (Exception e){
                        queueProcessorException.addException(e);
                    }
                }
            } else {
                try {
                    PersonAttribute personAttribute = getPersonAttributeFromJsonObject((JSONObject) patientAttributeObject);
                    if (personAttribute != null) {
                        attributes.add(personAttribute);
                    }
                } catch (Exception e){
                    queueProcessorException.addException(e);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.personattribute^")){
                    try {
                        PersonAttribute personAttribute = getPersonAttributeFromJsonObject((JSONObject) patientObject.get(key));
                        if (personAttribute != null) {
                            attributes.add(personAttribute);
                        }
                    } catch (Exception e){
                        queueProcessorException.addException(e);
                    }
                }
            }

            attributes.addAll(getLegacyPersonAttributes());
        } catch (InvalidPathException ex) {
            log.error("Error while parsing person attribute", ex);
        }

        if(!attributes.isEmpty()) {
            unsavedPatient.setAttributes(attributes);
        }
    }

    private Set<PersonAttribute> getLegacyPersonAttributes(){
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        String mothersName = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.mothers_name']");
        if(StringUtils.isNotEmpty(mothersName))
            attributes.add(createPersonAttribute("Mother's Name",null,mothersName));

        String phoneNumber = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.phone_number']");
        if(StringUtils.isNotEmpty(phoneNumber))
            attributes.add(createPersonAttribute("Contact Phone Number",null, phoneNumber));
        return attributes;
    }

    private PersonAttribute createPersonAttribute(String attributeTypeName, String attributeTypeUuid,String attributeValue){
        PersonService personService = Context.getPersonService();
        PersonAttributeType attributeType = null;

        if(StringUtils.isNotEmpty(attributeTypeUuid)){
            attributeType = personService.getPersonAttributeTypeByUuid(attributeTypeUuid);
        }

        if(attributeType == null){
            attributeType = personService.getPersonAttributeTypeByName(attributeTypeName);
        }

        if (attributeType == null) {
            queueProcessorException.addException(
                    new Exception("Unable to find Person Attribute Type by name: '" + attributeTypeName
                            + "' , uuid: '" +attributeTypeUuid + "'")
            );
        }

        PersonAttribute personAttribute = new PersonAttribute();
        personAttribute.setAttributeType(attributeType);
        personAttribute.setValue(attributeValue);
        return personAttribute;
    }

    private  void setUnsavedPatientChangedByFromPayload(){
        String userString = JsonUtils.readAsString(payload, "$['encounter']['encounter.user_system_id']");
        String providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");

        User user = Context.getUserService().getUserByUsername(userString);
        if (user == null) {
            providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");
            user = Context.getUserService().getUserByUsername(providerString);
        }
        if (user == null) {
            queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + userString + " or Provider Id: "+providerString));
        } else {
            unsavedPatient.setChangedBy(user);
        }
    }

    private boolean isBirthDateChangeValidated(){
        return JsonUtils.readAsBoolean(payload, "$['demographicsupdate']['demographicsupdate.birthdate_change_validated']");
    }

    private boolean isGenderChangeValidated(){
        return JsonUtils.readAsBoolean(payload, "$['demographicsupdate']['demographicsupdate.gender_change_validated']");
    }

    private boolean isDemographicsUpdateManualReviewRequired(){
        String activeSetupConfigUuid = JsonUtils.readAsString(payload, "$['encounter']['encounter.setup_config_uuid']");
        MuzimaSetting muzimaSetting = MuzimaSettingUtils.getMuzimaSetting(DEMOGRAPHICS_UPDATE_MANUAL_REVIEW_SETTING_PROPERTY,activeSetupConfigUuid);
        if(muzimaSetting != null){
            return muzimaSetting.getValueBoolean();
        }
        // Manual review is required by default
        return true;
    }

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}