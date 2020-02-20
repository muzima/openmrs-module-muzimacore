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

import java.util.Date;
import java.util.List;

public interface MuzimaPatientReportService extends OpenmrsService {

    MuzimaPatientReport getMuzimaPatientReportByUuid(final String uuid);

    List<MuzimaPatientReport> getMuzimaPatientReportByUuids(final String reportUuids);

    MuzimaPatientReport getMuzimaPatientReportById(final Integer id);

    MuzimaPatientReport getMuzimaPatientReportByName(final Integer patientId, final String reportName);

    List<MuzimaPatientReport> getMuzimaPatientReportsByPatientId(final Integer patientId);

    MuzimaPatientReport getLatestPatientReportByPatientId(final Integer patientId);

    MuzimaPatientReport getLatestPatientReportByPatientIdAndConfigId(Integer patientId, Integer configId);

    MuzimaPatientReport saveMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport);

    void deleteMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport);

    List<MuzimaPatientReport> getAllMuzimaPatientReports();

    MuzimaPatientReport getMuzimaPatientReportByReportRequestUuid(final String uuid);

    Number countMuzimaPatientReports();

    /**
     * Get the total number of the patient reports in the database for a particular patient.
     *
     * @param patientId the specific patient.
     * @return the total number of the patient reports in the database for the patient.
     */
    Number countMuzimaPatientReports(final Integer patientId);

    /**
     * Get paged patient reports for a specific patient.
     *
     * @param patientId the specific patient.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all patient reports for the specific patient for a particular page.
     */
    List<MuzimaPatientReport> getPagedMuzimaPatientReports(final Integer patientId, final Integer pageNumber, final Integer pageSize);
    List<MuzimaPatientReport> getPagedMuzimaPatientReports(final String patientUuids, final Integer pageNumber, final Integer pageSize, final Date syncDate);


}
