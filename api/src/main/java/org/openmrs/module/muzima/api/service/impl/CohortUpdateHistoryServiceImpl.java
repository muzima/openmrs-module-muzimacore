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

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.CohortUpdateHistoryService;
import org.openmrs.module.muzima.api.db.CohortUpdateHistoryDao;
import org.openmrs.module.muzima.model.CohortUpdateHistory;

public class CohortUpdateHistoryServiceImpl extends BaseOpenmrsService implements CohortUpdateHistoryService {
    private CohortUpdateHistoryDao dao;

    public void setDao(CohortUpdateHistoryDao dao) {
        this.dao = dao;
    }

    public CohortUpdateHistoryDao getDao() {
        return dao;
    }

    public CohortUpdateHistory saveCohortUpdateHistory(CohortUpdateHistory cohortUpdateHistory){
        return dao.saveOrUpdate(cohortUpdateHistory);
    }
}
