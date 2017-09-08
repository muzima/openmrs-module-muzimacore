package org.openmrs.module.muzima.handler;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.muzima.utils.JsonUtils;

/**
 * Created by HACKER on 07/09/2017.
 */
public class JsonUtilsTest {
    @Test
    public void isPathAJSONArrayTest(){
        String payload = "\"personaddress\": {\n" +
                "                        \"countyDistrict\": \"county\",\n" +
                "                        \"address6\": \"location\",\n" +
                "                        \"address5\": \"sublocation\",\n" +
                "                        \"cityVillage\": \"village\"\n" +
                "                        }";
        Object testPath = JsonUtils.readAsObject(payload,"$[personaddress]");
        Assert.assertFalse(JsonUtils.isPathAJSONArray(testPath));
    }

    @Test
    public  void isPathAJSONArrayNegativeTest(){
        String payload = "\"patient.personaddress\": [\n" +
                "                        {\n" +
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
                "                ]";

        Object testPath = JsonUtils.readAsObject(payload,"$['patient.personaddress']['patient.countyDistrict']");
        Assert.assertFalse(JsonUtils.isPathAJSONArray(testPath));
    }
}
