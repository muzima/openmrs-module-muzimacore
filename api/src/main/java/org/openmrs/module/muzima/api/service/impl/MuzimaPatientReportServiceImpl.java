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
package org.openmrs.module.muzima.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaPatientReportDao;
import org.openmrs.module.muzima.api.service.MuzimaPatientReportService;
import org.openmrs.module.muzima.model.MuzimaPatientReport;

import java.util.List;

public class MuzimaPatientReportServiceImpl extends BaseOpenmrsService implements MuzimaPatientReportService {
    private final Log log = LogFactory.getLog(this.getClass());
    private MuzimaPatientReportDao dao;

    public MuzimaPatientReportServiceImpl(MuzimaPatientReportDao dao){
        this.dao = dao;
    }

    public MuzimaPatientReportDao getDao() {
        return dao;
    }

    public void setDao(MuzimaPatientReportDao dao) {
        this.dao = dao;
    }

    @Override
    public List<MuzimaPatientReport> getMuzimaPatientReportsByPatientId(final  Integer patientId){
        return dao.getMuzimaPatientReportsByPatientId(patientId);
    }
    
    @Override
    public MuzimaPatientReport getLatestPatientReportByPatientId(Integer patientId) {
        return dao.getLatestPatientReportByPatientId(patientId);
    }
    
    @Override
    public MuzimaPatientReport getLatestPatientReportByPatientIdAndConfigId(Integer patientId, Integer configId) {
        return dao.getLatestPatientReportByPatientIdAndConfigId(patientId, configId);
    }
    
    @Override
    public MuzimaPatientReport getMuzimaPatientReportById(Integer id) {
        return dao.getMuzimaPatientReportById(id);
    }

    @Override
    public MuzimaPatientReport getMuzimaPatientReportByName(String reportName) {
        return dao.getMuzimaPatientReportByName(reportName);
    }

    @Override
    public MuzimaPatientReport getMuzimaPatientReportByUuid(final String uuid){
        return dao.getMuzimaPatientReportByUuid(uuid);
    }

    @Override
    public MuzimaPatientReport saveMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport){
        return dao.saveOrUpdateMuzimaPatientReport(muzimaPatientReport);
    }

    @Override
    public void deleteMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport){
        dao.deleteMuzimaPatientReport(muzimaPatientReport);
    }

    @Override
    public List<MuzimaPatientReport> getAllMuzimaPatientReports(){
        return dao.getAllMuzimaPatientReports();
    }

    @Override
    public Number countMuzimaPatientReports(){
        return dao.countMuzimaPatientReports();
    }

    /**
     * Get the total number of the patient reports in the database for a specific patient
     *
     *
     * @param patientId the patient id.
     * @return the total number of the patient reports in the database.
     */
    @Override
    public Number countMuzimaPatientReports(final Integer patientId) {
        return dao.countMuzimaPatientReports(patientId);
    }

    /**
     * Get patient reports for a particular page.
     *
     * @param patientId    the patient id.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all patient reports for this patient for a particular page.
     */
    @Override
    public List<MuzimaPatientReport> getPagedMuzimaPatientReports(final Integer patientId, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedMuzimaPatientReports(patientId, pageNumber, pageSize);
    }
}
