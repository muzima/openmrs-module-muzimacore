package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
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
    public void writeAsBooleanTest() throws Exception {

        logger.info("Executing Write as Boolean Test:");
        String stringPayload = "{\"rootkey\":{\"key\":\"true\"}}";
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(stringPayload, "$.rootkey");
        JsonUtils.writeAsBoolean(jsonObject, "$.key", false);
        assertThat(jsonObject.entrySet().contains(false));
        assertThat(JsonUtils.readAsBoolean(jsonObject.toJSONString(), "$.rootkey.key")).isEqualTo(false);
        assertThat(JsonUtils.readAsBoolean(jsonObject.toJSONString(), "$.rootkey.key")).isNotEqualTo(true);
        assertThat(jsonObject.keySet().contains("rootkey"));
        logger.info("Write as Boolean Test: JsonObject " + jsonObject.toJSONString() + " Initial value in payload replaced by " + JsonUtils.readAsBoolean(jsonObject.toJSONString(), "$.rootkey.key"));
    }

    @Test
    public void readAsBooleanTest() throws Exception {
        logger.info("Executing Read as Boolean Test");
        String booleanPayload = "{\"key\":\"true\"}";
        Boolean value = JsonUtils.readAsBoolean(booleanPayload, "$.key");
        assertTrue(value);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isEqualTo(value);
        assertThat(JsonUtils.readAsBoolean(booleanPayload, "$.key")).isInstanceOf(Boolean.class);
        logger.info("Read value matches value in JsonPayload as described, Read Value" + value + ", Value in Paload " + JsonUtils.readAsBoolean(booleanPayload, "$.key"));
    }

    @Test
    public void writeAsNumericTest() throws Exception {
        logger.info("Executing Write as Numeric Test");
        String payloadValueToWrite = "\"rootkey\":{\"key\":1.0}";
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(payloadValueToWrite, "$.rootkey");
        JsonUtils.writeAsNumeric(jsonObject, "$.rootkey.key", 3.0);
        Double doubleValue = JsonUtils.readAsNumeric(jsonObject.toJSONString(), "$.rootkey.key");
        assertThat(JsonUtils.readAsNumeric(jsonObject.toJSONString(), "$.rootkey.key")).isEqualTo(3.0);
        assertThat(JsonUtils.readAsNumeric(jsonObject.toJSONString(), "$.rootkey.key")).isNotEqualTo(1.0);
        logger.info("Write as Numeric Test: JsonObject " + jsonObject.toJSONString() + ", 1.0; Initial value in payload replaced by " + JsonUtils.readAsNumeric(jsonObject.toJSONString(), "$.rootkey.key"));

    }

    @Test
    public void readAsNumericTest() throws Exception {
        logger.info("Executing Read as Numeric Test");
        String numericPayload = "{\"key\":1.0}";
        Double value = JsonUtils.readAsNumeric(numericPayload, "$.key");
        assertTrue(value.equals(1.0));
        assertThat(JsonUtils.readAsNumeric(numericPayload, "$.key")).isEqualTo(1.0);
        assertThat(JsonUtils.readAsNumeric(numericPayload, "$.key")).isInstanceOf(Double.class);
        logger.info("Read value matches value in JsonPayload as described, Read value" + value + ", value in Payload " + JsonUtils.readAsNumeric(numericPayload, "$.key"));
    }

    @Test
    public void writeAsStringTest() throws Exception {
        logger.info("Executing Write as String Test");
        String stringValuePayload = "{\"key\":\"hello\"}";
        String value = JsonUtils.readAsString(stringValuePayload, "$.key");
        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(stringValuePayload, "$.key");
        JsonUtils.writeAsString(jsonObject, "$.key", "Updated Hello");
        assertThat(JsonUtils.readAsString(stringValuePayload, "$.key")).isInstanceOf(String.class);
        assertThat(JsonUtils.readAsString(stringValuePayload, "$.key")).isEqualTo(new String("Updated Hello"));
        logger.info("Write as String Test: JsonObject " + jsonObject.toJSONString() + "; Initial value in payload replaced by " + JsonUtils.readAsString(jsonObject.toJSONString(), "$.key"));

    }

    @Test
    public void readAsStringTest() throws Exception {
        logger.info("Executing Read as String Test");
        String stringPayload = "{\"key\":\"hello\"}";
        String value = JsonUtils.readAsString(stringPayload, "$.key");
        assertEquals(value, "hello");
        assertThat(JsonUtils.readAsString(stringPayload, "$.key")).isInstanceOf(String.class);
        assertThat(JsonUtils.readAsString(stringPayload, "$.key")).isEqualTo("Hello");
        assertThat(JsonUtils.readAsString(stringPayload, "$.key")).isNotNull();
        logger.info("Read value matches value in JsonPayload as described, Read value in payload " + JsonUtils.readAsString(stringPayload, "$.key"));
    }

    @Test
    public void parseLinkedHashMapToJsonObjectTest() throws Exception {

    }

    @Test
    //@Ignore //TODO - determine the workflow employeed for DateTime usage in muzima- to facilitate testing
    public void writeAsDateTimeTest() throws Exception {
        logger.info("Executing Write as DateTime Test");
        String dateTimeSamplePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date dateTimeValue = JsonUtils.readAsDateTime(dateTimeSamplePayload, "$.key");
        assertThat(JsonUtils.readAsDateTime(dateTimeSamplePayload, "$.key")).isNotNull();
        assertThat(JsonUtils.readAsDateTime(dateTimeSamplePayload, "$.key")).isEqualTo("1984-04-16 06:15:00");
        logger.info("Read value matches value in JsonPayliod as described,- " + JsonUtils.readAsDateTime(dateTimeSamplePayload, "$.key"));
    }

    @Test
    public void readAsDateTimeTest() throws Exception {
        logger.info("Executing Read as DataTime ");
        String currentDate = new Date().toString();
        String datePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date value = JsonUtils.readAsDateTime(datePayload, "$.key");
        assertEquals("1984-04-16 06:15:00", value.toString());
        Assertions.assertThat(value).isNotNull();
        logger.info("Expected to read value of 1984-04-16 06:15:00 returned " + JsonUtils.readAsDateTime(datePayload, "$.key"));
    }

    @Test
    public void readAsObjectTest() throws Exception {
        logger.info("Executing Read as Object Test");
        String samplePayload = "{\"key\":\"hello\"}";
        Object value = JsonUtils.readAsObject(samplePayload, "$.key");
        assertTrue(value instanceof Object);
        Assertions.assertThat(value).isNotNull();
        assertThat(value).isEqualTo("hello");
        assertThat(value).isInstanceOf(String.class);
        logger.info("Expected to read value of expected - [hello], call returned " + JsonUtils.readAsObject(samplePayload, "$.key"));
    }

    @Test
    public void isPathAJSONArrayTest() throws Exception {
        logger.info("Executing isPathAJSONArrayTest");
        String jsonArrayPayload = "\"payload\":[" +
                "{\"key\":\"hello\"}," +
                "{\"key\":\"hello\"}" +
                "]";
        assertThat(JsonUtils.isPathAJSONArray("$.payload")).isEqualTo(true);
        String samplePayload = "{\"key\":\"hello\"}";
        assertThat(JsonUtils.isPathAJSONArray(samplePayload)).isEqualTo(false);
        logger.info("Test exectued against sample payload " + jsonArrayPayload);
    }

    @Test
    public void isPersonAddressMultiNodeTest() throws Exception {
        String jsonSamplePayloadToReadFrom = " \"patient\": {\n" +
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
                "        }";

        JSONObject jsonObject = (JSONObject) JsonUtils.readAsObject(jsonSamplePayloadToReadFrom,"$.patient");
        logger.info("Executing multinode check algorithm test");
        assertThat(JsonUtils.isPersonAddressMultiNode(jsonObject)).isEqualTo(true);

        String flipSidePayload = " {\n" +
                "        \"patient\": {\n" +
                "                \"personaddress\": [{\n" +
                "                                \"countyDistrict\": \"county1\",\n" +
                "                                \"address6\": \"location1\",\n" +
                "                                \"address5\": \"sublocation1\",\n" +
                "                                \"cityVillage\": \"village1\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                                \"countyDistrict\": \"county2\",\n" +
                "                                \"address6\": \"location2\",\n" +
                "                                \"address5\": \"sublocation2\",\n" +
                "                                \"cityVillage\": \"village2\"\n" +
                "                        }\n" +
                "                }";
        JSONObject jsonObject1  = (JSONObject) JsonUtils.readAsObject(flipSidePayload,"$.patient.personaddress");

        assertThat(JsonUtils.isPersonAddressMultiNode(jsonObject1)).isEqualTo(false);
    }

    @Test
    public void readAsObjectListTest() throws Exception {
        String jsonSamplePayloadToReadFrom = "\"array\":[" +
                "{\"key\":\"hello\"}," +
                "{\"key\":\"hello again\"}" +
                "]";
        logger.info("Executing Read as Object List Test");
        List<Object> valueList = JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array");
        assertEquals(valueList, Arrays.asList("hello", "hello again"));
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array")).isInstanceOf(List.class);
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array").iterator().next()).isEqualTo("hello");
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array").size()).isGreaterThan(0);
        assertThat(JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array").size()).isEqualTo(2);
        logger.info("Read value matches value in JsonPayload as described, Read value in payload " + JsonUtils.readAsObjectList(jsonSamplePayloadToReadFrom, "$.array").toArray());
    }

    @Test
    public void writeAsDateTest() throws Exception {
        logger.info("Executing Write as Date Test");
        String dateSamplePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date dateTimeValue = JsonUtils.readAsDate(dateSamplePayload, "$.key");
        assertThat(JsonUtils.readAsDateTime(dateSamplePayload, "$.key")).isNotNull();
        assertThat(JsonUtils.readAsDateTime(dateSamplePayload, "$.key")).isEqualTo("1984-04-16 06:15:00");
        logger.info("Read value matches value in JsonPayload as described,- " + JsonUtils.readAsDateTime(dateSamplePayload, "$.key"));
    }

    @Test
    public void readAsDateTest() throws Exception {
        logger.info("Executing Read as Data ");
        String currentDate = new Date().toString();
        String datePayload = "{\"key\":\"1984-04-16 06:15:00\"}";
        Date value = JsonUtils.readAsDate(datePayload, "$.key");
        assertEquals("1984-04-16 06:15:00", value.toString());
        Assertions.assertThat(value).isNotNull();
        logger.info("Expected to read value of 1984-04-16 06:15:00 returned " + JsonUtils.readAsDate(datePayload, "$.key"));
    }

}