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
import org.openmrs.module.muzima.api.CohortDefinitionDataService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/module/muzimacore/cohortDefinitions.json")
public class CohortDefinitionsController{
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllExpandedCohortDefinitions(){
        Map<String, Object> response = new HashMap<String, Object>();
        CohortDefinitionDataService expandedCohortDataService = Context.getService(CohortDefinitionDataService.class);
        List<Object> objects = new ArrayList<Object>();
        for(CohortDefinitionData cohortCriteriaData : expandedCohortDataService.getAllCohortDefinitionData()) {
            objects.add(WebConverter.convertCohortDefinitionData(cohortCriteriaData));
        }
        response.put("objects", objects);
        return response;
    }
}
