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
package org.openmrs.module.muzima.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.cohort.CohortDefinition;
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.report.EvaluationContext;
import org.openmrs.module.reporting.definition.DefinitionSummary;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class MuzimaReportProcessor {
    
    private final Log log = LogFactory.getLog(MuzimaReportProcessor.class);
    
    private static Boolean isRunning = false;
    
    public void generateReports() {
        if (!isRunning) {
            processAllReports();
        } else {
            log.info("Queue data processor aborting (another processor already running)!");
        }
    }
    
    public void processAllReports() {
        
        List<ReportConfiguration> reportConfigurations = Context.getService(ReportConfigurationService.class).getAllReportConfigurations();
        ReportService rs = Context.getService(ReportService.class);
        ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
        for (ReportConfiguration reportConfiguration : reportConfigurations) {
            Cohort cohort = Context.getCohortService().getCohortByUuid(reportConfiguration.getCohortUuid());
            String patientIds = cohort.getCommaSeparatedPatientIds();
            String[] patientList = patientIds.split(",");
            RenderingMode selectedRenderingMode = null;
            
            
            ReportDesign design = rs.getReportDesignByUuid(reportConfiguration.getReportUuid());
    
            
            ReportDefinition reportDefinition = rds.getDefinitionByUuid(design.getReportDefinition().getUuid());
            
            System.out.println("12 12 12 12 12 12 12 InReport"+reportDefinition.toString());
           
            for (Parameter p : reportDefinition.getParameters()) {
                System.out.println("13 13 13 13 13 13 13InReport"+p.getName()+" "+p.getLabel());
                System.out.println("1414144141414141441414InReport"+rs.getRenderingModes(reportDefinition));
                }
            for (RenderingMode renderingMode : rs.getRenderingModes(reportDefinition)) {
                System.out.println("16161616161616161616166116"+renderingMode.getLabel()+"kkkkk"+design.getName());
                if(renderingMode.getLabel().equals(design.getName())){
                    selectedRenderingMode = renderingMode;
                }
                
            }
            
            for (String patientId : patientList) {
                System.out.println("171717717717717171717717"+patientId);
                
                design.getRendererType();
                ReportRequest   rr = new ReportRequest();
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                params.put("person",Context.getService(PersonService.class).getPerson(1));
                rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, params));
                rr.setRenderingMode(selectedRenderingMode);
                rr.setPriority(ReportRequest.Priority.NORMAL);
                rr = rs.queueReport(rr);
                rs.processNextQueuedReports();
                ReportDesignResource r = design.getResourceByUuid(design.getUuid());
                 File file1 =rs.getReportDataFile(rr);
                 
                System.out.println("1818181818181818"+file1.toString());
                
                File file2 =rs.getReportOutputFile(rr);
                
                System.out.println("199999999999999"+file1.toString());
                
                for (ReportProcessorConfiguration c : ReportUtil
                        .getAvailableReportProcessorConfigurations(rr, ReportProcessorConfiguration.ProcessorMode.values())) {
                    ReportProcessorConfiguration.ProcessorMode m = c.getProcessorMode();
                }
                System.out.println("2020202020202020202020"+file1.toString());
                ReportData reportData = rs.loadReportData(rr);
                System.out.println("212121212121212121212121"+reportData);
                byte[] data = rs.loadRenderedOutput(rr);
                System.out.println("22 22 22 22 22 22 22 22 22 2 22 222"+data);
                System.out.println("22 22 22 22 22 22 22 22 22 2 22 222"+data.toString());
            }
        }
    }
}
