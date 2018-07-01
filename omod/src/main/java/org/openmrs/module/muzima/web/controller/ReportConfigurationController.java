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
import org.openmrs.module.muzima.task.MuzimaReportProcessor;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
    public void deleteReportConfiguration(final @RequestBody Map<String, Object> map) {
        System.out.println("ssssss111111111111111111");
        if (Context.isAuthenticated()) {
            System.out.println("sssssss22222222222222222222");
            String uuid = (String) map.get("uuid");
            ReportConfigurationService reportConfigurationService = Context.getService(ReportConfigurationService.class);
            if (StringUtils.isNotBlank(uuid)) {
                 String reportUuid = (String) map.get("reportUuid");
                System.out.println("sssssss3333333333333333333333333333333");
                String cohortUuid =  (String) map.get("cohortUuid");
                ReportConfiguration reportConfiguration = reportConfigurationService.getReportConfigurationByUuid(uuid);
                if (reportUuid !="" && cohortUuid !=""){
                    System.out.println("sssssss4444444444444444444444444444");
                    reportConfiguration.setReportUuid(reportUuid);
                    reportConfiguration.setCohortUuid(cohortUuid);
                    reportConfigurationService.saveReportConfiguration(reportConfiguration);
                } else {
                    System.out.println("sssssss5555555555555555555555555555");
                    reportConfiguration.setRetired(true);
                    reportConfiguration.setRetireReason("Deleting a data source object!");
                    reportConfigurationService.saveReportConfiguration(reportConfiguration);
                }
            } else {
              
                String cohortUuid = (String) map.get("cohortUuid");
                System.out.println("sssssssss666666666666666666666666666");
                String reportConfigJson = (String)map.get("reportConfigJson");
                System.out.println("sssssssss77777777777777777"+reportConfigJson.substring(12,reportConfigJson.length()-2));
                String [] reports = reportConfigJson.substring(12,reportConfigJson.length()-2).split("}");
                
                for(String report:reports){
                    System.out.println("fffffaaaaaaaaa  "+report);
                    Integer index1 = report.indexOf("uuid");
                    System.out.println("fffffbbbbbbbbbbbbbb  "+index1);
                    String s1 = report.substring(index1+7,report.length()-1);
                    System.out.println("fffffcccccccccccc  "+s1);
                    Integer index2 = s1.indexOf("}");
                    System.out.println("fffffdddddddddd   "+index2);
                    /*String reportUuid="";
                    if(index2 ==-1){
                        reportUuid = s1;
                    }
                    else{
                        reportUuid= s1.substring(1,index2-2);
                    }*/
                    //String 
                    System.out.println("fffff555555555"+s1);
                    ReportConfiguration reportConfiguration = new ReportConfiguration();
                    reportConfiguration.setReportUuid(s1);
                    reportConfiguration.setCohortUuid(cohortUuid);
                    //reportConfigurationService.saveReportConfiguration(reportConfiguration);
                }
            }
            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqConfigs are saved");
            MuzimaReportProcessor muzimaReportProcessor = new MuzimaReportProcessor();
            muzimaReportProcessor.processAllReports();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/reportConfigReports.json", method = RequestMethod.GET)
    public Map<String, Object> getReports(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();
    
        ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
        List<DefinitionSummary> definitionSummaries = rds.getAllDefinitionSummaries(true);
        List<ReportDesign> reportDesigns = Context.getService(ReportService.class).getAllReportDesigns(true);
    
    
        if (Context.isAuthenticated()) {
            /*List<Object> objects = new ArrayList<Object>();
            System.out.println("rrrrrrrrrrrrrrrrr11111111111111111Inside reports Design");
            ReportService rs = Context.getService(ReportService.class);
            System.out.println("rrrrrrrrrrrrrrrrr22222222222222222Inside reports Design");
            ReportDesign reportDesign = rs.getReportDesignByUuid("155562ad-0988-4ae8-a6ae-cee2039a807b");
            System.out.println("rrrrrrrrrrrrrrrrr3333333333333333333Inside reports Design");
            objects.add(WebConverter.convertMuzimaReport(reportDesign));
            response.put("objects", objects);*/
            List<Object> objects = new ArrayList<Object>();
            for (ReportDesign reportDesign : reportDesigns) {
                objects.add(WebConverter.convertMuzimaReport(reportDesign));
            }
            response.put("objects", objects);
        }
        
        return response;
    }

}
