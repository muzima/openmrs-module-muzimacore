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
     * @see CoreDao#getObservations(java.util.List, java.util.List, Date, int, int, int)
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Obs> getObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                     final Date syncDate, final int startIndex, final int size,final int maxObsPerPatientPerConcept) throws DAOException {
        if(syncDate != null) {
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
        }else{
            List<Obs> obs = new ArrayList<Obs>();
            for(String patientUuid:patientUuids) {
                for(String conceptUuid:conceptUuids){
                    Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Obs.class);
                    criteria.createAlias("person", "person");
                    criteria.add(Restrictions.eq("person.uuid", patientUuid));
                    criteria.createAlias("concept", "concept");
                    criteria.add(Restrictions.eq("concept.uuid", conceptUuid));
                    criteria.add(Restrictions.eq("voided", false));
                    criteria.addOrder(Order.desc("dateCreated"));
                    criteria.setMaxResults(maxObsPerPatientPerConcept);
                    criteria.setFirstResult(0);
                    obs.addAll(criteria.list());
                }
            }
            return obs;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#countObservations(java.util.List, java.util.List, Date, int)
     */
    @Override
    @Transactional(readOnly = true)
    public Number countObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                    final Date syncDate, final int maxObsPerPatientPerConcept) throws DAOException {
        int obsCount = 0;
        if(syncDate != null) {
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
        }else{
            for(String patientUuid:patientUuids) {
                for(String conceptUuid:conceptUuids){
                    Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Obs.class);
                    criteria.createAlias("person", "person");
                    criteria.add(Restrictions.eq("person.uuid", patientUuid));
                    criteria.createAlias("concept", "concept");
                    criteria.add(Restrictions.eq("concept.uuid", conceptUuid));
                    criteria.add(Restrictions.eq("voided", false));
                    criteria.setMaxResults(maxObsPerPatientPerConcept);
                    criteria.setFirstResult(0);

                    criteria.setProjection(Projections.rowCount());
                    obsCount += ((Number) criteria.uniqueResult()).intValue();
                }
            }
            return obsCount;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#getEncounters(java.util.List, int, Date)
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Encounter> getEncounters(final List<String> patientUuids, final int maxEncounterResultsPerPatient,
                                         final Date syncDate) throws DAOException {
        List<Encounter> encounters = new ArrayList<Encounter>();
        for(String patientUuid:patientUuids) {
            Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Encounter.class);
            criteria.createAlias("patient", "patient");
            criteria.add(Restrictions.eq("patient.uuid", patientUuid));
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

                criteria.addOrder(Order.desc("dateCreated"));
                criteria.addOrder(Order.desc("dateChanged"));
                criteria.addOrder(Order.desc("dateVoided"));
            } else {
                criteria.add(Restrictions.eq("voided", false));
                criteria.addOrder(Order.desc("dateCreated"));
                criteria.addOrder(Order.desc("dateChanged"));
            }
            criteria.setMaxResults(maxEncounterResultsPerPatient);
            criteria.setFirstResult(0);
            encounters.addAll(criteria.list());
        }
        return encounters;
    }

    /**
     * {@inheritDoc}
     *
     * @see CoreDao#countEncounters(java.util.List, int, Date)
     */
    @Override
    @Transactional(readOnly = true)
    public Number countEncounters(final List<String> patientUuids, final int maxEncounterResultsPerPatient, final Date syncDate) throws DAOException {
        int encountersCount = 0;
        for(String patientUuid:patientUuids) {
            Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Encounter.class);
            criteria.createAlias("patient", "patient");
            criteria.add(Restrictions.eq("patient.uuid", patientUuid));
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
            criteria.setMaxResults(maxEncounterResultsPerPatient);
            criteria.setFirstResult(0);

            criteria.setProjection(Projections.rowCount());
            encountersCount += ((Number) criteria.uniqueResult()).intValue();
        }
        return encountersCount;
    }
}
