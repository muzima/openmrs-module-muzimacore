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

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.CohortDefinitionDataService;
import org.openmrs.module.muzima.api.model.CohortDefinitionData;
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
public class CohortsController {
    @RequestMapping(method = RequestMethod.GET,value = "/module/muzimacore/cohorts.json")
    @ResponseBody
    public Map<String, Object> getAllCohorts(){
        Map<String, Object> response = new HashMap<String, Object>();
        List<Object> objects = new ArrayList<Object>();
        for(Cohort cohort : Context.getCohortService().getAllCohorts()) {
            objects.add(WebConverter.convertMuzimaCohort(cohort));
        }
        response.put("objects", objects);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/module/muzimacore/cohortswithoutdefinition.json")
    @ResponseBody
    public Map<String, Object> getCohortsWithoutDefinition(){
        Map<String, Object> response = new HashMap<String, Object>();
        List<Object> objects = new ArrayList<Object>();
        List<Cohort> cohortsWithDefinition = new ArrayList<Cohort>();
        CohortDefinitionDataService expandedCohortDataService = Context.getService(CohortDefinitionDataService.class);

        for(CohortDefinitionData cohortDefinitionData : expandedCohortDataService.getAllCohortDefinitionData()) {
            cohortsWithDefinition.add(Context.getCohortService().getCohort(cohortDefinitionData.getCohortId()));
        }
        for(Cohort cohort : Context.getCohortService().getAllCohorts()) {
            if(!cohortsWithDefinition.contains(cohort)) {
                objects.add(WebConverter.convertMuzimaCohort(cohort));
            }
        }
        response.put("objects",objects);
        return response;
    }
}
