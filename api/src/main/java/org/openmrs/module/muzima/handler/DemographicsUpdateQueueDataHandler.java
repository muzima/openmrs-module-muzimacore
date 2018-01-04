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
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.muzima.utils.PatientSearchUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
                updateSavedPatientDemographics();
                Context.getPatientService().savePatient(savedPatient);
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

    private void updateSavedPatientDemographics(){
        if(unsavedPatient.getIdentifiers() != null){
            savedPatient.addIdentifiers(unsavedPatient.getIdentifiers());
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
        if(unsavedPatient.getPersonAddress() != null) {
            savedPatient.addAddress(unsavedPatient.getPersonAddress());
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
        setUnsavedPatientIdentifiersFromPayload();
        setUnsavedPatientBirthDateFromPayload();
        setUnsavedPatientBirthDateEstimatedFromPayload();
        setUnsavedPatientGenderFromPayload();
        setUnsavedPatientNameFromPayload();
        setUnsavedPatientAddressesFromPayload();
        setUnsavedPatientPersonAttributesFromPayload();
        setUnsavedPatientChangedByFromPayload();
    }

    private void setUnsavedPatientIdentifiersFromPayload() {
        List<PatientIdentifier> otherIdentifiers = getOtherPatientIdentifiersFromPayload();
        if (!otherIdentifiers.isEmpty()) {
            Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
            patientIdentifiers.addAll(otherIdentifiers);
            setIdentifierTypeLocation(patientIdentifiers);
            unsavedPatient.addIdentifiers(patientIdentifiers);
        }
    }

    private List<PatientIdentifier> getOtherPatientIdentifiersFromPayload() {
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

    private void setUnsavedPatientBirthDateFromPayload(){
        Date birthDate = JsonUtils.readAsDate(payload, "$['demographicsupdate']['demographicsupdate.birth_date']");
        if(birthDate != null){
            if(isBirthDateChangeValidated()){
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
            if(isGenderChangeValidated()){
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

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.personaddress^")){
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
    private void setUnsavedPatientPersonAttributesFromPayload() {
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        try {
            Object patientAttributeObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.personattribute']");
            if (JsonUtils.isJSONArrayObject(patientAttributeObject)) {
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

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.personattribute^")){
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

    private  void setUnsavedPatientChangedByFromPayload(){
        String providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");
        User user = Context.getUserService().getUserByUsername(providerString);
        if (user == null) {
            queueProcessorException.addException(new Exception("Unable to find user using the id: " + providerString));
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