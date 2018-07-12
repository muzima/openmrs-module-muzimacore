/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaGeneratedReportService;
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.MuzimaGeneratedReport;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class MuzimaReportProcessor {
    
    private static Boolean isRunning = false;
    
    private final Log log = LogFactory.getLog(MuzimaReportProcessor.class);
    
    public void generateReports() {
        if (!isRunning) {
            processAllReports();
        } else {
            log.info("Queue data processor aborting (another processor already running)!");
        }
    }
    
    public void processAllReports() {
        System.out.println("fffffffffffffffffffffaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        
        List<ReportConfiguration> reportConfigurations = Context.getService(ReportConfigurationService.class)
                .getAllReportConfigurations();
        ReportService reportService = Context.getService(ReportService.class);
        ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
        System.out.println("fffffffffffffffffffffI am befbore line");
        try {
            MuzimaGeneratedReportService muzimaGeneratedReportService = Context.getService(MuzimaGeneratedReportService.class);
    
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            MuzimaGeneratedReportService muzimaGeneratedReportService = null;
        }
        System.out.println("fffffffffffffffffffffI am after line");
        MuzimaGeneratedReportService muzimaGeneratedReportService = Context.getService(MuzimaGeneratedReportService.class);
    
        for (ReportConfiguration reportConfiguration : reportConfigurations) {
            System.out.println("fffffffffffffffffbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" + reportConfiguration.getReportDesignUuid());
            
            Cohort cohort = Context.getCohortService().getCohortByUuid(reportConfiguration.getCohortUuid());
            System.out.println("fffffffffffffffff1111111111111111111");
            String patientIds = cohort.getCommaSeparatedPatientIds();
            String[] patientList = "1,2".split(",");
            
            RenderingMode selectedRenderingMode = null;
            
            ReportDesign design = reportService.getReportDesignByUuid(reportConfiguration.getReportDesignUuid());
            ReportDefinition reportDefinition = rds.getDefinitionByUuid(design.getReportDefinition().getUuid());
            System.out.println("fffffffffffffffff2222222222222222222222");
            for (RenderingMode renderingMode : reportService.getRenderingModes(reportDefinition)) {
                if (renderingMode.getLabel().equals(design.getName())) {
                    selectedRenderingMode = renderingMode;
                    System.out.println("vvvvvvvvvvvvvvvvvvvvvvfffffffffffffffff2222222222222222222222");
                }
            }
            System.out.println("ttttttttttttttttttttttttt1");
           
            System.out.println("ttttttttttttttttttttttttt2");
            //  for (String patientId : patientList) {
            System.out.println("ffffffffffffffffffccccccccccccccccccccccccccccccc");
            
            List<MuzimaGeneratedReport> generatedReportList = muzimaGeneratedReportService.getMuzimaGeneratedReportByPatientId(1);
            System.out.println("tttttttttttffffffffccccccccccccccccccccccccccccccc" + generatedReportList.size());
            if (generatedReportList.size() != 0) {
                System.out.println("eeeeeeeeeeeffffffffdddddddddddddddddddddddddddddddddddd");
                System.out.println("fffffff33333333333333333 Reports remain" + generatedReportList.size());
                MuzimaGeneratedReport generatedReport = generatedReportList.get(generatedReportList.size() - 1);
                if (!"completed".equals(generatedReport.getStatus())) {
                    System.out.println("fffffffffeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                    ReportRequest reportRequest = reportService
                            .getReportRequestByUuid(generatedReport.getReportRequestUuid());
                    
                    byte[] data = reportService.loadRenderedOutput(reportRequest);
                    
                    String filename = selectedRenderingMode.getRenderer().getFilename(reportRequest).replace(" ", "_");
                    System.out.println("rrrrrrrrrrrrrrrrrrrrrrrr" + filename);
                    System.out.println(
                            "23333333333333" + selectedRenderingMode.getRenderer().getRenderedContentType(reportRequest));
                    
                    if (data != null) {
                        System.out.println("22 22 22 22 22 22 22 22 22 2 22 222" + data);
                        System.out.println("22 22 22 22 22 22 22 22 22 2 22 222" + data.toString());
                    } else {
                        System.out.println("ffffffffffffffffffffffffffffffffffffffffffffailed");
                    }
                }
            } else {
                System.out.println("fffff00000000000000000000000000000" + 1);
                
                ReportRequest reportRequest = new ReportRequest();
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                
                params.put("person", Context.getService(PersonService.class).getPerson(1));
                reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                reportRequest.setRenderingMode(selectedRenderingMode);
                reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                
                reportRequest = reportService.queueReport(reportRequest);
                reportService.processNextQueuedReports();
                System.out.println("fffff11111111111111111111End of AsynchronousTask");
                
                MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                muzimaGeneratedReport.setCohortReportConfigUuid(reportConfiguration.getCohortUuid());
                muzimaGeneratedReport.setPatientId(1);
                muzimaGeneratedReport.setStatus("progress");
                
                muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                System.out.println("fffffff2222222222222222222222222Save of generatedReport Success");
            }
            
            //}
            //break;
        }
    }
}
