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
package org.openmrs.module.muzima.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.model.GeneratedReport;

import java.util.List;

public interface GeneratedReportService extends OpenmrsService {

    GeneratedReport getGeneratedReportById(final Integer id);
    
    List<GeneratedReport> getGeneratedReportByPatientId(final Integer id);
    
    GeneratedReport getGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId, Integer cohortReportConfigId);
    
    GeneratedReport getGeneratedReportByUuid(final String uuid);
    
    GeneratedReport saveGeneratedReport(GeneratedReport generatedReport);

    void deleteGeneratedReport(GeneratedReport generatedReport);

    List<GeneratedReport> getAllGeneratedReports();

    Number countGeneratedReports();

    /**
     * Get the total number of the settings in the database with partial matching search term.
     *
     *
     * @param search the search term.
     * @return the total number of the settings in the database.
     */
    Number countDataSource(final Integer patientId);

    /**
     * Get settings with matching search term for a particular page.
     *
     * @param patientId     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all settings with matching search term for a particular page.
     */
    List<GeneratedReport> getPagedGeneratedReports(final Integer patientId, final Integer pageNumber,
            final Integer pageSize);

}
