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

/**
 */
public class ReportProcessor {

    private final Log log = LogFactory.getLog(ReportProcessor.class);

    private static Boolean isRunning = false;

    public void generateReports() {
        if (!isRunning) {
            processAllReports();
        } else {
            log.info("Queue data processor aborting (another processor already running)!");
        }
    }

    private void processAllReports() {
        
    }
}
