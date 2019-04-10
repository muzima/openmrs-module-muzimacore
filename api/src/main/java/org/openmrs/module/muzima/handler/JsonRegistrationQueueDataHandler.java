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
import org.apache.commons.lang.time.DateUtils;
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
import org.openmrs.module.idgen.service.IdentifierSourceService;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Write brief description about the class here.
 */
@Handler(supports = QueueData.class, order = 1)
public class JsonRegistrationQueueDataHandler implements QueueDataHandler {

    private static final String DISCRIMINATOR_VALUE = "json-registration";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(JsonRegistrationQueueDataHandler.class);

    private Patient unsavedPatient;
    private String payload;
    Set<PersonAttribute> personAttributes;
    private QueueProcessorException queueProcessorException;

    public static final String NEXT_OF_KIN_ADDRESS = "7cf22bec-d90a-46ad-9f48-035952261294";
    public static final String NEXT_OF_KIN_CONTACT = "342a1d39-c541-4b29-8818-930916f4c2dc";
    public static final String NEXT_OF_KIN_NAME = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
    public static final String NEXT_OF_KIN_RELATIONSHIP = "d0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5";
    public static final String SUBCHIEF_NAME = "40fa0c9c-7415-43ff-a4eb-c7c73d7b1a7a";
    public static final String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";
    public static final String EMAIL_ADDRESS = "b8d0b331-1d2d-4a9a-b741-1816f498bdb6";
    public static final String ALTERNATE_PHONE_CONTACT = "94614350-84c8-41e0-ac29-86bc107069be";
    public static final String NEAREST_HEALTH_CENTER = "27573398-4651-4ce5-89d8-abec5998165c";
    public static final String GUARDIAN_FIRST_NAME = "8caf6d06-9070-49a5-b715-98b45e5d427b";
    public static final String GUARDIAN_LAST_NAME = "0803abbd-2be4-4091-80b3-80c6940303df";

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
                queueProcessorException.addException(new Exception("Exception while process payload ",e));
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
            queueProcessorException.addException(new Exception("Exception while validating payload ",e));
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
        if(!JsonUtils.readAsBoolean(payload, "$['skipPatientMatching']")) {
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
    }

    private void populateUnsavedPatientFromPayload() {
        setPatientIdentifiersFromPayload();
        setPatientBirthDateFromPayload();
        setPatientBirthDateEstimatedFromPayload();
        setPatientGenderFromPayload();
        setPatientNameFromPayload();
        setPatientAddressesFromPayload();
        setPersonAttributesFromPayload();
        setUnsavedPatientCreatorFromPayload();
    }

    private void setPatientIdentifiersFromPayload() {
        Set<PatientIdentifier> patientIdentifiers = new TreeSet<PatientIdentifier>();
        PatientIdentifier preferredIdentifier = getPreferredPatientIdentifierFromPayload();
        if (preferredIdentifier != null) {
            patientIdentifiers.add(preferredIdentifier);
        }
        List<PatientIdentifier> otherIdentifiers = getOtherPatientIdentifiersFromPayload();
        if (!otherIdentifiers.isEmpty()) {
            patientIdentifiers.addAll(otherIdentifiers);
        }
        setIdentifierTypeLocation(patientIdentifiers);
        unsavedPatient.setIdentifiers(patientIdentifiers);
    }

    private PatientIdentifier getPreferredPatientIdentifierFromPayload(){
//        String identifierValue = JsonUtils.readAsString(payload, "$['patient']['patient.medical_record_number']");
//        String identifierTypeName = "AMRS Universal ID";

       // PatientIdentifier preferredPatientIdentifier = createPatientIdentifier(identifierTypeName, identifierValue);
        PatientIdentifier preferredPatientIdentifier = generateOpenMRSID() ;//createPatientIdentifier(identifierTypeName, identifierValue);
        if (preferredPatientIdentifier != null) {
            preferredPatientIdentifier.setPreferred(true);
            return preferredPatientIdentifier;
        } else {
            return null;
        }
    }

