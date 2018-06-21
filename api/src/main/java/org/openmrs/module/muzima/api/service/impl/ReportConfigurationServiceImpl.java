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
import org.openmrs.module.muzima.api.db.ReportConfigurationDao;
import org.openmrs.module.muzima.api.service.ReportConfigurationService;
import org.openmrs.module.muzima.model.ReportConfiguration;

import java.util.List;

public class ReportConfigurationServiceImpl extends BaseOpenmrsService implements ReportConfigurationService {
    private final Log log = LogFactory.getLog(this.getClass());
    private ReportConfigurationDao dao;

    public ReportConfigurationServiceImpl(ReportConfigurationDao dao){
        this.dao = dao;
    }

    public ReportConfigurationDao getDao() {
        return dao;
    }

    public void setDao(ReportConfigurationDao dao) {
        this.dao = dao;
    }

    @Override
    public ReportConfiguration getReportConfigurationById(final  Integer id){
        return dao.getReportConfigurationById(id);
    }

    @Override
    public ReportConfiguration getReportConfigurationByUuid(final String uuid){
        return dao.getReportConfigurationByUuid(uuid);
    }

    @Override
    public ReportConfiguration getReportConfigurationByReportId(final String reportId){
        return dao.getReportConfigurationByReportId(reportId);
    }

    @Override
    public ReportConfiguration saveReportConfiguration(ReportConfiguration reportConfiguration){
        return dao.saveOrUpdateReportConfiguration(reportConfiguration);
    }

    @Override
    public void deleteReportConfiguration(ReportConfiguration reportConfiguration){
        dao.deleteReportConfiguration(reportConfiguration);
    }

    @Override
    public List<ReportConfiguration> getAllReportConfigurations(){
        return dao.getAll();
    }

    @Override
    public Number countReportConfigurations(){
        return dao.countReportConfigurations();
    }

    /**
     * Get the total number of the settings in the database with partial matching search term.
     *
     *
     * @param search the search term.
     * @return the total number of the settings in the database.
     */
    @Override
    public Number countDataSource(final String search) {
        System.out.println("6666666666666666666666666\n");
        return dao.countReportConfigurations(search);
    }

    /**
     * Get settings with matching search term for a particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of all settings with matching search term for a particular page.
     */
    @Override
    public List<ReportConfiguration> getPagedReportConfigurations(final String search, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedReportConfigurations(search, pageNumber, pageSize);
    }
}
