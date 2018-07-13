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
import org.openmrs.module.muzima.model.MuzimaGeneratedReport;

import java.util.List;

public interface MuzimaGeneratedReportService extends OpenmrsService {

    MuzimaGeneratedReport getMuzimaGeneratedReportById(final Integer id);
    
    List<MuzimaGeneratedReport> getMuzimaGeneratedReportByPatientId(final Integer id);
    
    MuzimaGeneratedReport getLastMuzimaGeneratedReportByPatientId(final Integer id);
    
    MuzimaGeneratedReport getMuzimaGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId, Integer cohortReportConfigId);
    
    MuzimaGeneratedReport getMuzimaGeneratedReportByUuid(final String uuid);
    
    MuzimaGeneratedReport saveMuzimaGeneratedReport(MuzimaGeneratedReport muzimageneratedReport);

    void deleteMuzimaGeneratedReport(MuzimaGeneratedReport muzimageneratedReport);

    List<MuzimaGeneratedReport> getAllMuzimaGeneratedReports();

    Number countMuzimaGeneratedReports();

    /**
     * Get the total number of the settings in the database with partial matching search term.
     *
     *
     * @param patientId the search term.
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
    List<MuzimaGeneratedReport> getPagedMuzimaGeneratedReports(final Integer patientId, final Integer pageNumber,
            final Integer pageSize);

}
