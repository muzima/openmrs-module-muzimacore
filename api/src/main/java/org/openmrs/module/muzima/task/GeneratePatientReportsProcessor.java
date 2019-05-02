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

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
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

import java.util.*;

/**
 */
public class GeneratePatientReportsProcessor {
    
    private static Boolean isRunning = false;
    
    private final Log log = LogFactory.getLog(GeneratePatientReportsProcessor.class);
    
    public void generateReports() {
        if (!isRunning) {
            log.info("Starting up Generate Report processor ...");
            processAllReports();
        } else {
            log.info("Muzima Generate Report processor aborting (another processor already running)!");
        }
    }
    
    private void processAllReports() {
        try {
            isRunning = true;
        
            List<ReportConfiguration> reportConfigurations = Context.getService(ReportConfigurationService.class).getAllReportConfigurations();
            ReportService reportService = Context.getService(ReportService.class);
            ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);
            ObsService obsService = Context.getService(ObsService.class);
            PersonService personService = Context.getService(PersonService.class);
            PatientService patientService = Context.getService(PatientService.class);
            MuzimaGeneratedReportService muzimaGeneratedReportService = Context.getService(MuzimaGeneratedReportService.class);
            RenderingMode selectedRenderingMode = null;
            List<ReportRequest> reportsToQueue = new ArrayList<ReportRequest>();

            for (ReportConfiguration configuration : reportConfigurations ) {
                Cohort cohort = Context.getCohortService().getCohortByUuid(configuration.getCohortUuid());
                String patientIds = cohort.getCommaSeparatedPatientIds();
                String[] patientList = patientIds.split(",");

                JSONArray reports = JsonPath.read(configuration.getReportDesigns(),"reports");
                for (Object o : reports) {
                    String reportDesignUuid= JsonPath.read(o.toString(),"uuid");
                    ReportDesign design = reportService.getReportDesignByUuid(reportDesignUuid);

                    if (design == null)
                        continue;

                    for (RenderingMode renderingMode : reportService.getRenderingModes(design.getReportDefinition())) {
                        if (renderingMode.getLabel().equals(design.getName())) {
                            selectedRenderingMode = renderingMode;
                            break;
                        }
                    }

                    for (String patientIdString : patientList) {
                        Integer patientId = Integer.valueOf(patientIdString);

                        MuzimaGeneratedReport lastGeneratedReport = muzimaGeneratedReportService
                                .getLastMuzimaGeneratedReportByPatientIdAndCohortReportConfigId(patientId,configuration.getId());

                        if (lastGeneratedReport != null) {
                            if (!"completed".equals(lastGeneratedReport.getStatus())) {
                                ReportRequest reportRequest = reportService.getReportRequestByUuid(lastGeneratedReport.getReportRequestUuid());

                                if ("COMPLETED".equals(reportRequest.getStatus().toString())) {
                                    byte[] data = reportService.loadRenderedOutput(reportRequest);
                                    if (data != null) {
                                        lastGeneratedReport.setReportJson(data);
                                        lastGeneratedReport.setStatus("completed");
                                        muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                                    } else {
                                        lastGeneratedReport.setStatus("failed");
                                        muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                                    }
                                } else if ("FAILED".equals(reportRequest.getStatus().toString())) {
                                    lastGeneratedReport.setStatus("failed");
                                    muzimaGeneratedReportService.saveMuzimaGeneratedReport(lastGeneratedReport);
                                }
                            }

                            Patient patient = patientService.getPatient(patientId);
                            List<Obs> obsList = obsService.getObservationsByPerson(patient);
                            if (0 != obsList.size()) {
                                Obs obs = obsList.get(obsList.size() - 1);
                                final Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.DATE, -1);
                                Date yesterday = cal.getTime();
                                if (obs.getObsDatetime().after(yesterday)) {
                                    ReportRequest reportRequest = new ReportRequest();
                                    Map<String, Object> params = new LinkedHashMap<String, Object>();

                                    params.put("patient", personService.getPerson(patientId));
                                    reportRequest.setReportDefinition(new Mapped<ReportDefinition>(design.getReportDefinition(), params));
                                    reportRequest.setRenderingMode(selectedRenderingMode);
                                    reportRequest.setPriority(ReportRequest.Priority.LOW);
                                    reportsToQueue.add(reportRequest);

                                    MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                                    muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                                    muzimaGeneratedReport.setCohortReportConfigId(configuration.getId());
                                    muzimaGeneratedReport.setPatientId(patientId);
                                    muzimaGeneratedReport.setPriority(configuration.getPriority());
                                    muzimaGeneratedReport.setStatus("progress");

                                    muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                                }
                            }
                        } else {
                            try {
                                ReportRequest reportRequest = new ReportRequest();
                                Map<String, Object> params = new LinkedHashMap<String, Object>();

                                params.put("patient", personService.getPerson(patientId));
                                reportRequest.setReportDefinition(new Mapped<ReportDefinition>(design.getReportDefinition(), params));
                                reportRequest.setRenderingMode(selectedRenderingMode);
                                reportRequest.setPriority(ReportRequest.Priority.LOW);
                                reportRequest = reportService.queueReport(reportRequest);

                                MuzimaGeneratedReport muzimaGeneratedReport = new MuzimaGeneratedReport();
                                muzimaGeneratedReport.setReportRequestUuid(reportRequest.getUuid());
                                muzimaGeneratedReport.setCohortReportConfigId(configuration.getId());
                                muzimaGeneratedReport.setPatientId(patientId);
                                muzimaGeneratedReport.setPriority(configuration.getPriority());
                                muzimaGeneratedReport.setStatus("progress");
                                muzimaGeneratedReportService.saveMuzimaGeneratedReport(muzimaGeneratedReport);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            for (ReportRequest reportRequest : reportsToQueue) {
                reportService.queueReport(reportRequest);
            }
            reportService.processNextQueuedReports();
        } finally {
            isRunning = false;
        }
    }
}
