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
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.CohortDefinitionDataService;
import org.openmrs.module.muzima.api.ExpandedCohortProcessorService;
import org.openmrs.module.muzima.api.impl.ExpandedCohortProcessorServiceImpl;
import org.openmrs.module.muzima.api.model.CohortDefinitionData;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ExpandedCohortProcessor {
    private final Log log = LogFactory.getLog(ExpandedCohortProcessor.class);

    private static Boolean isRunning = false;
    public void processExpandedCohorts() {
        if (!isRunning) {
            log.info("Starting up Expanded cohort processor ...");
            process();
        } else {
            log.info("Expanded cohort processor aborting (another processor already running)!");
        }
    }

    private void process(){
        try {
            isRunning = true;
            CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
            ExpandedCohortProcessorService expandedCohortProcessorService = new ExpandedCohortProcessorServiceImpl();
            List<CohortDefinitionData> cohortDefinitionDataList = cohortDefinitionDataService.getAllScheduledCohortDefinitionData();

            Iterator<CohortDefinitionData> cohortCriteriaDataIterator = cohortDefinitionDataList.iterator();
            while(cohortCriteriaDataIterator.hasNext()){
                CohortDefinitionData cohortCriteriaData=cohortCriteriaDataIterator.next();
                System.out.println("Processing cohort:"+cohortCriteriaData.getCohortId());
                expandedCohortProcessorService.process(cohortCriteriaData);
            }
        } finally {
            isRunning = false;
        }
    }
}
