package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.module.muzima.api.db.CohortUpdateHistoryDao;
import org.openmrs.module.muzima.api.model.CohortUpdateHistory;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateCohortUpdateHistoryDao implements CohortUpdateHistoryDao{
    @Autowired
    protected SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CohortUpdateHistory saveOrUpdate(CohortUpdateHistory cohortUpdateHistory){
        System.out.println("Saving cohort update history : "+cohortUpdateHistory.getMembersAdded());
        sessionFactory.getCurrentSession().saveOrUpdate(cohortUpdateHistory);
        return cohortUpdateHistory;
    }
}
