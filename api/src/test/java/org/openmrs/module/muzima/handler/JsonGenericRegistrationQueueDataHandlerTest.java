package org.openmrs.module.muzima.handler;

import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JsonGenericRegistrationQueueDataHandlerTest {

    private JsonGenericRegistrationQueueDataHandler jsonGenericRegistrationQueueDataHandler;

    private Patient unsavedPatient;

    private QueueProcessorException queueProcessorException = new QueueProcessorException();


    private String testJsonPayload = "{\n" +
            "  \"patient\": {\n" +
            "    \"patient.family_name\": \"Maina\",\n" +
            "    \"patient.given_name\": \"Kajwang\",\n" +
            "    \"patient.middle_name\": \"Milly\",\n" +
            "    \"patient.uuid\": \"1037ba06-fj79-4244-9d14-687baa44bd81\",\n" +
            "    \"patient.sex\": \"F\",\n" +
            "    \"patient.birth_date\": \"04-06-1994\",\n" +
            "    \"patient.birthdate_estimated\": \"...\",\n" +
            "    \"patient.personattribute^1\": {\n" +
            "      \"attribute_value\": \"0733445566\",\n" +
            "      \"attribute_type_name\": \"Contact Phone Number\",\n" +
            "      \"attribute_type_uuid\": \"8037ba06-fc79-4244-9d14-687baa44bd81\"\n" +
            "    },\n" +
            "    \"patient.personattribute^2\": {\n" +
            "      \"attribute_value\": \"Ayuma\",\n" +
            "      \"attribute_type_name\": \"Mother's Name\",\n" +
            "      \"attribute_type_uuid\": \"8d871d18-c2cc-11de-8d13-0010c6dffd0f\"\n" +
            "    },\n" +
            "    \"patient.personaddress^1\": {\n" +
            "      \"countyDistrict\": \"county1\",\n" +
            "      \"address6\": \"location1\",\n" +
            "      \"address5\": \"sublocation1\",\n" +
            "      \"cityVillage\": \"village1\"\n" +
            "    },\n" +
            "    \"patient.personaddress^2\": {\n" +
            "      \"countyDistrict\": \"county2\",\n" +
            "      \"address6\": \"location2\",\n" +
            "      \"address5\": \"sublocation2\",\n" +
            "      \"cityVillage\": \"village2\"\n" +
            "    },\n" +
            "    \"patient.medical_record_number\": {\n" +
            "      \"identifier_value\": \"555555555-5\",\n" +
            "      \"identifier_type_uuid\": \"12345\"\n" +
            "    },\n" +
            "    \"patient.otheridentifier\": [\n" +
            "      {\n" +
            "        \"identifier_type_name\": \"KENYAN NATIONAL ID NUMBER\",\n" +
            "        \"identifier_value\": \"2233\",\n" +
            "        \"confirm_other_identifier_value\": \"2233\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"identifier_type_name\": \"CCC Number\",\n" +
            "        \"identifier_value\": \"456\",\n" +
            "        \"confirm_other_identifier_value\": \"456\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"tmp\": {\n" +
            "    \"tmp.birthdate_type\": \"age\",\n" +
            "    \"tmp.age_in_years\": \"20\"\n" +
            "  },\n" +
            "  \"encounter\": {\n" +
            "    \"encounter.location_id\": \"8\",\n" +
            "    \"encounter.provider_id_select\": \"3356-3\",\n" +
            "    \"encounter.provider_id\": \"3356-3\",\n" +
            "    \"encounter.encounter_datetime\": \"04-09-2017\"\n" +
            "  }\n" +
            "}\n";

    @Autowired
    private QueueData queueData;


    @Test
    public void retrievePersonAddress_simpleRetrieveTest() throws Exception {
        JSONObject personAddressObject = (JSONObject) JsonUtils.readAsObject(testJsonPayload, "$['patient']['patient.personaddress^1']");
        System.out.println(personAddressObject.toJSONString());
        assertThat(personAddressObject).isNotNull();
        assertThat(personAddressObject).isInstanceOf(JSONObject.class);
    }

    @Before
    //   @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public void setUp() {

        unsavedPatient = new Patient();
        Person unsavedPerson = new Person();
        PersonName unsavedPersonName = new PersonName();

        unsavedPersonName.setFamilyName("James");
        unsavedPersonName.setGivenName("Gosling");
        unsavedPersonName.setMiddleName("W");
        unsavedPersonName.setPerson(unsavedPerson);

        Set<PersonName> unsavedPersonsNames = new HashSet<>();

        unsavedPersonsNames.add(unsavedPersonName);
        unsavedPatient.setId(1);
        unsavedPatient.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        unsavedPatient.setNames(unsavedPersonsNames);
        unsavedPatient.setBirthdate(new Date());

        ApplicationContext testApplicationContext = new ClassPathXmlApplicationContext("service-test-context.xml");
        jsonGenericRegistrationQueueDataHandler = new JsonGenericRegistrationQueueDataHandler();
        jsonGenericRegistrationQueueDataHandler.unsavedPatient = unsavedPatient;
        jsonGenericRegistrationQueueDataHandler.payload = testJsonPayload;
        jsonGenericRegistrationQueueDataHandler.queueProcessorException = queueProcessorException;
    }

    @Test
    public void setPatientIdentifiersFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientIdentifiersFromPayload();
        PatientIdentifier identifier = unsavedPatient.getPatientIdentifier();
        assertThat(identifier).isNotNull();
        assertThat(identifier.getIdentifier()).isEqualTo("12345");
    }

    @Test
    public void getAutogeneratedIdentifierTest() throws Exception {
        //TODO write test method, anticipated an org.openmrs.api.APIException while trying to obtain Context by calling Context.getServiceContext
    }

    @Test
    public void getMedicalRecordNumberFromPayloadTest() throws Exception {
        PatientIdentifier patientIdentifier = jsonGenericRegistrationQueueDataHandler.getMedicalRecordNumberFromPayload();
        assertThat(patientIdentifier).isNotNull();
    }

    @Test
    public void getOtherPatientIdentifiersFromPayloadTest() throws Exception {
        List<PatientIdentifier> identifier = jsonGenericRegistrationQueueDataHandler.getOtherPatientIdentifiersFromPayload();
        assertThat(identifier.size()).isGreaterThan(0);
        assertThat(identifier.iterator().next()).isNotNull();
        assertThat(identifier.iterator().hasNext()).isTrue();
        assertThat(identifier.iterator().next().getIdentifier()).isEqualTo("2233");
        assertThat(identifier.iterator().next().getIdentifierType().getName()).isEqualTo("KENYAN NATIONAL ID NUMBER");
    }

    @Test
    public void createPatientIdentifier_shouldDetectBalnkIdentifireTypeValuesTest() throws Exception {
        PatientIdentifier patientIdentifier = jsonGenericRegistrationQueueDataHandler.createPatientIdentifier("", "", "");
        assertThat(patientIdentifier).isNull();
        ;
    }

    @Test
    public void createPatientIdentifier_shouldObtainIdentifierTypeByUuid() throws Exception {
        PatientIdentifier patientIdentifier = jsonGenericRegistrationQueueDataHandler.createPatientIdentifier("", "KENYA_NATIONAL_ID", "33333333");
        assertThat(patientIdentifier).isNotNull();
        assertThat(patientIdentifier.getIdentifierType()).isSameAs("KENAYA_NATIONAL_ID");
        assertThat(patientIdentifier.getIdentifier()).isSameAs("33333333");
        assertThat(patientIdentifier.getPatient()).isNotNull();
        assertThat(patientIdentifier.getPatient()).isInstanceOf(Patient.class);
    }

    @Test
    public void setIdentifierTypeLocationTest() throws Exception {
        Set<PatientIdentifier> identifiers = new HashSet<>();
        jsonGenericRegistrationQueueDataHandler.setIdentifierTypeLocation(identifiers);

        assertTrue(unsavedPatient.getIdentifiers().contains(identifiers.iterator().next()));
    }

    @Test
    public void setPatientBirthDateFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientBirthDateFromPayload();
        Date birthDate = unsavedPatient.getBirthdate();
        assertThat(birthDate).isNotNull();
        assertThat(birthDate).isInstanceOf(Date.class);
    }

    @Test
    public void setPatientBirthDateEstimatedFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientBirthDateFromPayload();
        Boolean estimated = unsavedPatient.getBirthdateEstimated();
        assertThat(estimated).isNotNull();
        assertThat(estimated).isTrue();
    }

    @Test
    public void setPatientGenderFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientGenderFromPayload();
        String patientGender = this.unsavedPatient.getGender();
        assertThat(patientGender).isNotEmpty();
        assertThat(patientGender).isEqualTo("F");

    }

    @Test
    public void setPatientNameFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientNameFromPayload();
        PersonName personName = unsavedPatient.getPersonName();

        assertThat(personName).isNotNull();
        assertThat(personName.getFamilyName()).isEqualTo("Maina");
        assertThat(personName.getFullName()).isEqualTo("Kajwang Milly Maina");
        assertThat(personName.getGivenName()).isEqualTo("Milly");
    }

    @Test
    public void getPatientUuidFromPayloadTest() throws Exception {
        String extractedUuid = jsonGenericRegistrationQueueDataHandler.getPatientUuidFromPayload();
        assertThat(extractedUuid).isNotEmpty();
        assertThat(extractedUuid).isNotNull();
        assertThat(extractedUuid).isEqualTo("1037ba06-fj79-4244-9d14-687baa44bd81");
    }

    @Test
    public void setPatientAddressesFromPayloadTest() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPatientAddressesFromPayload();
        PersonAddress personAddress = unsavedPatient.getPersonAddress();

        assertThat(personAddress).isNotNull();
        assertThat(personAddress.getAddress6()).isEqualTo("county2");
        assertThat(personAddress.getAddress5()).isEqualTo("sublocation2");
        assertThat(personAddress.getCityVillage()).isEqualTo("village2");
        assertThat(personAddress.getCountyDistrict()).isEqualTo("county1");
    }

    @Test
    public void getPatientAddressFromJsonObjectTest() throws Exception {

        JSONObject personAddressObject = (JSONObject) JsonUtils.readAsObject(testJsonPayload, "$['patient']['patient.personaddress^1']");
        System.out.println(personAddressObject.toJSONString());
        assertThat(personAddressObject).isNotNull();
        assertThat(personAddressObject).isInstanceOf(JSONObject.class);

        PersonAddress personAddress = jsonGenericRegistrationQueueDataHandler.getPatientAddressFromJsonObject(personAddressObject);

        assertNotNull(personAddressObject);
        assertThat(personAddress.getAddress6()).isEqualTo("location1");
        assertThat(personAddress.getAddress5()).isEqualTo("sublocation1");
        assertThat(personAddress.getCityVillage()).isEqualTo("village1");
        assertThat(personAddress.getCountyDistrict()).isEqualTo("county1");

    }

    @Test
    public void setPersonAttributesFromPayloadTet() throws Exception {
        jsonGenericRegistrationQueueDataHandler.setPersonAttributesFromPayload();
        Set<PersonAttribute> personAttribute = unsavedPatient.getAttributes();

        assertThat(personAttribute).isNotNull();
        assertThat(personAttribute).isNotEmpty();
        assertThat(personAttribute.size()).isGreaterThan(0);
        assertThat(personAttribute.iterator().next().getValue()).isEqualTo("0733445566");
        assertThat(personAttribute.iterator().next().getUuid()).isEqualTo("8037ba06-fc79-4244-9d14-687baa44bd81");
        assertThat(personAttribute.iterator().hasNext()).isTrue();

    }

    @Test
    public void getPatientAttributeFromJsonObjectTest() throws Exception {

    }


    @Test
    public void findSimilarSavedPatient_whenPatientNamesIsNullTest() throws Exception {
        Patient unsavedPatient = new Patient();

        Patient testUnsavedPatient = new Patient();
        Person unsavedPerson = new Person();
        PersonName unsavedPersonName = new PersonName();

        unsavedPersonName.setFamilyName("James");
        unsavedPersonName.setGivenName("Gosling");
        unsavedPersonName.setMiddleName("W");
        unsavedPersonName.setPerson(unsavedPerson);

        Set<PersonName> unsavedPersonsNames = new HashSet<>();

        unsavedPersonsNames.add(unsavedPersonName);
        testUnsavedPatient.setId(1);
        testUnsavedPatient.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        testUnsavedPatient.setNames(unsavedPersonsNames);
        testUnsavedPatient.setBirthdate(new Date());

        Mockito.when(Context.getPatientService().getPatients(unsavedPatient
                                                                     .getPatientIdentifier()
                                                                     .getIdentifier()))

                .thenReturn(Collections.singletonList(testUnsavedPatient));

        Patient patient = jsonGenericRegistrationQueueDataHandler.findSimilarSavedPatient();

        assertThat(patient).isNotNull();
        assertThat(patient).isEqualTo(unsavedPatient);
        assertThat(patient).isInstanceOf(Patient.class);
    }

    @Test
    public void getElementFromJsonObjectTest() throws Exception {
        String elementPayload = "{\"key\":\"04-06-1994\"}";
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(elementPayload, "$");
        Object element = jsonGenericRegistrationQueueDataHandler.getElementFromJsonObject(jsonObject, "key");
        assertThat(element).isNotNull();
        assertThat(element).isEqualTo(element);
    }

    /**
     * @throws Exception
     * @verifier Final test cycle phase
     */
    @Test
    public void acceptTest() throws Exception {
    }

    @Test
    public void getDiscriminatorValueTest() {
        assertThat(jsonGenericRegistrationQueueDataHandler.getDiscriminator()).isNotNull();
        assertThat(jsonGenericRegistrationQueueDataHandler.getDiscriminator()).isNotEmpty();
        assertThat(jsonGenericRegistrationQueueDataHandler.getDiscriminator()).isEqualTo("json-generic-registration");
    }
}


