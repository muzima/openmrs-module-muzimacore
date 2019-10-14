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

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 */
public class GeneratePatientReportsTask extends AbstractTask {

    private GeneratePatientReportsProcessor processor;

    public GeneratePatientReportsTask() {
        MuzimaSettingService muzimaSettingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting muzimaSetting = muzimaSettingService.getMuzimaSettingByProperty("PatientReport.isEnabled");
        Module module = ModuleFactory.getModuleById("reporting");
        if(module != null && module.isStarted() && muzimaSetting.getValueBoolean()) {
            this.processor = new GeneratePatientReportsProcessor();
        }
    }

    /**
     * @see org.openmrs.scheduler.Task#execute()
     */
    @Override
    public void execute() {
        if(processor != null) {
            Context.openSession();
            processor.generateReports();
            Context.closeSession();
        }
    }
}
