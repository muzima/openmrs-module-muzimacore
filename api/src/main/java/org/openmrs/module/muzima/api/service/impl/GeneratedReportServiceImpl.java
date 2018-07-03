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
import org.openmrs.module.muzima.api.db.GeneratedReportDao;
import org.openmrs.module.muzima.api.service.GeneratedReportService;
import org.openmrs.module.muzima.model.GeneratedReport;

import java.util.List;

public class GeneratedReportServiceImpl extends BaseOpenmrsService implements GeneratedReportService {
    private final Log log = LogFactory.getLog(this.getClass());
    private GeneratedReportDao dao;

    public GeneratedReportServiceImpl(GeneratedReportDao dao){
        this.dao = dao;
    }

    public GeneratedReportDao getDao() {
        return dao;
    }

    public void setDao(GeneratedReportDao dao) {
        this.dao = dao;
    }

    @Override
    public List<GeneratedReport> getGeneratedReportByPatientId(final  Integer id){
        return dao.getGeneratedReportByPatientId(id);
    }
    
    @Override
    public GeneratedReport getGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId,
            Integer cohortReportConfigId) {
        return dao.getGeneratedReportByPatientIdANDCohortReportConfigId(patientId,cohortReportConfigId);
    }
    
    @Override
    public GeneratedReport getGeneratedReportById(Integer id) {
        return dao.getGeneratedReportById(id);
    }
    
    
    @Override
    public GeneratedReport getGeneratedReportByUuid(final String uuid){
        return dao.getGeneratedReportByUuid(uuid);
    }

    

    @Override
    public GeneratedReport saveGeneratedReport(GeneratedReport generatedReport){
        return dao.saveOrUpdateGeneratedReport(generatedReport);
    }

    @Override
    public void deleteGeneratedReport(GeneratedReport GeneratedReport){
        dao.deleteGeneratedReport(GeneratedReport);
    }

    @Override
    public List<GeneratedReport> getAllGeneratedReports(){
        return dao.getAll();
    }

    @Override
    public Number countGeneratedReports(){
        return dao.countGeneratedReports();
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
        System.out.println("6666666666666666666666666\n");
        return dao.countGeneratedReports(patientId);
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
    public List<GeneratedReport> getPagedGeneratedReports(final Integer patientId, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedGeneratedReports(patientId, pageNumber, pageSize);
    }
}
