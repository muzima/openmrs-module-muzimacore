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
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping(value = "/module/muzimacore/errors.json")
public class ErrorsController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getErrors(final @RequestParam(value = "search") String search,
                                         final @RequestParam(value = "pageNumber") Integer pageNumber,
                                         final @RequestParam(value = "pageSize") Integer pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            List<Object> objects = new ArrayList<Object>();
            for (ErrorData errorData : dataService.getPagedErrorData(search, pageNumber, pageSize)) {
                objects.add(WebConverter.convertErrorData(errorData));
            }
            response.put("totalItems", dataService.countErrorData(search).intValue());
            response.put("objects", objects);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public void reQueue(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            List<String> uuidList = (List<String>) map.get("uuidList");
            DataService dataService = Context.getService(DataService.class);
            for (String uuid : uuidList) {
                ErrorData errorData = dataService.getErrorDataByUuid(uuid);
                QueueData queueData = new QueueData(errorData);
                dataService.saveQueueData(queueData);
                dataService.purgeErrorData(errorData);
            }
        }
    }
}
