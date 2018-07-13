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
import org.openmrs.module.muzima.api.db.MuzimaGeneratedReportDao;
import org.openmrs.module.muzima.api.service.MuzimaGeneratedReportService;
import org.openmrs.module.muzima.model.MuzimaGeneratedReport;

import java.util.List;

public class MuzimaGeneratedReportServiceImpl extends BaseOpenmrsService implements MuzimaGeneratedReportService {
    private final Log log = LogFactory.getLog(this.getClass());
    private MuzimaGeneratedReportDao dao;

    public MuzimaGeneratedReportServiceImpl(MuzimaGeneratedReportDao dao){
        this.dao = dao;
    }

    public MuzimaGeneratedReportDao getDao() {
        return dao;
    }

    public void setDao(MuzimaGeneratedReportDao dao) {
        this.dao = dao;
    }

    @Override
    public List<MuzimaGeneratedReport> getMuzimaGeneratedReportByPatientId(final  Integer id){
        return dao.getMuzimaGeneratedReportByPatientId(id);
    }
    
    @Override
    public MuzimaGeneratedReport getLastMuzimaGeneratedReportByPatientId(Integer id) {
        return dao.getLastMuzimaGeneratedReportByPatientId(id);
    }
    
    @Override
    public MuzimaGeneratedReport getMuzimaGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId,
            Integer cohortReportConfigId) {
        return dao.getMuzimaGeneratedReportByPatientIdANDCohortReportConfigId(patientId,cohortReportConfigId);
    }
    
    @Override
    public MuzimaGeneratedReport getMuzimaGeneratedReportById(Integer id) {
        return dao.getMuzimaGeneratedReportById(id);
    }
    
    
    @Override
    public MuzimaGeneratedReport getMuzimaGeneratedReportByUuid(final String uuid){
        return dao.getMuzimaGeneratedReportByUuid(uuid);
    }

    

    @Override
    public MuzimaGeneratedReport saveMuzimaGeneratedReport(MuzimaGeneratedReport muzimaGeneratedReport){
        return dao.saveOrUpdateMuzimaGeneratedReport(muzimaGeneratedReport);
    }

    @Override
    public void deleteMuzimaGeneratedReport(MuzimaGeneratedReport muzimaGeneratedReport){
        dao.deleteMuzimaGeneratedReport(muzimaGeneratedReport);
    }

    @Override
    public List<MuzimaGeneratedReport> getAllMuzimaGeneratedReports(){
        return dao.getAll();
    }

    @Override
    public Number countMuzimaGeneratedReports(){
        return dao.countMuzimaGeneratedReports();
    }

    /**
     * Get the total number of the settings in the database with partial matching search term.
     *
     *
     * @param patientId the search term.
     * @return the total number of the settings in the database.
     */
    @Override
    public Number countDataSource(final Integer patientId) {
        return dao.countMuzimaGeneratedReports(patientId);
    }

    /**
     * Get settings with matching search term for a particular page.
     *
     * @param patientId    the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all settings with matching search term for a particular page.
     */
    @Override
    public List<MuzimaGeneratedReport> getPagedMuzimaGeneratedReports(final Integer patientId, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedMuzimaGeneratedReports(patientId, pageNumber, pageSize);
    }
}
