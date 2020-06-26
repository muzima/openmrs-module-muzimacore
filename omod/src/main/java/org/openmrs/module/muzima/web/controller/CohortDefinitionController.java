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
import org.openmrs.module.muzima.api.service.CohortDefinitionDataService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

@Controller
public class CohortDefinitionController {

    @RequestMapping(value = "/module/muzimacore/cohortDefinition.json", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getCohortDefinitionData(final @RequestParam(value = "uuid") String uuid) {
        CohortDefinitionDataService expandedCohortDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = expandedCohortDataService.getCohortDefinitionDataByUuid(uuid);
        return WebConverter.convertCohortDefinitionData(cohortDefinitionData);
    }

    @RequestMapping(value = "/module/muzimacore/cohortDefinition.json", method = RequestMethod.POST)
    public void saveCohortDefinition(final @RequestBody Map<String, Object> map){
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            String definition = (String) map.get("definition");
            Integer cohortId = (Integer) map.get("cohortid");
            boolean isScheduled = (Boolean) map.get("isScheduledForExecution");
            boolean isMemberAdditionEnabled = (Boolean) map.get("isMemberAdditionEnabled");
            boolean isMemberRemovalEnabled = (Boolean) map.get("isMemberRemovalEnabled");
            boolean isFilterByProviderEnabled = (Boolean) map.get("isFilterByProviderEnabled");
            boolean isFilterByLocationEnabled = (Boolean) map.get("isFilterByLocationEnabled");
            String filterQuery = (String) map.get("filterQuery");
            String retireReason = (String) map.get("retireReason");

            CohortDefinitionDataService expandedCohortDataService = Context.getService(CohortDefinitionDataService.class);

            CohortDefinitionData cohortDefinitionData;
            if (StringUtils.isNotBlank(uuid)) {
                cohortDefinitionData = expandedCohortDataService.getCohortDefinitionDataByUuid(uuid);
                if (StringUtils.isNotBlank(retireReason)) {
                    cohortDefinitionData.setVoided(true);
                    cohortDefinitionData.setVoidReason(retireReason);
                    cohortDefinitionData.setVoidedBy(Context.getAuthenticatedUser());
                    cohortDefinitionData.setDateVoided(new Date());
                }
            } else {
                cohortDefinitionData = new CohortDefinitionData();
            }

            cohortDefinitionData.setCohortId(cohortId);
            cohortDefinitionData.setDefinition(definition);
            cohortDefinitionData.setIsScheduledForExecution(isScheduled);
            cohortDefinitionData.setIsMemberAdditionEnabled(isMemberAdditionEnabled);
            cohortDefinitionData.setIsMemberRemovalEnabled(isMemberRemovalEnabled);
            cohortDefinitionData.setIsFilterByProviderEnabled(isFilterByProviderEnabled);
            cohortDefinitionData.setIsFilterByLocationEnabled(isFilterByLocationEnabled);
            cohortDefinitionData.setFilterQuery(filterQuery);
            expandedCohortDataService.saveCohortDefinitionData(cohortDefinitionData);

        }

    }
    @RequestMapping(value = "/module/muzimacore/processCohortDefinition.json", method = RequestMethod.POST)
    public void processCohortDefinition(final @RequestBody Map<String, Object> map){
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            CohortDefinitionDataService expandedCohortDataService = Context.getService(CohortDefinitionDataService.class);
            if (StringUtils.isNotBlank(uuid)) {
               expandedCohortDataService.processCohortDefinitionData(uuid);
            }
        }
    }
}
