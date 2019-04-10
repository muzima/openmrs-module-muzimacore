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
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.web.utils.WebConverter;
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
@RequestMapping(value = "/module/muzimacore/source.json")
public class SourceController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSource(final @RequestParam(value = "uuid") String uuid) {
        DataSource dataSource = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            dataSource = dataService.getDataSourceByUuid(uuid);
        }
        return WebConverter.convertDataSource(dataSource);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void deleteSource(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            DataService dataService = Context.getService(DataService.class);
            if (StringUtils.isNotBlank(uuid)) {
                DataSource dataSource = dataService.getDataSourceByUuid(uuid);
                if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(description)) {
                    dataSource.setName(name);
                    dataSource.setDescription(description);
                    dataService.saveDataSource(dataSource);
                } else {
                    dataSource.setRetired(true);
                    dataSource.setRetireReason("Deleting a data source object!");
                    dataService.saveDataSource(dataSource);
                }
            } else {
                DataSource dataSource = new DataSource();
                dataSource.setName(name);
                dataSource.setDescription(description);
                dataService.saveDataSource(dataSource);
            }
        }
    }

}
