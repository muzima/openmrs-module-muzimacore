package org.openmrs.module.muzima.utils;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonUtilsTest {

    private String mockPayload;

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
    public void writeAsBoolean() throws Exception {

    }

    @Test
    public void readAsBoolean() throws Exception {
        String booleanPayload = "{\"key\":\"true\"}";
        Boolean value = JsonUtils.readAsBoolean(booleanPayload, "$.key");
        assertTrue(value);
    }

    @Test
    public void writeAsNumeric() throws Exception {
    }

    @Test
    public void readAsNumeric() throws Exception {
        String numericPayload = "{\"key\":\"1.0\"}";
        Double value = JsonUtils.readAsNumeric(numericPayload, "$.key");
        assertTrue(value.equals(1.0));
    }

    @Test
    public void writeAsString() throws Exception {
    }

    @Test
    public void readAsString() throws Exception {
        String numericPayload = "{\"key\":\"hello\"}";
        String value = JsonUtils.readAsString(numericPayload, "$.key");
        assertEquals(value, "hello");
    }

    @Test
    public void parseLinkedHashMapToJsonObject() throws Exception {

    }

    @Test
    public void writeAsDateTime() throws Exception {

    }

    @Test
    public void readAsDateTime() throws Exception {
        String currentDate = new Date().toString();
        String datePayload = "{\"key\":\"" + currentDate + "\"}";
        Date value = JsonUtils.readAsDateTime(datePayload, "$.key");
        assertEquals(new Date(), value);
        Assertions.assertThat(value).isNotNull();
    }

    @Test
    public void readAsObject() throws Exception {
        String samplePayload = "{\"key\":\"hello\"}";
        Object value = JsonUtils.readAsObject(samplePayload, "$.key");
        assertTrue(value instanceof Object);
        Assertions.assertThat(value).isNotNull();
    }

    @Test
    public void toJsonArray() throws Exception {

    }

    @Test
    public void isPathAJSONArray() throws Exception {
        String jsonArrayPayload = "\"payload\":[" +
                "{\"key\":\"hello\"}," +
                "{\"key\":\"hello\"}" +
                "]";
        assertTrue(JsonUtils.isPathAJSONArray("$.payload"));

        String samplePayload = "{\"key\":\"hello\"}";
        assertFalse(JsonUtils.isPathAJSONArray(jsonArrayPayload));
    }

    @Test
    public void isPersonAddressMultiNode() throws Exception {

    }

    @Test
    public void readAsObjectList() throws Exception {
    }

    @Test
    public void writeAsDate() throws Exception {
    }

    @Test
    public void readAsDate() throws Exception {
        String currentDate = new Date().toString();
        String datePayload = "{\"key\":\"" + currentDate + "\"}";
        Date value = JsonUtils.readAsDate(datePayload, "$.key");
        assertEquals(new Date(), value);
        Assertions.assertThat(value).isNotNull();
    }

}