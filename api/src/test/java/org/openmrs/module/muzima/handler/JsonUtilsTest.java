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
        String payload = "{\n" +
                "\t\"patient\": {\n" +
                "\t\t\"personaddress\": {\n" +
                "\t\t\t\"countyDistrict\": \"county\",\n" +
                "\t\t\t\"address6\": \"location\",\n" +
                "\t\t\t\"address5\": \"sublocation\",\n" +
                "\t\t\t\"cityVillage\": \"village\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        Object testPath = JsonUtils.readAsObject(payload,"$['patient']['personaddress']");
        Assert.assertFalse(JsonUtils.isPathAJSONArray(testPath));
    }

    @Test
    public  void isPathAJSONArrayNegativeTest(){
        String payload = "{\n" +
                "\t\"patient\": {\n" +
                "\t\t\"personaddress\": [{\n" +
                "\t\t\t\t\"countyDistrict\": \"county1\",\n" +
                "\t\t\t\t\"address6\": \"location1\",\n" +
                "\t\t\t\t\"address5\": \"sublocation1\",\n" +
                "\t\t\t\t\"cityVillage\": \"village1\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"countyDistrict\": \"county2\",\n" +
                "\t\t\t\t\"address6\": \"location2\",\n" +
                "\t\t\t\t\"address5\": \"sublocation2\",\n" +
                "\t\t\t\t\"cityVillage\": \"village2\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";

        Object testPath = JsonUtils.readAsObject(payload,"$['patient']['personaddress']");
        Assert.assertTrue(JsonUtils.isPathAJSONArray(testPath));
    }
}
