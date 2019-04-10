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

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.openmrs.module.muzima.web.utils.XmlJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
@RequestMapping(value = "/module/muzimacore/error-data/{uuid}.json")
public class EditController {

    private static final Logger logger = LoggerFactory.getLogger(EditController.class.getSimpleName());

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEdits(final @RequestParam(value = "uuid") String uuid) {
        ErrorData errorData = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            errorData = dataService.getErrorDataByUuid(uuid);
        }
        return WebConverter.convertEditRegistrationData(errorData);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public void reQueue(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            try {
                String xmlPayload = XmlJsonUtil.createXmlFromJson(map.get("formData").toString());
                String recordUuid = XmlJsonUtil.readAsString(map.get("formData").toString(), "record_uuid");
                DataService dataService = Context.getService(DataService.class);
                ErrorData errorData = dataService.getErrorDataByUuid(recordUuid);
                errorData.setPayload(xmlPayload);
                dataService.saveErrorData(errorData);
            } catch (Exception e) {
                logger.error("Error parsing json file to create xml.", e);
            }
        }
    }
}
