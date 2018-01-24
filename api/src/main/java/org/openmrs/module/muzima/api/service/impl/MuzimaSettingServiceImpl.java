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
import org.openmrs.module.muzima.api.db.MuzimaSettingDao;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaSetting;

import java.util.List;

public class MuzimaSettingServiceImpl extends BaseOpenmrsService implements MuzimaSettingService{
    private final Log log = LogFactory.getLog(this.getClass());
    private MuzimaSettingDao dao;

    public MuzimaSettingServiceImpl(MuzimaSettingDao dao){
        this.dao = dao;
    }

    public MuzimaSettingDao getDao() {
        return dao;
    }

    public void setDao(MuzimaSettingDao dao) {
        this.dao = dao;
    }

    @Override
    public MuzimaSetting getMuzimaSettingById(final  Integer id){
        return dao.getSettingById(id);
    }

    @Override
    public MuzimaSetting getMuzimaSettingByUuid(final String uuid){
        return dao.getSettingByUuid(uuid);
    }

    @Override
    public MuzimaSetting getMuzimaSettingByProperty(final String property){
        return dao.getSettingByProperty(property);
    }

    @Override
    public MuzimaSetting saveMuzimaSetting(MuzimaSetting setting){
        return dao.saveOrUpdateSetting(setting);
    }

    @Override
    public void deleteMuzimaSetting(MuzimaSetting setting){
        dao.deleteSetting(setting);
    }

    @Override
    public List<MuzimaSetting> getAllMuzimaSettings(){
        return dao.getAll();
    }

    @Override
    public Number countMuzimaSettings(){
        return dao.countSettings();
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
        return dao.countSettings(search);
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
    public List<MuzimaSetting> getPagedSettings(final String search, final Integer pageNumber, final Integer pageSize) {
        return dao.getPagedSettings(search, pageNumber, pageSize);
    }
}
