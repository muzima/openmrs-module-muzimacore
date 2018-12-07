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
 * This class generates muzimaGeneratedReports for each cohort according to scheduled parameters
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
        
        List<ReportConfiguration> reportConfigurations = Context.getService(ReportConfigurationService.class)
                .getAllReportConfigurations();
        ReportService reportService = Context.getService(ReportService.class);
        ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
        ObsService obsService = Context.getService(ObsService.class);
        PersonService personService = Context.getService(PersonService.class);
        PatientService patientService = Context.getService(PatientService.class);
        MuzimaGeneratedReportService muzimaGeneratedReportService = Context.getService(MuzimaGeneratedReportService.class);
    
        //This loop iterates through every report configuration created
        for (ReportConfiguration reportConfiguration : reportConfigurations) {
       
            Cohort cohort = Context.getCohortService().getCohortByUuid(reportConfiguration.getCohortUuid());
            String patientIds = cohort.getCommaSeparatedPatientIds();
            String[] patientList = patientIds.split(",");
            
            RenderingMode selectedRenderingMode = null;
            
            ReportDesign design = reportService.getReportDesignByUuid(reportConfiguration.getReportDesignUuid());
            ReportDefinition reportDefinition = reportDefinitionService
                    .getDefinitionByUuid(design.getReportDefinition().getUuid());
            
            //sets the selected rendering mode
            for (RenderingMode renderingMode : reportService.getRenderingModes(reportDefinition)) {
                if (renderingMode.getLabel().equals(design.getName())) {
                    selectedRenderingMode = renderingMode;
                }
            }
            //iterates through the list of patients in the cohort of the reportConfiguration
            for (String patientIdString : patientList) {
                Integer patientId = Integer.valueOf(patientIdString);
               
                MuzimaGeneratedReport lastGeneratedReport = muzimaGeneratedReportService
                        .getLastMuzimaGeneratedReportByPatientIdANDCohortReportConfigId(patientId,reportConfiguration.getId());
    
                //gets the last generated report to check whether the it is complete
                if (lastGeneratedReport != null) {
                    if (!"completed".equals(lastGeneratedReport.getStatus())) {
                        ReportRequest reportRequest = reportService
                                .getReportRequestByUuid(lastGeneratedReport.getReportRequestUuid());
    
                        //checks whether the request to generate reports is completed
                        if ("COMPLETED".equals(reportRequest.getStatus().toString())) {
                            //if complete, report is generated
                            byte[] data = reportService.loadRenderedOutput(reportRequest);
                            
                            if (data != null) {
                                String s = new String(data);
                                lastGeneratedReport.setReportJson(data);
                                lastGeneratedReport.setStatus("completed");
                                muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                            } else {
                                lastGeneratedReport.setStatus("completed");
                                muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                            }
                            
                        } else {
                            lastGeneratedReport.setStatus("progress");
                            muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                        }
                    }
                    
                    Patient patient = patientService.getPatient(patientId);
                    List<Obs> obsList = obsService.getObservationsByPerson(patient);
                    if (0 != obsList.size()) {
                        Obs obs = obsList.get(obsList.size() - 1); //gets the last observation of the patient
                        final Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        Date yesterday = cal.getTime();
    
                        //checks whether the last obs datetime against the last time the report was generated
                        if (obs.getObsDatetime().after(yesterday)) {
                            ReportRequest reportRequest = new ReportRequest();
                            Map<String, Object> params = new LinkedHashMap<String, Object>();
                            
                            params.put("person", personService.getPerson(patientId));
                            reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                            reportRequest.setRenderingMode(selectedRenderingMode);
                            reportRequest.setPriority(ReportRequest.Priority.NORMAL);
                            
                            //Request for report generation is made(handled asynchronously)
                            reportRequest = reportService.queueReport(reportRequest);
                            reportService.processNextQueuedReports();
                           
                            MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                            muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                            muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                            muzimaGeneratedReport.setPatientId(patientId);
                            muzimaGeneratedReport.setPriority(reportConfiguration.getPriority());
                            muzimaGeneratedReport.setStatus("progress");
                            
                            muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                        }
                        
                    }
                    
                } else {
                    
                   //this snippet is run for the very first time of the task
                    ReportRequest reportRequest = new ReportRequest();
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    
                    params.put("person", personService.getPerson(patientId));
                    reportRequest.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                    reportRequest.setRenderingMode(selectedRenderingMode);
                    reportRequest.setPriority(ReportRequest.Priority.NORMAL);
    
                    //Request for report generation is made(handled asynchronously)
                    reportRequest = reportService.queueReport(reportRequest);
                    reportService.processNextQueuedReports();
                   
                    MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                    muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                    muzimaGeneratedReport.setCohortReportConfigId(reportConfiguration.getId());
                    muzimaGeneratedReport.setPatientId(patientId);
                    muzimaGeneratedReport.setPriority(reportConfiguration.getPriority());
                    muzimaGeneratedReport.setStatus("progress");
                    
                    muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                }
            }
        }
    }
}
