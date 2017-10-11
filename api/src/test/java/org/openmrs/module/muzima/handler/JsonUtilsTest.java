package org.openmrs.module.muzima.handler;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.muzima.utils.JsonUtils;

/**
 * Created by HACKER on 07/09/2017.
 */
public class JsonUtilsTest {

    @Test
    public void isPathAJSONArrayTest(){
        String payload = "{" +
                "\"patient\": {" +
                "\"personaddress\": {" +
                "\"countyDistrict\": \"county\"," +
                "\"address6\": \"location\"," +
                "\"address5\": \"sublocation\"," +
                "\"cityVillage\": \"village\"" +
                "}" +
                "}" +
                "}";
        Object testPath = JsonUtils.readAsObject(payload,"$['patient']['patient.personaddress']");
        Assert.assertFalse(JsonUtils.isPathAJSONArray(testPath));
    }

    @Test
    public  void isPathAJSONArrayNegativeTest(){
        String payload = "{" +
                "\"patient\": {" +
                "\"patient.personaddress\": [{" +
                "\"countyDistrict\": \"county1\"," +
                "\"address6\": \"location1\"," +
                "\"address5\": \"sublocation1\"," +
                "\"cityVillage\": \"village1\"" +
                "}," +
                "{" +
                "\"countyDistrict\": \"county2\"," +
                "\"address6\": \"location2\"," +
                "\"address5\": \"sublocation2\"," +
                "\"cityVillage\": \"village2\"" +
                "}" +
                "]" +
                "}" +
                "}";

        Object testPath = JsonUtils.readAsObject(payload,"$['patient']['patient.personaddress']");
        Assert.assertTrue(JsonUtils.isPathAJSONArray(testPath));
    }
}
