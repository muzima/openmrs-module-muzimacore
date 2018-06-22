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
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.model.ReportConfiguration;
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
@RequestMapping(value = "/module/muzimacore/reportConfig.json")
public class ReportConfigurationController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getReportConfiguration(final @RequestParam(value = "uuid") String uuid) {
        ReportConfiguration reportConfiguration = null;
        if (Context.isAuthenticated()) {
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
        }
        return WebConverter.convertMuzimaReportConfiguration(reportConfiguration);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void deleteReportConfiguration(final @RequestBody Map<String, Object> map) {
        System.out.println("ssssss111111111111111111");
        if (Context.isAuthenticated()) {
            System.out.println("sssssss22222222222222222222");
            String uuid = (String) map.get("uuid");
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            if (StringUtils.isNotBlank(uuid)) {
                Integer reportId =  (Integer)map.get("reportId");
                System.out.println("sssssss3333333333333333333333333333333");
                Integer cohortId =  (Integer)map.get("cohortId");
                ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
                if (reportId !=0 && cohortId !=0){
                    System.out.println("sssssss4444444444444444444444444444");
                    reportConfiguration.setReportId(reportId);
                    reportConfiguration.setCohortId(cohortId);
                    reportConfigurationService.saveReportConfiguration(reportConfiguration);
                } else {
                    System.out.println("sssssss5555555555555555555555555555");
                    reportConfiguration.setRetired(true);
                    reportConfiguration.setRetireReason("Deleting a data source object!");
                    reportConfigurationService.saveReportConfiguration(reportConfiguration);
                }
            } else {
                String reportId =  (String)map.get("reportId");
                String cohortId = (String)map.get("cohortId");
                System.out.println("sssssssss666666666666666666666666666");
                ReportConfiguration reportConfiguration = new ReportConfiguration();
                reportConfiguration.setReportId(Integer.parseInt(reportId));
                reportConfiguration.setCohortId(Integer.parseInt(cohortId));
                reportConfigurationService.saveReportConfiguration(reportConfiguration);
            }
        }
    }

}
