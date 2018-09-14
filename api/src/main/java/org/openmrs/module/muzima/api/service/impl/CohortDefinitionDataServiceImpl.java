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
import org.openmrs.module.muzima.api.CohortDefinitionDataService;
import org.openmrs.module.muzima.api.db.CohortDefinitionDataDao;
import org.openmrs.module.muzima.model.CohortDefinitionData;

import java.util.List;

public class CohortDefinitionDataServiceImpl extends BaseOpenmrsService implements CohortDefinitionDataService {
    private final Log log = LogFactory.getLog(this.getClass());

    private CohortDefinitionDataDao dao;

    public void setDao(CohortDefinitionDataDao dao) {
        this.dao = dao;
    }

    public CohortDefinitionDataDao getDao() {
        return dao;
    }

    public CohortDefinitionData getCohortDefinitionDataById(final Integer id){
        return dao.getById(id);
    }

    public CohortDefinitionData getCohortDefinitionDataByUuid(final String uuid){
        return dao.getByUuid(uuid);
    }

    public CohortDefinitionData saveCohortDefinitionData(final CohortDefinitionData cohortDefinitionData){
        return dao.saveOrUpdate(cohortDefinitionData);

    }
    public void deleteCohortDefinitionData(final CohortDefinitionData cohortDefinitionData){
        dao.delete(cohortDefinitionData);
    }
    public List<CohortDefinitionData> getAllCohortDefinitionData(){
        return dao.getAll();
    }
    public List<CohortDefinitionData> getAllScheduledCohortDefinitionData(){
        return dao.getByScheduled(true);
    }
    public Number countCohortDefinitionData(){
        return dao.count();
    }
}
