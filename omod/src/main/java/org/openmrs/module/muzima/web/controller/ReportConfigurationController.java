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
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
public class ReportConfigurationController {

    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/reportConfig.json", method = RequestMethod.GET)
    public Map<String, Object> getReportConfiguration(final @RequestParam(value = "uuid") String uuid) {
        ReportConfiguration reportConfiguration = null;
        if (Context.isAuthenticated()) {
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
        }
        return WebConverter.convertMuzimaReportConfiguration(reportConfiguration);
    }
    
    @RequestMapping(value = "/module/muzimacore/reportConfig.json", method = RequestMethod.POST)
    public void saveReportConfiguration(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            String cohortUuid = (String) map.get("cohortUuid");
            String reportConfigJson = (String) map.get("reportConfigJson");
            Boolean priority = (Boolean) map.get("priority");
            if (priority == null) priority = false;

            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            ReportConfiguration reportConfiguration;
            if (StringUtils.isNotBlank(uuid)) {
                reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
            } else {
                reportConfiguration = new ReportConfiguration();
                reportConfiguration.setCohortUuid(cohortUuid);
            }
            reportConfiguration.setReportDesigns(reportConfigJson);
            reportConfiguration.setPriority(priority);
            reportConfigurationService.saveReportConfiguration(reportConfiguration);
        }
    }

    @RequestMapping(value = "/module/muzimacore/delete/reportConfig.json", method = RequestMethod.POST)
    public void deleteReportConfiguration(final @RequestBody Map<String, Object> map) {
        String uuid = (String) map.get("uuid");
        ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
        ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);

        reportConfiguration.setRetired(true);
        reportConfiguration.setRetireReason("Deleted no longer needed configuration!");
        reportConfiguration.setDateRetired(new Date());
        reportConfigurationService.saveReportConfiguration(reportConfiguration);
    }
    
    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/reportConfigReports.json", method = RequestMethod.GET)
    public Map<String, Object> getReports(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();
        
        List<ReportDesign> reportDesigns = Context.getService(ReportService.class).getAllReportDesigns(true);
    
        if (Context.isAuthenticated()) {
            List<Object> objects = new ArrayList<Object>();
            for (ReportDesign reportDesign : reportDesigns) {
                objects.add(WebConverter.convertMuzimaReport(reportDesign));
            }
            response.put("objects", objects);
        }
        
        return response;
    }
}
