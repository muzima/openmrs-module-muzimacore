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
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.ReportConfiguration;
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

@Controller
public class ReportConfigurationsController {

    @RequestMapping(value = "/module/muzimacore/reportConfigs.json", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getReportConfigurations(final @RequestParam(value = "search") String search,
                                          final @RequestParam(value = "pageNumber") Integer pageNumber,
                                          final @RequestParam(value = "pageSize") Integer pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            int pages = (reportConfigurationService.countReportConfigurations(search).intValue() + pageSize - 1) / pageSize;
            List<Object> objects = new ArrayList<Object>();
            for (ReportConfiguration reportConfiguration : reportConfigurationService.getPagedReportConfigurations(search, pageNumber, pageSize)) {
                objects.add(WebConverter.convertMuzimaReportConfiguration(reportConfiguration));
            }
            response.put("pages", pages);
            response.put("objects", objects);
        }
      return response;
    }

    @RequestMapping(value = "/module/muzimacore/countReportConfigurations.json", method = RequestMethod.GET)
    @ResponseBody
    public int countReportConfigurations() {
        ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
        return reportConfigurationService.countReportConfigurations().intValue();
    }
}
