/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;
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
@RequestMapping(value = "module/muzimacore/registrations.json")
public class RegistrationsController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getRegistrations(final @RequestParam(value = "pageNumber") Integer pageNumber,
                                                   final @RequestParam(value = "pageSize") Integer pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();
        RegistrationDataService service = Context.getService(RegistrationDataService.class);

        int pages = (service.countRegistrationData().intValue() + pageSize - 1) / pageSize;

        List<Object> objects = new ArrayList<Object>();
        for (RegistrationData registrationData : service.getRegistrationData(pageNumber, pageSize)) {
            objects.add(WebConverter.convertRegistrationData(registrationData));
        }
        response.put("pages", pages);
        response.put("objects", objects);
        return response;
    }
}
