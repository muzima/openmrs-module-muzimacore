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
import org.openmrs.module.muzima.model.MuzimaPatientReport;

import java.util.List;

public interface MuzimaPatientReportService extends OpenmrsService {

    MuzimaPatientReport getMuzimaPatientReportById(final Integer id);
    
    List<MuzimaPatientReport> getMuzimaPatientReportByPatientId(final Integer patientId);
    
    MuzimaPatientReport getLatestPatientReportByPatientId(final Integer patientId);
    
    MuzimaPatientReport getLatestPatientReportByPatientIdAndConfigId(Integer patientId, Integer configId);
    
    MuzimaPatientReport getMuzimaPatientReportByUuid(final String uuid);
    
    MuzimaPatientReport saveMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport);

    void deleteMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport);

    List<MuzimaPatientReport> getAllMuzimaPatientReports();

    Number countMuzimaPatientReports();

    /**
     * Get the total number of the settings in the database with partial matching search term.
     *
     *
     * @param patientId the search term.
     * @return the total number of the settings in the database.
     */
    Number countMuzimaPatientReports(final Integer patientId);

    /**
     * Get settings with matching search term for a particular page.
     *
     * @param patientId     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all settings with matching search term for a particular page.
     */
    List<MuzimaPatientReport> getPagedMuzimaPatientReports(final Integer patientId, final Integer pageNumber, final Integer pageSize);

}
