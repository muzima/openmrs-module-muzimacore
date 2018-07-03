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
package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.GeneratedReport;

import java.util.List;

public interface GeneratedReportDao {

    List<GeneratedReport> getAll();

    List<GeneratedReport> getPagedGeneratedReports(final Integer patientId, final Integer pageNumber,
            final Integer pageSize);

    /**
     * Get the total number of data source with matching search term.
     *
     *
     * @param patientId the search term.
     * @return total number of reports in the database.
     */
    Number countGeneratedReports(final Integer patientId);

    Number countGeneratedReports();
    
     GeneratedReport getGeneratedReportById(Integer id);
    
    List<GeneratedReport> getGeneratedReportByPatientId(Integer id);

    GeneratedReport getGeneratedReportByUuid(String Uuid);
    
    GeneratedReport getGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId, Integer cohortReportConfigId);
    
    List<GeneratedReport> getGeneratedReportByCohortReportConfigId(Integer cohortReportConfigId);

    GeneratedReport saveOrUpdateGeneratedReport(GeneratedReport reportConfiguration);

    void deleteGeneratedReport(GeneratedReport reportConfiguration);
}
