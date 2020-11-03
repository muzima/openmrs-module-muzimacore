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
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static org.openmrs.module.muzima.utils.PersonCreationUtils.copyPersonAddress;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.createPersonPayloadStubForPerson;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.createPersonPayloadStubFromIndexPatientStub;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAddressFromJsonObject;
import static org.openmrs.module.muzima.utils.PersonCreationUtils.getPersonAttributeFromJsonObject;


@Handler(supports = QueueData.class, order = 8)
public class PersonDemographicsUpdateQueueDataHandler  implements QueueDataHandler {

    public static final String DISCRIMINATOR_VALUE = "json-person-demographics-update";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final Log log = LogFactory.getLog(RelationshipQueueDataHandler.class);

    private String payload;
    private QueueProcessorException queueProcessorException;
    private PersonService personService;
    private Person unsavedPerson;
    private Person savedPerson;

    private void requeIndexPatientObsAsEncounterIfDefined(final QueueData queueData){
        Object indexObsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['index_obs']");
        Object indexPatientObject = JsonUtils.readAsObject(queueData.getPayload(), "$['index_patient']");
        if (indexObsObject != null && indexPatientObject != null) {
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

    private void requePersonObsAsIndividualObsIfDefined(final QueueData queueData){
        Object obsObject = JsonUtils.readAsObject(queueData.getPayload(), "$['observation']");
        if (obsObject != null) {
            QueueData encounterQueueData = new QueueData();

            //Recreate payload to reflect updated person demographics and eliminate index_patient obs, if any
            JSONObject payload = new JSONObject();
            payload.put("patient",createPersonPayloadStubForPerson(savedPerson));
            payload.put("observation",obsObject);
            payload.put("encounter",JsonUtils.readAsObject(queueData.getPayload(), "$['encounter']"));

            encounterQueueData.setPayload(payload.toJSONString());

            encounterQueueData.setDiscriminator("json-individual-obs");
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

    @Override
    public void process(final QueueData queueData) throws QueueProcessorException {
        log.info("Processing demographics update form data: " + queueData.getUuid());
        try {
            if (validate(queueData)) {
                if(isPersonDemographicsUpdateStuDefined()) {
                    updateSavedPersonDemographics();
                    Context.getPersonService().savePerson(savedPerson);
                }

                requePersonObsAsIndividualObsIfDefined(queueData);
                requeIndexPatientObsAsEncounterIfDefined(queueData);
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

    private void updateSavedPersonDemographics(){
        if(unsavedPerson.getPersonName() != null) {
            savedPerson.addName(unsavedPerson.getPersonName());
        }
        if(StringUtils.isNotBlank(unsavedPerson.getGender())) {
            savedPerson.setGender(unsavedPerson.getGender());
        }
        if(unsavedPerson.getBirthdate() != null) {
            savedPerson.setBirthdate(unsavedPerson.getBirthdate());
            savedPerson.setBirthdateEstimated(unsavedPerson.getBirthdateEstimated());
        }
        if(unsavedPerson.getAddresses() != null) {
            for(PersonAddress unsavedAddress:unsavedPerson.getAddresses()) {
                boolean savedAddressFound = false;

                if(StringUtils.isNotBlank(unsavedAddress.getUuid())) {
                    for (PersonAddress savedAddress : savedPerson.getAddresses()) {
                        if (StringUtils.equals(unsavedAddress.getUuid(), savedAddress.getUuid())) {
                            savedAddressFound = true;
                            copyPersonAddress(unsavedAddress, savedAddress);
                            break;
                        }
                    }
                }
                if(!savedAddressFound){
                    savedPerson.getAddresses().add(unsavedAddress);
                }
            }
        }
        if(unsavedPerson.getAttributes() != null) {
            Set<PersonAttribute> attributes = unsavedPerson.getAttributes();
            Iterator<PersonAttribute> iterator = attributes.iterator();
            while(iterator.hasNext()) {
                savedPerson.addAttribute(iterator.next());
            }
        }
        if(unsavedPerson.getChangedBy() != null) {
            savedPerson.setChangedBy(unsavedPerson.getChangedBy());
        }
    }

    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing demographics Update form data: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            payload = queueData.getPayload();
            Person candidatePerson = getCandidatePersonFromPayload();
            savedPerson = findSavedPerson(candidatePerson,true);
            if(savedPerson == null){
                queueProcessorException.addException(new Exception("Unable to uniquely identify Person for this " +
                        "demographic update form data. "));
            } else {
                populateUnsavedPersonDemographicsFromPayload();
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

    private Person findSavedPerson(Person candidatePerson, boolean searchRegistrationData){
        Person savedPerson = null;
        if (StringUtils.isNotEmpty(candidatePerson.getUuid())) {
            savedPerson = Context.getPersonService().getPersonByUuid(candidatePerson.getUuid());
            if (savedPerson == null && searchRegistrationData == true) {
                String temporaryUuid = candidatePerson.getUuid();
                RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                if(registrationData!=null) {
                    savedPerson = Context.getPersonService().getPersonByUuid(registrationData.getAssignedUuid());
                }
            }
        }
        return savedPerson;
    }

    private Person getCandidatePersonFromPayload(){
        Person candidatePerson = new Person();

        String uuid = getCandidatePersonUuidFromPayload();
        candidatePerson.setUuid(uuid);

        PersonName personName = getCandidatePersonNameFromPayload();
        candidatePerson.addName(personName);

        String gender = getCandidatePersonGenderFromPayload();
        candidatePerson.setGender(gender);

        Date birthDate = getCandidatePersonBirthDateFromPayload();
        candidatePerson.setBirthdate(birthDate);

        return candidatePerson;
    }

    private String getCandidatePersonUuidFromPayload(){
        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
    }

    private PersonName getCandidatePersonNameFromPayload(){
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

    private String getCandidatePersonGenderFromPayload(){
        return JsonUtils.readAsString(payload, "$['patient']['patient.sex']");
    }

    private Date getCandidatePersonBirthDateFromPayload(){
        return JsonUtils.readAsDate(payload, "$['patient']['patient.birth_date']");
    }

    private void populateUnsavedPersonDemographicsFromPayload() {
        if(isPersonDemographicsUpdateStuDefined()) {
            unsavedPerson = new Person();
            setUnsavedPersonBirthDateFromPayload();
            setUnsavedPersonBirthDateEstimatedFromPayload();
            setUnsavedPersonGenderFromPayload();
            setUnsavedPersonNameFromPayload();
            setUnsavedPersonAddressesFromPayload();
            setUnsavedPersonAttributesFromPayload();
            setUnsavedPatientChangedByFromPayload();
        }
    }

    private  boolean isPersonDemographicsUpdateStuDefined(){
        return JsonUtils.containsKey(payload,"$['demographicsupdate']");
    }

    private void setUnsavedPersonBirthDateFromPayload(){
        Date birthDate = JsonUtils.readAsDate(payload, "$['demographicsupdate']['demographicsupdate.birth_date']");
        if(birthDate != null){
            if(isBirthDateChangeValidated()){
                unsavedPerson.setBirthdate(birthDate);
            }else{
                queueProcessorException.addException(
                        new Exception("Change of Birth Date requires manual review"));
            }
        }
    }

    private void setUnsavedPersonBirthDateEstimatedFromPayload(){
        boolean birthdateEstimated = JsonUtils.readAsBoolean(payload,
                "$['demographicsupdate']['demographicsupdate.birthdate_estimated']");
        unsavedPerson.setBirthdateEstimated(birthdateEstimated);
    }

    private void setUnsavedPersonGenderFromPayload(){
        String gender = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.sex']");
        if(StringUtils.isNotBlank(gender)){
            if(isGenderChangeValidated()){
                unsavedPerson.setGender(gender);
            }else{
                queueProcessorException.addException(
                        new Exception("Change of Gender requires manual review"));
            }
        }
    }

    private void setUnsavedPersonNameFromPayload(){
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
            unsavedPerson.addName(personName);
        }
    }

    private void setUnsavedPersonAddressesFromPayload() {
        Set<PersonAddress> addresses = new TreeSet<PersonAddress>();

        try {
            Object personAddressObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.personaddress']");
            if (JsonUtils.isJSONArrayObject(personAddressObject)) {
                for (Object personAddressJSONObject:(JSONArray) personAddressObject) {
                    PersonAddress patientAddress = getPersonAddressFromJsonObject((JSONObject) personAddressJSONObject);
                    if(patientAddress != null){
                        addresses.add(patientAddress);
                    }
                }
            } else {
                PersonAddress personAddress = getPersonAddressFromJsonObject((JSONObject) personAddressObject);
                if(personAddress != null){
                    addresses.add(personAddress);
                }
            }

            JSONObject patientObject = (JSONObject) JsonUtils.readAsObject(payload, "$['demographicsupdate']");
            Set keys = patientObject.keySet();
            for(Object key:keys){
                if(((String)key).startsWith("demographicsupdate.personaddress^")){
                    PersonAddress personAddress = getPersonAddressFromJsonObject((JSONObject) patientObject.get(key));
                    if(personAddress != null){
                        addresses.add(personAddress);
                    }
                }
            }

        } catch (InvalidPathException e) {
            log.error("Error while parsing person address", e);
        }

        if(!addresses.isEmpty()) {
            unsavedPerson.setAddresses(addresses);
        }
    }

    private void setUnsavedPersonAttributesFromPayload() {
        Set<PersonAttribute> attributes = new TreeSet<PersonAttribute>();
        try {
            Object personAttributeObject = JsonUtils.readAsObject(payload, "$['demographicsupdate']['demographicsupdate.personattribute']");
            if (JsonUtils.isJSONArrayObject(personAttributeObject)) {
                for (Object personAdttributeJSONObject:(JSONArray) personAttributeObject) {
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
                    PersonAttribute personAttribute = getPersonAttributeFromJsonObject((JSONObject) personAttributeObject);
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
        } catch (InvalidPathException ex) {
            log.error("Error while parsing person attribute", ex);
        }

        if(!attributes.isEmpty()) {
            unsavedPerson.setAttributes(attributes);
        }
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
            unsavedPerson.setChangedBy(user);
        }
    }

    private boolean isBirthDateChangeValidated(){
        return JsonUtils.readAsBoolean(payload, "$['demographicsupdate']['demographicsupdate.birthdate_change_validated']");
    }

    private boolean isGenderChangeValidated(){
        return JsonUtils.readAsBoolean(payload, "$['demographicsupdate']['demographicsupdate.gender_change_validated']");
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
