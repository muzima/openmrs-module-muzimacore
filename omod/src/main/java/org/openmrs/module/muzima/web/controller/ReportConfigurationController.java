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
import org.openmrs.Cohort;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.openmrs.module.muzima.task.MuzimaReportProcessor;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages single cohortReportConfiguration
 */
@Controller
public class ReportConfigurationController {
    
    /**
     * This method fetches single reportConfiguration
     * @return reportConfiguration relevant to a uuid
     * @param uuid reportConfiguration uuid
     */
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
    
    /**
     * This method fetches report design
     * @return report design json relevant to a reportConfiguration is sent to the view
     * @param uuid reportConfiguration uuid
     */
    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/reportConfig/report.json", method = RequestMethod.GET)
    public Map<String, Object> getReportDesignForReportConfiguration(final @RequestParam(value = "uuid") String uuid) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
    
            ReportDesign reportDesign = Context.getService(ReportService.class).getReportDesignByUuid(reportConfiguration.getReportDesignUuid());
            List<Object> objects = new ArrayList<Object>();
            objects.add(WebConverter.convertMuzimaReport(reportDesign));
            response.put("objects", objects);
        }
        return response;
    }
    /**
     * This method saves or updates report configuration
     * @return cohort relevant to a reportConfiguration is sent to the view
     * @param uuid reportConfiguration uuid
     */
    @RequestMapping(value = "/module/muzimacore/reportConfig/singleCohort.json", method = RequestMethod.GET)
    public Map<String, Object> getCohortForReportConfiguration(final @RequestParam(value = "uuid") String uuid) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            ReportConfiguration reportConfiguration= reportConfigurationService.getReportConfigurationByUuid(uuid);
    
            Cohort cohort= Context.getService(CohortService.class).getCohortByUuid(reportConfiguration.getCohortUuid());
            List<Object> objects = new ArrayList<Object>();
            objects.add(WebConverter.convertMuzimaCohort(cohort));
            response.put("objects", objects);
        }
        return response;
    }
    /**
     * This method saves or updates report configuration
     * @param map reportConfiguration json
     */
    @RequestMapping(value = "/module/muzimacore/reportConfig.json", method = RequestMethod.POST)
    public void saveReportConfiguration(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            Boolean priority = (Boolean) map.get("priority");
            if(priority ==null){
                priority = false;
            }
            
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            if (StringUtils.isNotBlank(uuid)) {
                String cohortUuid =  (String) map.get("cohortUuid");
                String reportConfigJson = (String)map.get("reportConfigJson");
                String [] reports = reportConfigJson.substring(12,reportConfigJson.length()-2).split("}");
    
                
                ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
                    reportConfiguration.setCohortUuid(cohortUuid);
    
                    for(String report:reports){
                        Integer index1 = report.indexOf("uuid");
                        String s1 = report.substring(index1+7,report.length()-1);
                        reportConfiguration.setReportDesignUuid(s1);
                        reportConfiguration.setCohortUuid(cohortUuid);
                        reportConfiguration.setPriority(priority);
                    }
            } else {
              
                String cohortUuid = (String) map.get("cohortUuid");
                String reportConfigJson = (String)map.get("reportConfigJson");
                String [] reports = reportConfigJson.substring(12,reportConfigJson.length()-2).split("}");
                
                for(String report:reports){
                    Integer index1 = report.indexOf("uuid");
                    String s1 = report.substring(index1+7,report.length()-1);
                    ReportConfiguration reportConfiguration = new ReportConfiguration();
                    reportConfiguration.setReportDesignUuid(s1);
                    reportConfiguration.setCohortUuid(cohortUuid);
                    reportConfiguration.setPriority(priority);
                }
            }
        }
    }
    /**
     * This method deletes report configuration
     * @param map reportConfiguration uuid
     */
    @RequestMapping(value = "/module/muzimacore/delete/reportConfig.json", method = RequestMethod.POST)
    public void deleteReportConfiguration(final @RequestBody Map<String, Object> map) {
            String uuid = (String) map.get("uuid");
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
    
            reportConfiguration.setRetired(true);
            reportConfiguration.setRetireReason("Deleting a data source object!");
            reportConfiguration.setDateRetired(new Date());
            reportConfigurationService.saveReportConfiguration(reportConfiguration);
        
    }
    
    /**
     * @return This method returns report designs when creating new report configuration
     * @param search the query sent by the user to find reportDesigns
     */
    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/reportConfigReports.json", method = RequestMethod.GET)
    public Map<String, Object> searchReportDesignsForReportConfiguration(final @RequestParam(value = "search") String search) {
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
