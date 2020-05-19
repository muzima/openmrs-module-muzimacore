package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.MuzimaSettingDataType;

public class MuzimaSettingUtils {
    public static MuzimaSetting parseMuzimaSettingFromJsonObject(JSONObject settingObject){
        MuzimaSetting muzimaSetting = new MuzimaSetting();
        muzimaSetting.setProperty((String)settingObject.get("property"));

        if(settingObject.containsKey("uuid")) {
            muzimaSetting.setUuid((String) settingObject.get("uuid"));
        }

        if(settingObject.containsKey("name")) {
            muzimaSetting.setName((String) settingObject.get("name"));
        }

        if(settingObject.containsKey("description")) {
            muzimaSetting.setDescription((String) settingObject.get("description"));
        }

        if(settingObject.containsKey("datatype") ) {
            String settingDataType = (String)settingObject.get("datatype");
            muzimaSetting.setSettingDataType(MuzimaSettingDataType.getSettingDataTypeForString(settingDataType));
            if(settingObject.containsKey("value")) {
                if ("BOOLEAN".equals(settingDataType)) {
                    muzimaSetting.setValueBoolean((Boolean) settingObject.get("value"));
                } else {
                    muzimaSetting.setValueString((String) settingObject.get("value"));
                }
            }
        }
        return muzimaSetting;
    }
}