    private List<PatientIdentifier> getOtherPatientIdentifiersFromPayload() {
        List<PatientIdentifier> otherIdentifiers = new ArrayList<PatientIdentifier>();
        Object identifierTypeNameObject = JsonUtils.readAsObject(payload, "$['observation']['other_identifier_type']");
        Object identifierValueObject = JsonUtils.readAsObject(payload, "$['observation']['other_identifier_value']");

        if (identifierTypeNameObject instanceof JSONArray) {
            JSONArray identifierTypeName = (JSONArray) identifierTypeNameObject;
            JSONArray identifierValue = (JSONArray) identifierValueObject;
            for (int i = 0; i < identifierTypeName.size(); i++) {
                PatientIdentifier identifier = createPatientIdentifier(identifierTypeName.get(i).toString(),
                        identifierValue.get(i).toString());
                if (identifier != null) {
                    otherIdentifiers.add(identifier);
                }
            }
        } else if (identifierTypeNameObject instanceof String) {
            String identifierTypeName = (String) identifierTypeNameObject;
            String identifierValue = (String) identifierValueObject;
            PatientIdentifier identifier = createPatientIdentifier(identifierTypeName, identifierValue);
            if (identifier != null) {
                otherIdentifiers.add(identifier);
            }
        }
        return otherIdentifiers;
    }

