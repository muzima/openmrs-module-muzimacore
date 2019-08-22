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

    @Override
    public Number countReportConfigurations(final String search) {
        return dao.countReportConfigurations(search);
    }

    @Override
    public List<ReportConfiguration> getPagedReportConfigurations(final String search, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedReportConfigurations(search, pageNumber, pageSize);
    }
}
