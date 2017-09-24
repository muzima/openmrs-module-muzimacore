/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.exception.InvalidSettingException;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.MuzimaSettingDataType;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * This class defines handler methods that map web requests for accessing and managing mUzima Settings
 */

@Controller
@RequestMapping(value = "/module/muzimacore/setting.json")
public class MuzimaSettingController {
    private final Log log = LogFactory.getLog(this.getClass());
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSetting(final @RequestParam(value = "uuid") String uuid) {
        MuzimaSetting setting = null;
        if (Context.isAuthenticated()) {
            MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
            setting = settingService.getMuzimaSettingByUuid(uuid);
        }
        return WebConverter.convertMuzimaSetting(setting);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveSetting(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            Object value = map.get("value");
            MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
            if (StringUtils.isNotBlank(uuid)) {
                try {
                    MuzimaSetting setting = settingService.getMuzimaSettingByUuid(uuid);
                    setting.setSettingValue(value);
                    settingService.saveMuzimaSetting(setting);
                } catch (InvalidSettingException e){
                    log.error("Cannot save setting.", e);
                }
            }
        }
    }

}
