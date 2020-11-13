package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaConfig;
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

    public static MuzimaSetting getMuzimaSetting(String settingProperty,String setupConfigUuid){
        MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting muzimaSetting = null;
        if(StringUtils.isNotBlank(setupConfigUuid)){
            MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
            MuzimaConfig config = configService.getConfigByUuid(setupConfigUuid);
            if(config != null){
                muzimaSetting = config.getConfigMuzimaSettingByProperty(settingProperty);
            }
        }
        if(muzimaSetting == null){
            muzimaSetting = settingService.getMuzimaSettingByProperty(settingProperty);
        }

        return muzimaSetting;
    }
}
