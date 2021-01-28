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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CohortDefinitionDataService;
import org.openmrs.module.muzima.api.service.ExpandedCohortProcessorService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.module.muzima.utils.ISO8601Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExpandedCohortProcessor {
    private final Log log = LogFactory.getLog(ExpandedCohortProcessor.class);
    private static Boolean isRunning = false;
    public void processExpandedCohorts() {
        if (!isRunning) {
            log.info("Starting up Expanded cohort processor ...");
            CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);

            List<CohortDefinitionData> cohortDefinitionDataList = cohortDefinitionDataService.getAllScheduledCohortDefinitionData();

            String lastExecutionTime = cohortDefinitionDataService.getLastExecutionTime();
            Date date = null;
            if(lastExecutionTime != null) {
                date = parseDate(lastExecutionTime);
            }
            Date today = new Date();
            long diff = 0;
            if(date != null) {
                 diff = today.getTime() - date.getTime();
            }
            double hours = diff/(60 * 60 * 1000);
            if(date == null || hours>12) {
                process();
            }
        } else {
            log.info("Expanded cohort processor aborting (another processor already running)!");
        }
    }

    private Date parseDate(final String dateValue) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(StringUtils.isNumeric(dateValue))
        {
            long timestamp = Long.parseLong(dateValue);
            date = new Date(timestamp);
        }else {
            try {
                date = dateFormat.parse(dateValue);
            } catch (ParseException e) {
                log.error("Unable to parse date data for encounter!", e);
            }
        }
        return date;
    }

    public void processExpandedCohort(final String uuid) {
        if (!isRunning) {
            log.info("Starting up Expanded cohort processor ...");
            process(uuid);
        } else {
            log.info("Expanded cohort processor aborting (another processor already running)!");
        }
    }

    private void process(){
        try {
            isRunning = true;
            CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);

            List<CohortDefinitionData> cohortDefinitionDataList = cohortDefinitionDataService.getAllScheduledCohortDefinitionData();

            ExpandedCohortProcessorService expandedCohortProcessorService = Context.getRegisteredComponent("muzima.ExpandedCohortProcessorService",ExpandedCohortProcessorService.class);
            Iterator<CohortDefinitionData> cohortCriteriaDataIterator = cohortDefinitionDataList.iterator();
            while(cohortCriteriaDataIterator.hasNext()){
                CohortDefinitionData cohortCriteriaData=cohortCriteriaDataIterator.next();
                expandedCohortProcessorService.process(cohortCriteriaData);
            }
        } finally {
            isRunning = false;
        }
    }

    private void process(final String uuid){
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByUuid(uuid);
        ExpandedCohortProcessorService expandedCohortProcessorService = Context.getRegisteredComponent("muzima.ExpandedCohortProcessorService",ExpandedCohortProcessorService.class);
        if(cohortDefinitionData !=null ){
            expandedCohortProcessorService.process(cohortDefinitionData);
        }
    }
}
