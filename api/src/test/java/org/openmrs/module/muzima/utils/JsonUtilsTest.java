package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class JsonUtilsTest {

    private String mockPayload;
    private final Log logger = LogFactory.getLog(JsonUtilsTest.class);

    @Before
    public void setUp() throws Exception {
        mockPayload = "{\n" +
                "    \"dataSource\": \"Mobile Device\",\n" +
                "    \"discriminator\": \"registration\",\n" +
                "    \"payload\": {\n" +
                "        \"patient.identifier\":\"\",\n" +
                "        \"patient.medical_record_number\": \"9999-4\",\n" +
                "        \"patient.identifier_type\": 3,\n" +
                "        \"patient.identifier_location\": 1,\n" +
                "        \"patient.uuid\":\"6e698d66-9f59-4a3b-b3d7-91efb7b297d3\",\n" +
                "        \"patient.birthdate\": \"1984-04-16 06:15:00\",\n" +
                "        \"patient.birthdate_estimated\": false,\n" +
                "        \"patient.given_name\": \"Example\",\n" +
                "        \"patient.middle_name\": \"of\",\n" +
                "        \"patient.family_name\": \"Patient\",\n" +
                "        \"patient.gender\": \"M\",\n" +
                "        \"person_address.address1\": \"Adress 1\",\n" +
                "        \"person_address.address2\": \"Adress 2\"\n" +
                "    }\n" +
                "}";
    }

    @Test
    @Ignore
    public void writeAsBooleanTest() throws Exception {
        //TODO there is [probably] a need to refactor the JsonUtils.writeAsBoolean() metthod.
        /**
         * Susppecting that that the problem could be due to use of writeAsString while processing java.util.boolean
         * value.
         * Or ofcourse this test case is wrongly written.
         */
        logger.debug("Executing Write as Boolean Test:");
        String stringPayload = "{\"key\":\"true\"}";
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(stringPayload, "$");
        System.out.println(jsonObject.toJSONString());
        JsonUtils.writeAsBoolean(jsonObject, "key", Boolean.valueOf("false"));
        System.err.println(jsonObject.toJSONString());
        System.out.println(JsonUtils.readAsBoolean(jsonObject.toString(), "key"));
        assertThat(JsonUtils.readAsBoolean(jsonObject.toJSONString(), "key")).isEqualTo(false);
        assertThat(JsonUtils.readAsBoolean(jsonObject.toJSONString(), "key")).isNotEqualTo(true);
        assertThat(jsonObject.keySet().contains("key")).isTrue();
        assertThat(jsonObject.entrySet().contains(false)).isTrue();
        assertThat(jsonObject.entrySet().contains(true)).isFalse();
        logger.debug("Write as Boolean Test: JsonObject " + jsonObject.toJSONString() + " Initial value in payload replaced by " + JsonUtils.readAsBoolean(jsonObject.toJSONString(), "key"));
    }

    @Test
    public void readAsBooleanTest() throws Exception {
        logger.debug("Executing Read as Boolean Test");
        String booleanPayload = "{\"key\":\"true\"}";
        Boolean value = JsonUtils.readAsBoolean(booleanPayload, "$.key");
        assertTrue(value);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isEqualTo(value);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isInstanceOf(Boolean.class);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isNotEqualTo(false);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isNotSameAs(false);
        logger.debug("Read value matches value in JsonPayload as described, Read Value" + value + ", Value in Paload " + JsonUtils.readAsBoolean(booleanPayload, "$.key"));
    }

    @Test
    public void writeAsNumericTest() throws Exception {
        logger.debug("Executing Write as Numeric Test");

        String payloadValueToWrite = "{\"key\":\"1.0\"}";

        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(payloadValueToWrite, "$.");
        JsonUtils.writeAsNumeric(jsonObject, "key", 3.0);
        Double doubleValue = JsonUtils.readAsNumeric(jsonObject.toJSONString(), "key");
        assertThat(JsonUtils.readAsNumeric(jsonObject.toJSONString(), "key")).isEqualTo(3.0);
        assertThat(JsonUtils.readAsNumeric(jsonObject.toJSONString(), "key")).isNotEqualTo(1.0);
        logger.debug("Write as Numeric Test: JsonObject " + jsonObject.toJSONString() + ", 1.0; Initial value in payload replaced by " + JsonUtils.readAsNumeric(jsonObject.toJSONString(), "key"));

    }

    @Test
    public void readAsNumericTest() throws Exception {
        logger.debug("Executing Read as Numeric Test");
        String numericPayload = "{\"key\":1.0}";
        Double value = JsonUtils.readAsNumeric(numericPayload, "$.key");
        assertTrue(value.equals(1.0));
        assertThat(JsonUtils.readAsNumeric(numericPayload, "$.key")).isEqualTo(1.0);
        assertThat(JsonUtils.readAsNumeric(numericPayload, "$.key")).isInstanceOf(Double.class);
        logger.debug("Read value matches value in JsonPayload as described, Read value" + value + ", value in Payload " + JsonUtils.readAsNumeric(numericPayload, "$.key"));
    }

    @Test
    public void writeAsStringTest() throws Exception {
        logger.debug("Executing Write as String Test");
        String stringValuePayload = "{\"key\":\"hello\"}";
        String value = JsonUtils.readAsString(stringValuePayload, "key");
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(stringValuePayload, "$");
        JsonUtils.writeAsString(jsonObject, "key", "Updated Hello");
        assertThat(JsonUtils.readAsString(jsonObject.toString(), "key")).isInstanceOf(String.class);
        assertThat(JsonUtils.readAsString(jsonObject.toString(), "key")).isEqualTo("Updated Hello");
        logger.debug("Write as String Test: JsonObject " + jsonObject.toJSONString() + "; Initial value in payload replaced by " + JsonUtils.readAsString(jsonObject.toJSONString(), "$.key"));

    }

    @Test
    public void readAsStringTest() throws Exception {
        logger.debug("Executing Read as String Test");
        String stringPayload = "{\"key\":\"hello\"}";
        String value = JsonUtils.readAsString(stringPayload, "key");
        assertEquals(value, "hello");
        assertThat(JsonUtils.readAsString(stringPayload, "key")).isInstanceOf(String.class);
        assertThat(JsonUtils.readAsString(stringPayload, "key")).isEqualTo("hello");
        assertThat(JsonUtils.readAsString(stringPayload, "key")).isNotEqualTo("Hello Wrong String");
        assertThat(JsonUtils.readAsString(stringPayload, "key")).isNotNull();
        logger.debug("Read value matches value in JsonPayload as described, Read value in payload " + JsonUtils.readAsString(stringPayload, "key"));
    }

    @Test
    public void parseLinkedHashMapToJsonObjectTest() throws Exception {

    }

    @Test
    @Ignore //TODO - determine the workflow employeed for DateTime usage in muzima- to facilitate testing
    /**
     * TODO : View and Integrate the correct DateAndTime representation @ href="http://en.wikipedia.org/wiki/ISO_8601">ISO-8601 Wikipedia Page
     */
    public void writeAsDateTimeTest() throws Exception {
        logger.debug("Executing Write as DateTime Test");
        String dateTimeSamplePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date dateTimeValue = JsonUtils.readAsDateTime(dateTimeSamplePayload, "key");
        assertThat(JsonUtils.readAsDateTime(dateTimeSamplePayload, "key")).isNotNull();
        assertThat(JsonUtils.readAsDateTime(dateTimeSamplePayload, "key")).isEqualTo("1984-04-16 06:15:00");
        logger.debug("Read value matches value in JsonPayliod as described,- " + JsonUtils.readAsDateTime(dateTimeSamplePayload, "$.key"));
    }

    @Test
    @Ignore
    //TODO - determine the workflow employeed for DateTime usage in muzima- to facilitate testing
    /**
     * TODO : View and Integrate the correct DateAndTime representation @ href="http://en.wikipedia.org/wiki/ISO_8601">ISO-8601 Wikipedia Page
     */
    public void readAsDateTimeTest() throws Exception {
        logger.debug("Executing Read as DataTime ");
        String currentDate = new Date().toString();
        String datePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date value = JsonUtils.readAsDateTime(datePayload, "$.key");
        assertEquals("1984-04-16 06:15:00", value.toString());
        Assertions.assertThat(value).isNotNull();
        logger.debug("Expected to read value of 1984-04-16 06:15:00 returned " + JsonUtils.readAsDateTime(datePayload, "$.key"));
    }

    @Test
    public void readAsObjectTest() throws Exception {
        logger.debug("Executing Read as Object Test");
        String samplePayload = "{\"key\":\"hello\"}";
        Object value = JsonUtils.readAsObject(samplePayload, "$.key");
        assertTrue(value instanceof Object);
        Assertions.assertThat(value).isNotNull();
        assertThat(value).isEqualTo("hello");
        assertThat(value).isInstanceOf(String.class);
        logger.debug("Expected to read value of expected - [hello], call returned " + JsonUtils.readAsObject(samplePayload, "$.key"));
    }

    @Test
    public void isPayloadAJsonArrayTest() throws Exception {
        logger.debug("Executing isPathAJSONArrayTest");
        String jsonArrayPayload = "[" +
                "{\"key\":\"hello\"}," +
                "{\"key2\":\"hello Two\"}" +
                "]";
        System.out.println(jsonArrayPayload);
        JSONArray jsonArray = (JSONArray) JsonUtils.readAsObject(jsonArrayPayload, "$");
        System.out.println(jsonArray.toString());
        assertThat(JsonUtils.isJSONArrayObject(jsonArray)).isEqualTo(true);
        String jsonObjectPayload = "{\"key\":\"hello\"}";
        assertThat(JsonUtils.isJSONArrayObject(jsonObjectPayload)).isEqualTo(false);
        logger.debug("Test executed against sample payload " + jsonArrayPayload);
    }

    @Test
    public void isPersonAddressMultiNodeTest() throws Exception {
        String jsonSamplePayloadToReadFrom = "{\n" +
                "        \"patient\": {\n" +
                "                \"patient.family_name\": \"Maina\",\n" +
                "                \"patient.given_name\": \"Kajwang\",\n" +
                "                \"patient.middle_name\": \"Milly\",\n" +
                "                \"patient.sex\": \"F\",\n" +
                "                \"patient.birthdate_estimated\": \"...\",\n" +
                "                \"patient.personattribute^1\": {\n" +
                "                        \"attribute_value\": \"0733445566\",\n" +
                "                        \"attribute_type_name\": \"Contact Phone Number\",\n" +
                "                        \"attribute_type_uuid\": \"8037ba06-fc79-4244-9d14-687baa44bd81\"\n" +
                "                },\n" +
                "                \"patient.personattribute^2\": {\n" +
                "                        \"attribute_value\": \"Ayuma\",\n" +
                "                        \"attribute_type_name\": \"Mother's Name\",\n" +
                "                        \"attribute_type_uuid\": \"8d871d18-c2cc-11de-8d13-0010c6dffd0f\"\n" +
                "                },\n" +
                "                \"patient.personaddress\": {\n" +
                "                        \"countyDistrict\": \"county\",\n" +
                "                        \"address6\": \"location\",\n" +
                "                        \"address5\": \"sublocation\",\n" +
                "                        \"cityVillage\": \"village\"\n" +
                "                },\n" +
                "                \"patient.medical_record_number\": {\n" +
                "                        \"identifier_value\": \"555555555-5\",\n" +
                "                        \"identifier_type_uuid\": \"12345\"\n" +
                "                },\n" +
                "                \"patient.otheridentifier\": [\n" +
                "                        {\n" +
                "                                \"identifier_type_name\": \"KENYAN NATIONAL ID NUMBER\",\n" +
                "                                \"identifier_value\": \"2233\",\n" +
                "                                \"confirm_other_identifier_value\": \"2233\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                                \"identifier_type_name\": \"CCC Number\",\n" +
                "                                \"identifier_value\": \"456\",\n" +
                "                                \"confirm_other_identifier_value\": \"456\"\n" +
                "                        }\n" +
                "                ]\n" +
                "        },\n" +
                "        \"tmp\": {\n" +
                "                \"tmp.birthdate_type\": \"age\",\n" +
                "                \"tmp.age_in_years\": \"20\"\n" +
                "        },\n" +
                "        \"encounter\": {\n" +
                "                \"encounter.location_id\": \"8\",\n" +
                "                \"encounter.provider_id_select\": \"3356-3\",\n" +
                "                \"encounter.provider_id\": \"3356-3\",\n" +
                "                \"encounter.encounter_datetime\": \"04-09-2017\"\n" +
                "        }\n" +
                "}";

        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(jsonSamplePayloadToReadFrom, "patient");
        System.err.println("{  "+jsonObject);
        logger.debug("Executing multinode check algorithm test");
        assertThat(jsonObject).isNotNull();
        assertThat(JsonUtils.isPersonAddressMultiNode(jsonObject)).isEqualTo(false);

        String flipSidePayload = "{\n" +
                "        \"patient\": {\n" +
                "                \"patient.family_name\": \"Maina\",\n" +
                "                \"patient.given_name\": \"Kajwang\",\n" +
                "                \"patient.middle_name\": \"Milly\",\n" +
                "                \"patient.sex\": \"F\",\n" +
                "                \"patient.birthdate_estimated\": \"...\",\n" +
                "                \"patient.personattribute^1\": {\n" +
                "                        \"attribute_value\": \"0733445566\",\n" +
                "                        \"attribute_type_name\": \"Contact Phone Number\",\n" +
                "                        \"attribute_type_uuid\": \"8037ba06-fc79-4244-9d14-687baa44bd81\"\n" +
                "                },\n" +
                "                \"patient.personattribute^2\": {\n" +
                "                        \"attribute_value\": \"Ayuma\",\n" +
                "                        \"attribute_type_name\": \"Mother's Name\",\n" +
                "                        \"attribute_type_uuid\": \"8d871d18-c2cc-11de-8d13-0010c6dffd0f\"\n" +
                "                },\n" +
                "                \"patient.personaddress^1\": {\n" +
                "                                \"countyDistrict\": \"county1\",\n" +
                "                                \"address6\": \"location1\",\n" +
                "                                \"address5\": \"sublocation1\",\n" +
                "                                \"cityVillage\": \"village1\"\n" +
                "                        },\n" +
                "\t\t\t\"patient.personaddress^2\": {\n" +
                "                                \"countyDistrict\": \"county2\",\n" +
                "                                \"address6\": \"location2\",\n" +
                "                                \"address5\": \"sublocation2\",\n" +
                "                                \"cityVillage\": \"village2\"\n" +
                "                        },\n" +
                "                \"patient.medical_record_number\": {\n" +
                "                        \"identifier_value\": \"555555555-5\",\n" +
                "                        \"identifier_type_uuid\": \"12345\"\n" +
                "                },\n" +
                "                \"patient.otheridentifier\": [\n" +
                "                        {\n" +
                "                                \"identifier_type_name\": \"KENYAN NATIONAL ID NUMBER\",\n" +
                "                                \"identifier_value\": \"2233\",\n" +
                "                                \"confirm_other_identifier_value\": \"2233\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                                \"identifier_type_name\": \"CCC Number\",\n" +
                "                                \"identifier_value\": \"456\",\n" +
                "                                \"confirm_other_identifier_value\": \"456\"\n" +
                "                        }\n" +
                "                ]\n" +
                "        },\n" +
                "        \"tmp\": {\n" +
                "                \"tmp.birthdate_type\": \"age\",\n" +
                "                \"tmp.age_in_years\": \"20\"\n" +
                "        },\n" +
                "        \"encounter\": {\n" +
                "                \"encounter.location_id\": \"8\",\n" +
                "                \"encounter.provider_id_select\": \"3356-3\",\n" +
                "                \"encounter.provider_id\": \"3356-3\",\n" +
                "                \"encounter.encounter_datetime\": \"04-09-2017\"\n" +
                "        }\n" +
                "}";

        JSONObject jsonObject1 = (JSONObject) JsonUtils.readAsObject(flipSidePayload, "patient");

        assertThat(JsonUtils.isPersonAddressMultiNode(jsonObject1)).isEqualTo(true);
    }

    @Test
    public void readAsObjectListTest() throws Exception {
        String jsonSamplePayloadToReadFrom = "[" +
                "{\"key1\":\"hello\"}," +
                "{\"key2\":\"hello again\"}" +
                "]";
        logger.debug("Executing Read as Object List Test");
        List<Object> valueList = JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$");
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$")).isInstanceOf(List.class);
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$").iterator().next()).isInstanceOf(JSONObject.class);
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$").size()).isGreaterThan(0);
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$").size()).isEqualTo(2);
        logger.debug("Read value matches value in JsonPayload as described, Read value in payload " + JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$").toArray());
    }

    @Test
    public void writeAsDateTest() throws Exception {
        logger.debug("Executing Write as Date Test");
        String dateSamplePayload = "{\"key\":\"04-06-1994\"}";

        JSONObject jsonObject = (JSONObject)JsonUtils.readAsObject(dateSamplePayload,"$");
        Date dateValue = JsonUtils.readAsDate(dateSamplePayload, "key");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,6);
        calendar.set(Calendar.DAY_OF_MONTH,4);
        calendar.set(Calendar.YEAR,1995);

        Date date = new SimpleDateFormat("dd-MM-yyyy").parse("04-06-1996");
        JsonUtils.writeAsDate(jsonObject,"key",date);

        assertThat(JsonUtils.readAsDate(jsonObject.toString(), "key")).isNotNull();
        Date stubDate = new SimpleDateFormat("dd-MM-yyyyy").parse("04-06-1996");
        Date wrongStubDate = new SimpleDateFormat("dd-MM-yyyyy").parse("04-06-1996");

                    //TODO integrate jav 8 lambda style conditionals.
//        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).hasDayOfMonth(4);
//        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).hasMonth(6);
//        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).hasYear(1995);
//        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).doesNotHave( e -> Calendar.MONTH == 2);

        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).isEqualTo(stubDate);
        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).isNotSameAs(wrongStubDate);
        assertThat(JsonUtils.readAsDate(jsonObject.toString(),"key")).isEqualTo(stubDate);
        assertThat(JsonUtils.readAsDate(jsonObject.toString(), "key")).isNotEqualTo(new SimpleDateFormat("04-01-1784"));

        logger.debug("Read value matches value in JsonPayload as described,- " + JsonUtils.readAsDate(jsonObject.toString(),"key"));
    }

    @Test
    public void readAsDateTest() throws Exception {
        logger.debug("Executing Read as Data ");
        String datePayload = "{\"key\":\"04-06-1994\"}";
        Date value = JsonUtils.readAsDate(datePayload, "key");
        assert value != null;
        assertThat(JsonUtils.readAsDate(datePayload,"key")).hasDayOfMonth(4);
        assertThat(JsonUtils.readAsDate(datePayload,"key")).hasMonth(6);
        assertThat(JsonUtils.readAsDate(datePayload,"key")).hasYear(1994);
      //  assertEquals("04-06-1994", new SimpleDateFormat("dd-MM-yyyy").parse(value.toString()));
        Assertions.assertThat(value).isNotNull();
        logger.debug("Expected to read value of parsed value ,1984-04-16 06:15:00 has returned " + JsonUtils.readAsDate(datePayload, "key"));
    }

}