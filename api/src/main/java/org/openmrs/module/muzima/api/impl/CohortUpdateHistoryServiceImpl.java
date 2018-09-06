package org.openmrs.module.muzima.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.CohortUpdateHistoryService;
import org.openmrs.module.muzima.api.db.CohortUpdateHistoryDao;
import org.openmrs.module.muzima.api.model.CohortUpdateHistory;

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
