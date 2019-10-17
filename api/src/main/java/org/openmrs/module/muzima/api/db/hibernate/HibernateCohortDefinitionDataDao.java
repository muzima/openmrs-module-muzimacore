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
import org.openmrs.module.muzima.api.db.CohortDefinitionDataDao;
import org.openmrs.module.muzima.model.CohortDefinitionData;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

public class HibernateCohortDefinitionDataDao implements CohortDefinitionDataDao{
    @Autowired
    protected DbSessionFactory sessionFactory;
    protected Class mappedClass =CohortDefinitionData.class;

    public HibernateCohortDefinitionDataDao(){
        super();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CohortDefinitionData getById(Integer id){
        return (CohortDefinitionData)sessionFactory.getCurrentSession().get(mappedClass,id);
    }

    public CohortDefinitionData getByUuid(String uuid){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (CohortDefinitionData)criteria.uniqueResult();
    }

    public List<CohortDefinitionData> getAll(){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (List<CohortDefinitionData>) criteria.list();
    }

    public List<CohortDefinitionData> getPagedCohortDefinitionData(Integer pageNumber, Integer pageSize) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (List<CohortDefinitionData>) criteria.list();
    }

    public List<CohortDefinitionData> getByScheduled(Boolean scheduled){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("isScheduledForExecution", scheduled));
        return (List<CohortDefinitionData>) criteria.list();
    }

    public CohortDefinitionData saveOrUpdate(CohortDefinitionData object){
        sessionFactory.getCurrentSession().saveOrUpdate(object);
        return object;
    }

    public CohortDefinitionData update(CohortDefinitionData object){
        sessionFactory.getCurrentSession().update(object);
        return object;
    }

    public void delete(CohortDefinitionData object){
        sessionFactory.getCurrentSession().delete(object);
    }

    public Number count(){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}
