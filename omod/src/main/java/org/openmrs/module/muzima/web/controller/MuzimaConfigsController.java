/*
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
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
public class MuzimaConfigsController {

    @RequestMapping(value = "/module/muzimacore/configs.json", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getConfigs(final @RequestParam(value = "search") String search,
                                         final @RequestParam(value = "pageNumber") Integer pageNumber,
                                         final @RequestParam(value = "pageSize") Integer pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();

        if (Context.isAuthenticated()) {
            MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
            int pages = (configService.countConfigs(search).intValue() + pageSize - 1) / pageSize;
            List<Object> objects = new ArrayList<Object>();
            for (MuzimaConfig config : configService.getPagedConfigs(search, pageNumber, pageSize,null)) {
                objects.add(WebConverter.convertMuzimaConfig(config));
            }

            response.put("pages", pages);
            response.put("totalItems", configService.countConfigs(search).intValue());
            response.put("objects", objects);
        }
        return response;
    }

    @RequestMapping(value = "/module/muzimacore/configCount.json", method = RequestMethod.GET)
    @ResponseBody
    public int getConfigCount() {
        MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
        return configService.countConfigs("").intValue();
    }
}