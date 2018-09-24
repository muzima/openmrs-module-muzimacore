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
package org.openmrs.module.muzima.api.db.hibernate;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.CohortUpdateHistoryDao;
import org.openmrs.module.muzima.model.CohortUpdateHistory;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateCohortUpdateHistoryDao implements CohortUpdateHistoryDao{
    @Autowired
    protected DbSessionFactory sessionFactory;

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CohortUpdateHistory saveOrUpdate(CohortUpdateHistory cohortUpdateHistory){
        sessionFactory.getCurrentSession().saveOrUpdate(cohortUpdateHistory);
        return cohortUpdateHistory;
    }
}
