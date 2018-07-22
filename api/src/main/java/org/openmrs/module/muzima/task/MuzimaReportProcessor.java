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
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
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

import java.util.Calendar;
import java.util.Date;
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
        ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
        ObsService obsService = Context.getService(ObsService.class);
        PersonService personService = Context.getService(PersonService.class);
        PatientService patientService = Context.getService(PatientService.class);
        MuzimaGeneratedReportService muzimaGeneratedReportService = Context.getService(MuzimaGeneratedReportService.class);
        
        for (ReportConfiguration reportConfiguration : reportConfigurations) {
            System.out
                    .println("fffffffffffffffff1111111111111111111111111111111" + reportConfiguration.getReportDesignUuid());
            
            Cohort cohort = Context.getCohortService().getCohortByUuid(reportConfiguration.getCohortUuid());
            System.out.println("fffffffffffffffff2222222222222222222" + cohort.getName());
            String patientIds = cohort.getCommaSeparatedPatientIds();
            System.out.println("fffffffffffffffff3333333333333333333333" + patientIds);
            String[] patientList = patientIds.split(",");
            
            RenderingMode selectedRenderingMode = null;
            
            ReportDesign design = reportService.getReportDesignByUuid(reportConfiguration.getReportDesignUuid());
            ReportDefinition reportDefinition = reportDefinitionService
                    .getDefinitionByUuid(design.getReportDefinition().getUuid());
            for (RenderingMode renderingMode : reportService.getRenderingModes(reportDefinition)) {
                if (renderingMode.getLabel().equals(design.getName())) {
                    selectedRenderingMode = renderingMode;
                    System.out.println("fffffffffffffffffffff444444444444444444");
                }
            }
            
            for (String patientIdString : patientList) {
                Integer patientId = Integer.valueOf(patientIdString);
                System.out.println("ffffffffffffffffff5555555555555555555" + patientId);
    
                System.out.println("ffffffffffffffffff555555555555555555cccccccccccc5" + patientId+" kkkk"+ muzimaGeneratedReportService
                        .getLastMuzimaGeneratedReportByPatientId(patientId));
                
                MuzimaGeneratedReport lastGeneratedReport = muzimaGeneratedReportService
                        .getLastMuzimaGeneratedReportByPatientId(patientId);
                System.out.println("ffffffffffffffffff66666666666666666666" + lastGeneratedReport);
                if (lastGeneratedReport != null) {
                    System.out.println("fffffffffffffffff777777777777777777777");
                    if (!"completed".equals(lastGeneratedReport.getStatus())) {
                        System.out.println("fffffffffffffffff888888888888888888888");
                        ReportRequest reportRequest = reportService
                                .getReportRequestByUuid(lastGeneratedReport.getReportRequestUuid());
                        if ("completed".equals(reportRequest.getStatus().toString())) {
                            System.out.println("ffffffffffffffffffff999999999999999" + reportRequest.getStatus().toString());
                            byte[] data = reportService.loadRenderedOutput(reportRequest);
                            
                            if (data != null) {
                                System.out.println("fffffffffffffffffaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + data);
                                System.out.println("Text [Byte Format] : " + data);
                                System.out.println("Text [Byte Format] : " + design.toString());
                                
                                String s = new String(data);
                                System.out.println("Text Decryted : " + s);
    
                                lastGeneratedReport.setReportJson(s);
                                lastGeneratedReport.setStatus("completed");
                                muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                                System.out.println("fffffffffffffffffffbbbbbbbbbbbbbbbbbbbbbbbb");
                            } else {
                                System.out.println("ffffffffffffffffffcccccccccccccccccc");
                                lastGeneratedReport.setStatus("completed");
                                muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                                System.out.println("fffffffffffffffffffdddddddddddddddddddddd");
                            }
                            
                        } else {
                            System.out.println("ffffffffffffffffffeeeeeeeeeeeeeeeeeee");
                            lastGeneratedReport.setStatus("completed");
                            muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                            System.out.println("fffffffffffffffffffdgggggggggggggggggggg");
                            
                        }
                    }
                    
                    System.out.println("ffffffffffffffffffffhhhhhhhhhhhhhhhhh" + 1);
                    
                    Patient patient = patientService.getPatient(patientId);
                    List<Obs> obsList = obsService.getObservationsByPerson(patient);
                    if (0 != obsList.size()) {
                        System.out.println("ffffffffffffffffffffiiiiiiiiiiiiiiiiiii"+obsList+" "+obsList.size()+" "+obsList.get(0));
                        Obs obs = obsList.get(obsList.size() - 1);
    
                        final Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        Date yesterday = cal.getTime();
                        
                        if (obs.getObsDatetime().after(yesterday)) {
                            System.out.println("fffffffffffffffjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                            
                            ReportRequest reportRequest = new ReportRequest();
                            Map<String, Object> params = new LinkedHashMap<String, Object>();
                            
                            params.put("person", personService.getPerson(patientId));
                            reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                            reportRequest.setRenderingMode(selectedRenderingMode);
                            reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                            
                            reportRequest = reportService.queueReport(reportRequest);
                            reportService.processNextQueuedReports();
                            System.out.println("fffff11111111111111111111End of AsynchronousTask");
                            
                            MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                            muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                            muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                            muzimaGeneratedReport.setPatientId(patientId);
                            muzimaGeneratedReport.setPriority(reportConfiguration.getPriority());
                            muzimaGeneratedReport.setStatus("progress");
                            
                            muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                            System.out.println("fffffffffffffffkkkkkkkkkkkkkkkkkkkkkkkkkSave of generatedReport Success");
                        }
                        
                    }
                    
                } else {
                    
                    System.out.println("fffffffffffffllllllllllllllllllllll" + 1);
                    
                    ReportRequest reportRequest = new ReportRequest();
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    
                    params.put("person", personService.getPerson(patientId));
                    reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                    reportRequest.setRenderingMode(selectedRenderingMode);
                    reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                    
                    reportRequest = reportService.queueReport(reportRequest);
                    reportService.processNextQueuedReports();
                    System.out.println("fffffffffffffffffffmmmmmmmmmmmmmmmmmmmmmm");
                    
                    MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                    muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                    muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                    muzimaGeneratedReport.setPatientId(patientId);
                    muzimaGeneratedReport.setPriority(reportConfiguration.getPriority());
                    muzimaGeneratedReport.setStatus("progress");
                    
                    muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                    System.out.println("ffffffffffffffooooooooooooooooooooooSave of generatedReport Success");
                    
                }
            }
        }
    }
}
