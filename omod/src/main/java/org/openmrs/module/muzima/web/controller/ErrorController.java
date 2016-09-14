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
import org.openmrs.module.muzima.model.ErrorMessage;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
@RequestMapping(value = "/module/muzimacore/error.json")
public class ErrorController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getError(final @RequestParam(value = "uuid") String uuid) {
        ErrorData errorData = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            errorData = dataService.getErrorDataByUuid(uuid);
        }
        return WebConverter.convertErrorData(errorData);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Map<String, Object> saveEditedFormData(final @RequestParam(value = "uuid") String uuid,
                                   final @RequestParam(value = "formData") String formData){
        ErrorData errorData = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            ErrorData errorDataEdited = dataService.getErrorDataByUuid(uuid);
            errorDataEdited.setPayload(formData);
            List<ErrorMessage> errorMessages = dataService.validateData(uuid, formData);
            errorDataEdited.setErrorMessages(new HashSet<ErrorMessage>(errorMessages));
            errorData = dataService.saveErrorData(errorDataEdited);
        }
        return WebConverter.convertErrorData(errorData);
    }
}
