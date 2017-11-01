/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.handler;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAddress;
import org.openmrs.annotation.Handler;
import com.jayway.jsonpath.InvalidPathException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.PersonName;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.Constants;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.PatientIdentifier;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.PatientIdentifierType;
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
 * TODO: Write brief description about the class here.
 */
@Handler(supports = QueueData.class, order = 1)
public class JsonRegistrationQueueDataHandler implements QueueDataHandler {

    private static final String TAG = "JsonRegistrationQueueDataHandler";
    private static final String DISCRIMINATOR_VALUE = "json-registration";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(JsonRegistrationQueueDataHandler.class);

    private Patient unsavedPatient;
    private String payload;
    Set<PersonAttribute> personAttributes;
    private QueueProcessorException queueProcessorException;

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing registration form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            if (validate(queueData)) {
                registerUnsavedPatient();
            }
        } catch (Exception e) {
            /*Custom exception thrown by the validate function should not be added again into @queueProcessorException.
             It should add the runtime dao Exception while saving the data into @queueProcessorException collection */
            if (!e.getClass().equals(QueueProcessorException.class)) {
                queueProcessorException.addException(e);
            }
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing registration form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            payload = queueData.getPayload();
            unsavedPatient = new Patient();
            populateUnsavedPatientFromPayload();
            validateUnsavedPatient();
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

    private void validateUnsavedPatient() {
        Patient savedPatient = findSimilarSavedPatient();
        if (savedPatient != null) {
            queueProcessorException.addException(
                new Exception(
                        "Found a patient with similar characteristic :  patientId = " + savedPatient.getPatientId()
                                + " Identifier Id = " + savedPatient.getPatientIdentifier().getIdentifier()
                )
            );
        }
    }

    private void populateUnsavedPatientFromPayload() {
        setPatientIdentifiersFromPayload();
        setPatientBirthDateFromPayload();
        setPatientBirthDateEstimatedFromPayload();
        setPatientGenderFromPayload();
        setPatientNameFromPayload();
        setPatientAddressesFromPayload();
        setPersonAttributesFromPayload();
    }

    private void setPatientIdentifiersFromPayload() {
        Set<PatientIdentifier> patientIdentifiers = new TreeSet<PatientIdentifier>();

        //get setting for identifier autogeneration
        MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting autogenerationSetting = settingService.getMuzimaSettingByProperty(
                Constants.MuzimaSettings.PATIENT_IDENTIFIER_AUTOGENERATTION_SETTING_PROPERTY);
        boolean shouldAutogenerateIdentifier = (autogenerationSetting != null & autogenerationSetting.getValueBoolean()
                || Constants.MuzimaSettings.PATIENT_IDENTIFIER_AUTOGENERATTION_SETTING_DEFAULT_VALUE);

        PatientIdentifier preferredIdentifier;
        if(shouldAutogenerateIdentifier){
            preferredIdentifier = getAutogeneratedIdentifier();
            PatientIdentifier medicalRecordNumberFromPayload = getMedicalRecordNumberFromPayload();
            if(medicalRecordNumberFromPayload != null){
                patientIdentifiers.add(medicalRecordNumberFromPayload);
            }
        } else {
            preferredIdentifier = getMedicalRecordNumberFromPayload();
            if(preferredIdentifier == null){
                queueProcessorException.addException(
                        new Exception("Could not retrieve medical record number from payload"));
            }
        }
        if (preferredIdentifier != null) {
            preferredIdentifier.setPreferred(true);
            patientIdentifiers.add(preferredIdentifier);
        }

        List<PatientIdentifier> otherIdentifiers = getOtherPatientIdentifiersFromPayload();
        if (!otherIdentifiers.isEmpty()) {
            patientIdentifiers.addAll(otherIdentifiers);
        }
        setIdentifierTypeLocation(patientIdentifiers);
        unsavedPatient.setIdentifiers(patientIdentifiers);
    }

    private PatientIdentifier getAutogeneratedIdentifier(){
        MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting autoGenerationSourceSetting = settingService.getMuzimaSettingByProperty(
                Constants.MuzimaSettings.PATIENT_IDENTIFIER_AUTOGENERATTION_SOURCE_NAME);
        if(autoGenerationSourceSetting == null){
            queueProcessorException.addException(
                new Exception("Could not auto-generate patient identifier. mUzima setting for idgen source is not defined.")
            );
            return null;
        }

        try {
            IdentifierSourceService sourceService = Context.getService(IdentifierSourceService.class);
            if (sourceService != null) {
                String autoGenerationSource = autoGenerationSourceSetting.getValueString();
                List<IdentifierSource> sources = sourceService.getAllIdentifierSources(false);
                IdentifierSource source = null;
                for (IdentifierSource s : sources) {
                    if (StringUtils.equals(s.getName(), autoGenerationSource)) {
                        source = s;
                    }
                }
                if (source == null) {
                    queueProcessorException.addException(
                            new Exception("Could not auto-generate patient identifier." +
                                    " Could not find idgen source with name: "+ autoGenerationSource)
                    );
                    return null;
                }
                PatientIdentifierType identifierType = source.getIdentifierType();
                String identifierValue = sourceService.generateIdentifier(source, "mUzima registration");
                PatientIdentifier identifier = new PatientIdentifier();
                identifier.setIdentifierType(identifierType);
                identifier.setIdentifier(identifierValue);
                return identifier;
            }
        }catch (NoClassDefFoundError e){
            queueProcessorException.addException(
                    new Exception("Could not auto-generate patient identifier. Idgen module is not running.")
            );
        }
        return null;
    }

    private PatientIdentifier getMedicalRecordNumberFromPayload() {
        JSONObject medicalRecordNumberObject = (JSONObject) JsonUtils.readAsObject(payload, "$['patient']['patient.medical_record_number']");
        return createPatientIdentifier(medicalRecordNumberObject);
    }

    private List<PatientIdentifier> getOtherPatientIdentifiersFromPayload() {
        List<PatientIdentifier> otherIdentifiers = new ArrayList<PatientIdentifier>();
        try {
            Object otheridentifierObject = JsonUtils.readAsObject(payload, "$['patient']['patient.otheridentifier']");
            if (JsonUtils.isPathAJSONArray(otheridentifierObject)) {
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

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['patient']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("patient.otheridentifier^")){
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

    private PatientIdentifier createPatientIdentifier(JSONObject identifierObject) {
        if(identifierObject == null){
            return null;
        }

        String identifierTypeName = (String) getElementFromJsonObject(identifierObject,"identifier_type_name");
        String identifierUuid = (String) getElementFromJsonObject(identifierObject,"identifier_type_uuid");
        String identifierValue = (String) getElementFromJsonObject(identifierObject,"identifier_value");

        return createPatientIdentifier(identifierUuid,identifierTypeName, identifierValue);
    }

    private PatientIdentifier createPatientIdentifier(String identifierTypeUuid, String identifierTypeName, String identifierValue) {
        if(StringUtils.isBlank(identifierTypeUuid) && StringUtils.isBlank(identifierTypeName)) {
            queueProcessorException.addException(
                    new Exception("Cannot create identifier. Identifier type name or uuid must be supplied"));
        }

        if(StringUtils.isBlank(identifierTypeUuid)) {
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

    private void setPatientBirthDateFromPayload() {
        Date birthDate = JsonUtils.readAsDate(payload, "$['patient']['patient.birth_date']");
        unsavedPatient.setBirthdate(birthDate);
    }

    private void setPatientBirthDateEstimatedFromPayload() {
        boolean birthdateEstimated = JsonUtils.readAsBoolean(payload, "$['patient']['patient.birthdate_estimated']");
        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
    }

    private void setPatientGenderFromPayload() {
        String gender = JsonUtils.readAsString(payload, "$['patient']['patient.sex']");
        unsavedPatient.setGender(gender);
    }

    private void setPatientNameFromPayload() {
        String givenName = JsonUtils.readAsString(payload, "$['patient']['patient.given_name']");
        String familyName = JsonUtils.readAsString(payload, "$['patient']['patient.family_name']");
        String middleName = "";
        try {
            middleName = JsonUtils.readAsString(payload, "$['patient']['patient.middle_name']");
        } catch (Exception e) {
            log.error(e);
        }

        PersonName personName = new PersonName();
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);
        unsavedPatient.addName(personName);
    }

    private void registerUnsavedPatient() {
        RegistrationDataService registrationDataService = Context.getService(RegistrationDataService.class);
        String temporaryUuid = getPatientUuidFromPayload();
        RegistrationData registrationData = registrationDataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
        if (registrationData == null) {
            registrationData = new RegistrationData();
            registrationData.setTemporaryUuid(temporaryUuid);
            Context.getPatientService().savePatient(unsavedPatient);
            String assignedUuid = unsavedPatient.getUuid();
            registrationData.setAssignedUuid(assignedUuid);
            registrationDataService.saveRegistrationData(registrationData);
        }
    }

    private String getPatientUuidFromPayload() {
        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
    }

    private void setPatientAddressesFromPayload() {
        Set<PersonAddress> addresses = new TreeSet<PersonAddress>();

        try {
            Object patientAddressObject = JsonUtils.readAsObject(payload, "$['patient']['patient.personaddress']");
            if (JsonUtils.isPathAJSONArray(patientAddressObject)) {
                for (Object personAddressJSONObject:(JSONArray) patientAddressObject) {
                    PersonAddress patientAddress = getPatientAddressFromJsonObject((JSONObject) personAddressJSONObject);
                    if(patientAddress != null){
                        addresses.add(patientAddress);
                    }
                }
            } else {
                PersonAddress patientAddress = getPatientAddressFromJsonObject((JSONObject) patientAddressObject);
                if(patientAddress != null){
                    addresses.add(patientAddress);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['patient']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("patient.personaddress^")){
                    PersonAddress patientAddress = getPatientAddressFromJsonObject((JSONObject) patientObject.get(key));
                    if(patientAddress != null){
                        addresses.add(patientAddress);
                    }
                }
            }
        } catch (InvalidPathException e) {
            log.error("Error while parsing person address", e);
        }

        if(!addresses.isEmpty()) {
            unsavedPatient.setAddresses(addresses);
        }

    }

    private PersonAddress getPatientAddressFromJsonObject(JSONObject addressJsonObject){
        if(addressJsonObject == null){
            return null;
        }
        PersonAddress patientAddress = new PersonAddress();
        patientAddress.setAddress1((String)getElementFromJsonObject(addressJsonObject,"address1"));
        patientAddress.setAddress2((String)getElementFromJsonObject(addressJsonObject,"address2"));
        patientAddress.setAddress3((String)getElementFromJsonObject(addressJsonObject,"address3"));
        patientAddress.setAddress4((String)getElementFromJsonObject(addressJsonObject,"address4"));
        patientAddress.setAddress5((String)getElementFromJsonObject(addressJsonObject,"address5"));
        patientAddress.setAddress6((String)getElementFromJsonObject(addressJsonObject,"address6"));
        patientAddress.setCityVillage((String)getElementFromJsonObject(addressJsonObject,"cityVillage"));
        patientAddress.setCountyDistrict((String)getElementFromJsonObject(addressJsonObject,"countyDistrict"));
        patientAddress.setCountry((String)getElementFromJsonObject(addressJsonObject,"country"));
        patientAddress.setPostalCode((String)getElementFromJsonObject(addressJsonObject,"postalCode"));
        patientAddress.setLatitude((String)getElementFromJsonObject(addressJsonObject,"latitude"));
        patientAddress.setLongitude((String)getElementFromJsonObject(addressJsonObject,"longitude"));
        patientAddress.setStartDate((Date) getElementFromJsonObject(addressJsonObject,"startDate"));
        patientAddress.setEndDate((Date) getElementFromJsonObject(addressJsonObject,"endDate"));
        patientAddress.setPreferred((Boolean) getElementFromJsonObject(addressJsonObject,"preferred"));

        if(patientAddress.isBlank()){
            return null;
        } else {
            return patientAddress;
        }
    }

    private void setPersonAttributesFromPayload() {
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        try {
            Object patientAttributeObject = JsonUtils.readAsObject(payload, "$['patient']['patient.personattribute']");
            if (JsonUtils.isPathAJSONArray(patientAttributeObject)) {
                for (Object personAdttributeJSONObject:(JSONArray) patientAttributeObject) {
                    PersonAttribute personAttribute = getPatientAdttributeFromJsonObject((JSONObject) personAdttributeJSONObject);
                    if(personAttribute != null){
                        attributes.add(personAttribute);
                    }
                }
            } else {
                PersonAttribute personAttribute = getPatientAdttributeFromJsonObject((JSONObject) patientAttributeObject);
                if(personAttribute != null){
                    attributes.add(personAttribute);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['patient']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("patient.personattribute^")){
                    PersonAttribute personAttribute = getPatientAdttributeFromJsonObject((JSONObject) patientObject.get(key));
                    if(personAttribute != null){
                        attributes.add(personAttribute);
                    }
                }
            }
        } catch (InvalidPathException ex) {
            log.error("Error while parsing person attribute", ex);
        }

        if(!attributes.isEmpty()) {
            unsavedPatient.setAttributes(attributes);
        }
    }

    private PersonAttribute getPatientAdttributeFromJsonObject(JSONObject attributeJsonObject){
        if(attributeJsonObject == null){
            return null;
        }

        String attributeValue = (String) getElementFromJsonObject(attributeJsonObject,"attribute_value");
        if(StringUtils.isBlank(attributeValue)){
            return null;
        }

        String attributeTypeName = (String) getElementFromJsonObject(attributeJsonObject,"attribute_type_name");
        String attributeTypeUuid = (String) getElementFromJsonObject(attributeJsonObject,"attribute_type_uuid");

        PersonService personService = Context.getPersonService();
        PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(attributeTypeUuid);

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

    private Patient findSimilarSavedPatient() {
        Patient savedPatient = null;
        if (unsavedPatient.getNames().isEmpty()) {
            PatientIdentifier identifier = unsavedPatient.getPatientIdentifier();
            if (identifier != null) {
                List<Patient> patients = Context.getPatientService().getPatients(identifier.getIdentifier());
                savedPatient = PatientSearchUtils.findPatient(patients, unsavedPatient);
            }
        } else {
            PersonName personName = unsavedPatient.getPersonName();
            List<Patient> patients = Context.getPatientService().getPatients(personName.getFullName());
            savedPatient = PatientSearchUtils.findPatient(patients, unsavedPatient);
        }
        return savedPatient;
    }

    private Object getElementFromJsonObject(JSONObject jsonObject, String key){
        if(jsonObject.containsKey(key)) {
            return jsonObject.get(key);
        }
        return null;
    }

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}