    private PatientIdentifier createPatientIdentifier(String identifierTypeName, String identifierValue) {
        PatientIdentifierType identifierType = Context.getPatientService()
                .getPatientIdentifierTypeByName(identifierTypeName);
        if (identifierType == null) {
            queueProcessorException.addException(
                    new Exception("Unable to find identifier type with name: " + identifierTypeName));
        } else if (identifierValue == null) {
            queueProcessorException.addException(
                    new Exception("Identifier value can't be null type: " + identifierTypeName));
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

        if(locationIdString != null){
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

    private void setPatientBirthDateFromPayload(){
        Date birthDate = JsonUtils.readAsDate(payload, "$['patient']['patient.birth_date']");
        unsavedPatient.setBirthdate(birthDate);
    }

    private void setPatientBirthDateEstimatedFromPayload(){
        boolean birthdateEstimated = JsonUtils.readAsBoolean(payload, "$['patient']['patient.birthdate_estimated']");
        unsavedPatient.setBirthdateEstimated(birthdateEstimated);
    }

    private void setPatientGenderFromPayload(){
        String gender = JsonUtils.readAsString(payload, "$['patient']['patient.sex']");
        unsavedPatient.setGender(gender);
    }

    private void setPatientNameFromPayload(){
        String givenName = JsonUtils.readAsString(payload, "$['patient']['patient.given_name']");
        String familyName = JsonUtils.readAsString(payload, "$['patient']['patient.family_name']");
        String middleName="";
        try{
            middleName= JsonUtils.readAsString(payload, "$['patient']['patient.middle_name']");
        } catch(Exception e){
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

    private String getPatientUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
    }

    private void setPatientAddressesFromPayload(){
        PersonAddress patientAddress = new PersonAddress();

        String county = JsonUtils.readAsString(payload, "$['patient']['patient.county']");
        patientAddress.setStateProvince(county);

        String location = JsonUtils.readAsString(payload, "$['patient']['patient.location']");
        patientAddress.setAddress6(location);

        String sub_location = JsonUtils.readAsString(payload, "$['patient']['patient.sub_location']");
        patientAddress.setAddress5(sub_location);

        String village = JsonUtils.readAsString(payload, "$['patient']['patient.village']");
        patientAddress.setCityVillage(village);

        Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
        addresses.add(patientAddress);
        unsavedPatient.setAddresses(addresses);
    }

    private void setPersonAttributesFromPayload(){
        personAttributes = new TreeSet<PersonAttribute>();
        PersonService personService = Context.getPersonService();

        String mothersName = JsonUtils.readAsString(payload, "$['patient']['patient.mothers_name']");
        setAsAttribute("Mother's Name",mothersName);

        String phoneNumber = JsonUtils.readAsString(payload, "$['patient']['patient.phone_number']");
        setAsAttribute("Telephone contact",phoneNumber);

//        String phoneNumber = JsonUtils.readAsString(payload, "$['patient']['patient.phone_number']");
//        setAsAttributeByUUID(TELEPHONE_CONTACT,phoneNumber);

        String nearestHealthCenter = JsonUtils.readAsString(payload, "$['patient']['patient.nearest_health_center']");
        setAsAttributeByUUID(NEAREST_HEALTH_CENTER,nearestHealthCenter);

        String emailAddress = JsonUtils.readAsString(payload, "$['patient']['patient.email_address']");
        setAsAttributeByUUID(EMAIL_ADDRESS,emailAddress);

        String guardianFirstName = JsonUtils.readAsString(payload, "$['patient']['patient.guardian_first_name']");
        setAsAttributeByUUID(GUARDIAN_FIRST_NAME,guardianFirstName);

        String guardianLastName = JsonUtils.readAsString(payload, "$['patient']['patient.guardian_last_name']");
        setAsAttributeByUUID(GUARDIAN_LAST_NAME,guardianLastName);

        String alternativePhoneContact = JsonUtils.readAsString(payload, "$['patient']['patient.alternate_phone_contact']");
        setAsAttributeByUUID(ALTERNATE_PHONE_CONTACT,alternativePhoneContact);

        String nextOfKinName = JsonUtils.readAsString(payload, "$['patient']['patient.next_of_kin_name']");
        setAsAttributeByUUID(NEXT_OF_KIN_NAME,nextOfKinName);

        String nextOfKinRelationship = JsonUtils.readAsString(payload, "$['patient']['patient.next_of_kin_relationship']");
        setAsAttributeByUUID(NEXT_OF_KIN_RELATIONSHIP,nextOfKinRelationship);

        String nextOfKinContact = JsonUtils.readAsString(payload, "$['patient']['patient.next_of_kin_contact']");
        setAsAttributeByUUID(NEXT_OF_KIN_CONTACT,nextOfKinContact);

        String nextOfKinAddress = JsonUtils.readAsString(payload, "$['patient']['patient.next_of_kin_address']");
        setAsAttributeByUUID(NEXT_OF_KIN_ADDRESS,nextOfKinAddress);


        unsavedPatient.setAttributes(personAttributes);
    }

    private void setAsAttributeByUUID(String uuid, String value){
        PersonService personService = Context.getPersonService();
        PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(uuid);
        if(attributeType !=null && value != null){
            PersonAttribute personAttribute = new PersonAttribute(attributeType, value);
            personAttributes.add(personAttribute);
        } else if(attributeType ==null){
            queueProcessorException.addException(
                    new Exception("Unable to find Person Attribute type by uuid '" + uuid + "'")
            );
        }
    }

    private void setAsAttribute(String attributeTypeName, String value){
        PersonService personService = Context.getPersonService();
        PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(attributeTypeName);
        if(attributeType !=null && value != null){
            PersonAttribute personAttribute = new PersonAttribute(attributeType, value);
            personAttributes.add(personAttribute);
        } else if(attributeType ==null){
            queueProcessorException.addException(
                    new Exception("Unable to find Person Attribute type by name '" + attributeTypeName + "'")
            );
        }
    }


    private  void setUnsavedPatientCreatorFromPayload(){
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
            unsavedPatient.setCreator(user);
        }
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

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
    /**
     * Can't save patients unless they have required OpenMRS IDs
     */
    private PatientIdentifier generateOpenMRSID() {
        PatientIdentifierType openmrsIDType = Context.getPatientService().getPatientIdentifierTypeByUuid("dfacd928-0370-4315-99d7-6ec1c9f7ae76");

        String locationIdString = JsonUtils.readAsString(payload, "$['encounter']['encounter.location_id']");
        Location location = null;
        int locationId;

        if(locationIdString != null){
            locationId = Integer.parseInt(locationIdString);
            location = Context.getLocationService().getLocation(locationId);
        }

        String generated = Context.getService(IdentifierSourceService.class).generateIdentifier(openmrsIDType, "Registration");
        PatientIdentifier identifier = new PatientIdentifier(generated, openmrsIDType, location);
        return identifier;
    }
}
