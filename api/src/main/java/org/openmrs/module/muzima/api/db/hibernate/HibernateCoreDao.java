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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.CoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public class HibernateCoreDao implements CoreDao {

    @Autowired
    protected DbSessionFactory sessionFactory;

    public DbSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(final DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#getObservations(java.util.List, java.util.List, Date, int, int)
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Obs> getObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                     final Date syncDate, final int startIndex, final int size) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Obs.class);
        criteria.createAlias("person", "person");
        criteria.add(Restrictions.in("person.uuid", patientUuids));
        criteria.createAlias("concept", "concept");
        criteria.add(Restrictions.in("concept.uuid", conceptUuids));
        if (syncDate != null) {
            criteria.add(Restrictions.or(
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                            Restrictions.isNull("dateVoided")),
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                            Restrictions.isNotNull("dateCreated"))));
        } else {
            criteria.add(Restrictions.eq("voided", false));
        }

        criteria.setMaxResults(size);
        criteria.setFirstResult(startIndex);
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#countObservations(java.util.List, java.util.List, Date)
     */
    @Override
    @Transactional(readOnly = true)
    public Number countObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                    final Date syncDate) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Obs.class);
        criteria.createAlias("person", "person");
        criteria.add(Restrictions.in("person.uuid", patientUuids));
        criteria.createAlias("concept", "concept");
        criteria.add(Restrictions.in("concept.uuid", conceptUuids));
        if (syncDate != null) {
            criteria.add(Restrictions.or(
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                            Restrictions.isNull("dateVoided")),
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                            Restrictions.isNotNull("dateCreated"))));
        } else {
            criteria.add(Restrictions.eq("voided", false));
        }

        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#getEncounters(java.util.List, Date, int, int)
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Encounter> getEncounters(final List<String> patientUuids, final Date syncDate,
                                         final int startIndex, final int size) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Encounter.class);
        criteria.createAlias("patient", "patient");
        criteria.add(Restrictions.in("patient.uuid", patientUuids));
        if (syncDate != null) {
            criteria.add(Restrictions.or(
                    Restrictions.or(
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                                    Restrictions.and(Restrictions.isNull("dateChanged"), Restrictions.isNull("dateVoided"))),
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateChanged"), Restrictions.ge("dateChanged", syncDate)),
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNull("dateVoided")))),
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNotNull("dateChanged")))));
        } else {
            criteria.add(Restrictions.eq("voided", false));
        }

        criteria.setMaxResults(size);
        criteria.setFirstResult(startIndex);
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#countEncounters(java.util.List, Date)
     */
    @Override
    @Transactional(readOnly = true)
    public Number countEncounters(final List<String> patientUuids, final Date syncDate) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Encounter.class);
        criteria.createAlias("patient", "patient");
        criteria.add(Restrictions.in("patient.uuid", patientUuids));
        if (syncDate != null) {
            criteria.add(Restrictions.or(
                    Restrictions.or(
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                                    Restrictions.and(Restrictions.isNull("dateChanged"), Restrictions.isNull("dateVoided"))),
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateChanged"), Restrictions.ge("dateChanged", syncDate)),
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNull("dateVoided")))),
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNotNull("dateChanged")))));
        } else {
            criteria.add(Restrictions.eq("voided", false));
        }

        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}
