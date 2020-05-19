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
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAddressFromJsonObject;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAttributeFromJsonObject;

/**
 *  This Handler processes relationships received from {@link org.openmrs.module.muzima.model.DataSource} = "mobile"
 *  The handler will:
 *      <b>Create a new relationship between two persons based on their uuid</b>
 *      <b>Update a relationship between to persons based on their uuid</b>
 *      <b>Delete a relationship between two persons based on their uuid</b>
 *      <b>Throw an error to the {@link org.openmrs.module.muzima.model.ErrorData} where any of this fails</b>
 *
 * @author sthaiya
 */

@Handler(supports = QueueData.class, order = 8)
public class RelationshipQueueDataHandler implements QueueDataHandler {

    public static final String DISCRIMINATOR_VALUE = "json-relationship";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(RelationshipQueueDataHandler.class);

    private String payload;
    private QueueProcessorException queueProcessorException;
    private PersonService personService;

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing relationship data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        personService = Context.getPersonService();
        try {
            if (validate(queueData)) {
                createRelationship();
            }
        } catch (Exception e) {
            /*Custom exception thrown by the validate function should not be added again into @queueProcessorException.
             It should add the runtime dao Exception while saving the data into @queueProcessorException collection */
            if (!e.getClass().equals(QueueProcessorException.class)) {
                queueProcessorException.addException(new Exception("Exception while processing relationship payload ",e));
                log.error(e);
            }
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }

    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing relationship form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            personService = Context.getPersonService();
            payload = queueData.getPayload();
            String temporaryUuid = queueData.getPatientUuid();
            Person patient = personService.getPersonByUuid(temporaryUuid);
            if (patient == null){
                    RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                    RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                    if(registrationData!=null) {
                        patient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
                    }
            }

            if(patient == null)
                queueProcessorException.addException(new Exception("Unable to validate a relationship patient"));

            if (personService.getRelationshipTypeByUuid(getRelationshipTypeUuidFromPayload()) == null)
                queueProcessorException.addException(new Exception("Unable to validate a relationship type used in a relationship"));

            return true;
        } catch (Exception e) {
            log.error("Exception while validating payload ",e);
            queueProcessorException.addException(new Exception("Exception while validating payload ",e));
            return false;
        } finally {
            if (queueProcessorException.anyExceptions())
                throw queueProcessorException;
        }
    }

    private void createRelationship() {
        Person personA = validateOrCreate(getPersonUuidFromPayload("personA"), "personA");
        Person personB = validateOrCreate(getPersonUuidFromPayload("personB"), "personB");
        try {
            if (personA != null && personB !=null) {
                RelationshipType relationshipType = personService.getRelationshipTypeByUuid(getRelationshipTypeUuidFromPayload());
                Relationship relationship = new Relationship(personA, personB, relationshipType);

                // We reuse the uuid created on the mobile device
                relationship.setUuid(getRelationshipUuidFromPayload());

                personService.saveRelationship(relationship);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private Person validateOrCreate(String personUuid, String root){
        Person p = personService.getPersonByUuid(personUuid);
        if (p == null) {
            Person person = new Person();
            try {
                person.addName(getPersonNameFromPayload(root));
                person.setBirthdate(getPersonBirthDateFromPayload(root));
                person.setBirthdateEstimated(getPersonBirthDateEstimatedFromPayload(root));
                person.setGender(getPersonGenderFromPayload(root));
                person.setCreator(getCreatorFromPayload());
                person.setAddresses(getPersonAddressesFromPayload(root));
                person.setAttributes(getPersonAttributesFromPayload(root));

                // We reuse the person uuid created on the mobile device
                person.setUuid(personUuid);

                p = personService.savePerson(person);
            } catch (Exception e) {
                log.error(e);
            }
        }

        return p;
    }

    private String getPersonUuidFromPayload(String root){
        return JsonUtils.readAsString(payload, root + "['uuid']");
    }

    private String getRelationshipUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['uuid']");
    }

    private String getRelationshipTypeUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['relationshipType']['uuid']");
    }

    private PersonName getPersonNameFromPayload(String root){
        String givenName = JsonUtils.readAsString(payload, root + "['given_name']");
        String familyName = JsonUtils.readAsString(payload, root + "['family_name']");
        String middleName="";
        try{
            middleName= JsonUtils.readAsString(payload, root + "['middle_name']");
        } catch(Exception e){
            log.error(e);
        }

        PersonName personName = new PersonName();
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);
        return personName;
    }

    private Date getPersonBirthDateFromPayload(String root){
        return JsonUtils.readAsDate(payload, root + "['birth_date']");
    }

    private Boolean getPersonBirthDateEstimatedFromPayload(String root){
        boolean birthdateEstimated = false;

        try{
            birthdateEstimated= JsonUtils.readAsBoolean(payload, root + "['birthdate_estimated']");
        } catch(Exception e){
            log.error(e);
        }

        return birthdateEstimated;
    }

    private String getPersonGenderFromPayload(String root){
        return JsonUtils.readAsString(payload, root + "['sex']");
    }

    private  User getCreatorFromPayload(){
        String providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");

        if (StringUtils.isEmpty(providerString))
            providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id_select']");

        User user = Context.getUserService().getUserByUsername(providerString);
        if (user == null) {
            queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + providerString));
            return null;
        } else {
            return  user;
        }
    }
    private Set<PersonAddress> getPersonAddressesFromPayload(String root) {
        Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
        try {
            Object patientAddressObject = JsonUtils.readAsObject(payload, root + "['addresses']");
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
        } catch (InvalidPathException e) {
            log.error("Error while parsing person address", e);
        }
        return addresses;
    }

    private Set<PersonAttribute> getPersonAttributesFromPayload(String root) {
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        try {
            Object patientAttributeObject = JsonUtils.readAsObject(payload, root + "['attributes']");
            if (JsonUtils.isJSONArrayObject(patientAttributeObject)) {
                for (Object personAttributeJSONObject:(JSONArray) patientAttributeObject) {
                    try {
                        PersonAttribute personAttribute = getPersonAttributeFromJsonObject((JSONObject) personAttributeJSONObject);
                        if (personAttribute != null) {
                            attributes.add(personAttribute);
                        }
                    } catch (Exception e){
                        queueProcessorException.addException(e);
                        log.error(e);
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
                    log.error(e);
                }
            }
        } catch (InvalidPathException ex) {
            log.error("Error while parsing person attribute", ex);
        }
        return attributes;
    }

    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }
}