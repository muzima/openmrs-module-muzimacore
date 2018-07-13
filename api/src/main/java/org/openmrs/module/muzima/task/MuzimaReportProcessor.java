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
            
            String patientIds = cohort.getCommaSeparatedPatientIds();
            System.out.println("fffffffffffffffff22222222222222222222" + patientIds);
            String[] patientList = patientIds.split(",");
            
            RenderingMode selectedRenderingMode = null;
            
            ReportDesign design = reportService.getReportDesignByUuid(reportConfiguration.getReportDesignUuid());
            ReportDefinition reportDefinition = reportDefinitionService
                    .getDefinitionByUuid(design.getReportDefinition().getUuid());
            for (RenderingMode renderingMode : reportService.getRenderingModes(reportDefinition)) {
                if (renderingMode.getLabel().equals(design.getName())) {
                    selectedRenderingMode = renderingMode;
                    System.out.println("vvvvvvvvvvvvvvvvvvvvvvfffffffffffffffff2222222222222222222222");
                }
            }
            
            for (String patientId : patientList) {
                System.out.println("ffffffffffffffffff333333333333333333333" + patientId);
                
                MuzimaGeneratedReport generatedReport = muzimaGeneratedReportService
                        .getLastMuzimaGeneratedReportByPatientId(Integer.valueOf(patientId));
                System.out.println("tttttttttttffffffffccccccccccccccccccccccccccccccc" + generatedReport);
                if (generatedReport != null) {
                    System.out.println("fffffffffffffffff4444444444444444444444444444444444");
                    if (!"completed".equals(generatedReport.getStatus())) {
                        System.out.println("fffffffffffffffff5555555555555555555555555555");
                        ReportRequest reportRequest = reportService
                                .getReportRequestByUuid(generatedReport.getReportRequestUuid());
                        
                        byte[] data = reportService.loadRenderedOutput(reportRequest);
                        
                        if (data != null) {
                            System.out.println("fffffffffffffffff6666666666666666666666666666" + data);
                            System.out.println("Text [Byte Format] : " + data);
                            System.out.println("Text [Byte Format] : " + design.toString());
                            
                            String s = new String(data);
                            System.out.println("Text Decryted : " + s);
                            
                            generatedReport.setReportJson(s);
                            generatedReport.setStatus("completed");
                            muzimaGeneratedReportService.saveMuzimaGeneratedReport(generatedReport);
                            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                        } else {
                            System.out.println("ffffffffffffffffffffffffffffffffffffffffffffailed");
                        }
                    }
                    
                    System.out.println("ffffffffffffffffffff77777777777777777777777" + 1);
                    
                    Patient patient = patientService.getPatient(Integer.valueOf(patientId));
                    List<Obs> obsList = obsService.getObservationsByPerson(patient);
                    
                    Obs obs = obsList.get(obsList.size() - 1);
                    
                    if (obs.getObsDatetime().after(generatedReport.getDateCreated())) {
                        System.out.println("fffffffffffffffA new report has to be generated");
                        
                        ReportRequest reportRequest = new ReportRequest();
                        Map<String, Object> params = new LinkedHashMap<String, Object>();
                        
                        params.put("person", personService.getPerson(1));
                        reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                        reportRequest.setRenderingMode(selectedRenderingMode);
                        reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                        
                        reportRequest = reportService.queueReport(reportRequest);
                        reportService.processNextQueuedReports();
                        System.out.println("fffff11111111111111111111End of AsynchronousTask");
                        
                        MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                        muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                        muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                        muzimaGeneratedReport.setPatientId(1);
                        muzimaGeneratedReport.setStatus("progress");
                        
                        muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                        System.out.println("fffffff2222222222222222222222222Save of generatedReport Success");
                    }
                    
                } else {
                    
                    System.out.println("fffff00000000000000000000000000000" + 1);
                    
                    System.out.println("A new report has to be generated");
                    
                    ReportRequest reportRequest = new ReportRequest();
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    
                    params.put("person", personService.getPerson(1));
                    reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                    reportRequest.setRenderingMode(selectedRenderingMode);
                    reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                    
                    reportRequest = reportService.queueReport(reportRequest);
                    reportService.processNextQueuedReports();
                    System.out.println("fffff11111111111111111111End of AsynchronousTask");
                    
                    MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                    muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                    muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                    muzimaGeneratedReport.setPatientId(1);
                    muzimaGeneratedReport.setStatus("progress");
                    
                    muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                    System.out.println("fffffff2222222222222222222222222Save of generatedReport Success");
                    
                }
                break;
            }
            break;
        }
    }
}
