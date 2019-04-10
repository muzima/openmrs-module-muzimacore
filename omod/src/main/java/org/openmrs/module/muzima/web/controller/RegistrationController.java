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
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Registration rest controller class, http request made to URI module/muzimacore/registration.json are
 * delegated to this controller by spring DispatcherServlet.
 * This class is responsible for handling, that is, verifying, processing and returning if necessary a
 * httpResponse to the requests.
 */
@Controller
@RequestMapping(value = "module/muzimacore/registration.json")
public class RegistrationController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getRegistration(final @RequestParam(value = "uuid") String uuid) {
        RegistrationDataService service = Context.getService(RegistrationDataService.class);
        return WebConverter.convertRegistrationData(service.getRegistrationDataByUuid(uuid));
    }
}
