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
package org.openmrs.module.muzima.api.db.Hibernate;

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
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.CoreDao;
import org.openmrs.module.muzima.api.db.MuzimaCohortDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component("muzima.MuzimaCohortDao")
@OpenmrsProfile(openmrsPlatformVersion = "2.1")
public class HibernateMuzimaCohortDaoCompatibility2_1 implements MuzimaCohortDao {

    @Autowired
    protected DbSessionFactory sessionFactory;

    public DbSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(final DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Cohort> getCohorts(final String name, final Date syncDate,
                                   final int startIndex, final int size) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Cohort.class);
        criteria.add(Expression.ilike("name", name, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("name"));
        if (syncDate != null) {


            String sql = "select cohort_id from expanded_cohort_update_history where date_updated >= :syncDate";
            SQLQuery myquery = getSessionFactory().getCurrentSession().createSQLQuery(sql);
            myquery.setParameter("syncDate", syncDate);

            Disjunction disjunction = Restrictions.disjunction();
            if(myquery.list().size() > 0) {
                disjunction.add(Restrictions.in("id", myquery.list()));
                criteria.add(disjunction);
            }


            criteria.add(
                    Restrictions.or(
                        Restrictions.in("id", myquery.list()),
                        Restrictions.or(
                            Restrictions.or(
                                Restrictions.and(
                                        Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                                        Restrictions.and(Restrictions.isNull("dateChanged"), Restrictions.isNull("dateVoided"))),
                                Restrictions.and(
                                        Restrictions.and(Restrictions.isNotNull("dateChanged"), Restrictions.ge("dateChanged", syncDate)),
                                        Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNull("dateVoided")))),
                            Restrictions.and(
                                Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                                Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNotNull("dateChanged")))
                        )
                    )
            );
        }
        criteria.add(Restrictions.eq("voided", false));

        criteria.setMaxResults(size);
        criteria.setFirstResult(startIndex);
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Number countCohorts(final String name, final Date syncDate) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Cohort.class);
        criteria.add(Expression.ilike("name", name, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("name"));
        if (syncDate != null) {


            String sql = "select cohort_id from expanded_cohort_update_history where date_updated >= :syncDate";
            SQLQuery myquery = getSessionFactory().getCurrentSession().createSQLQuery(sql);
            myquery.setParameter("syncDate", syncDate);

            Disjunction disjunction = Restrictions.disjunction();
            if(myquery.list().size() > 0) {
                disjunction.add(Restrictions.in("id", myquery.list()));
                criteria.add(disjunction);
            }


            criteria.add(
                    Restrictions.or(
                            Restrictions.or(
                                    Restrictions.and(
                                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                                            Restrictions.and(Restrictions.isNull("dateChanged"), Restrictions.isNull("dateVoided"))),
                                    Restrictions.and(
                                            Restrictions.and(Restrictions.isNotNull("dateChanged"), Restrictions.ge("dateChanged", syncDate)),
                                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNull("dateVoided")))),
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateVoided"), Restrictions.ge("dateVoided", syncDate)),
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNotNull("dateChanged")))
                    )
            );
        }
        criteria.add(Restrictions.eq("voided", false));

        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    private List getAddedCohortMembersList(final String cohortUuid, final Date syncDate) throws DAOException{
        String increaseConcatLimit = "SET SESSION group_concat_max_len=1000000";
        getSessionFactory().getCurrentSession().createSQLQuery(increaseConcatLimit).executeUpdate();
        String addedMembersSql = "select GROUP_CONCAT(e.members_added) from expanded_cohort_update_history e, cohort c" +
                " where e.date_updated >= :syncDate and e.cohort_id = c.cohort_id and c.uuid = :cohortUuid";
        SQLQuery addedMembersQuery = getSessionFactory().getCurrentSession().createSQLQuery(addedMembersSql);
        List addedMembersList = new ArrayList();
        if (syncDate != null) {
            addedMembersQuery.setParameter("syncDate", syncDate);
            addedMembersQuery.setParameter("cohortUuid", cohortUuid);
            List addedMembersQueryResult = addedMembersQuery.list();
            if(addedMembersQueryResult.size() > 0){
                String memberIdResult = (String)addedMembersQueryResult.get(0);

                if(StringUtils.isNotBlank(memberIdResult)) {
                    String[] ids = memberIdResult.split(",");
                    for (String id : ids) {
                        if(StringUtils.isNotBlank(id)) {
                            addedMembersList.add(Integer.parseInt(id));
                        }
                    }
                }
            }
        }
        return addedMembersList;
    }

    private List getRemovedCohortMembersList(final String cohortUuid, final Date syncDate) throws DAOException{
        String increaseConcatLimit = "SET SESSION group_concat_max_len=1000000";
        getSessionFactory().getCurrentSession().createSQLQuery(increaseConcatLimit).executeUpdate();
        String removedMembersSql = "select GROUP_CONCAT(e.members_removed) from expanded_cohort_update_history e, cohort" +
                " c where e.date_updated >= :syncDate and e.cohort_id = c.cohort_id and c.uuid = :cohortUuid";
        SQLQuery removedMembersSqlQuery = getSessionFactory().getCurrentSession().createSQLQuery(removedMembersSql);
        List removedMembersIds = new ArrayList();
        if (syncDate != null) {
            removedMembersSqlQuery.setParameter("syncDate", syncDate);
            removedMembersSqlQuery.setParameter("cohortUuid", cohortUuid);
            List members = removedMembersSqlQuery.list();
            if(members.size() > 0){
                String memberIdResult = (String)members.get(0);
                if(StringUtils.isNotBlank(memberIdResult)) {
                    String[] ids = memberIdResult.split(",");
                    for (String id : ids) {
                        if(StringUtils.isNotBlank(id)) {
                            removedMembersIds.add(Integer.parseInt(id));
                        }
                    }
                }
            }
        }
        return removedMembersIds;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(final String cohortUuid, final Date syncDate,
                                     final int startIndex, final int size) throws DAOException {

        //This will take care of cohort members who were added to cohort since sync date but have not been changed themselves
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate);

        String hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                " and c.cohort_id = m.cohort_id " +
                " and c.voided = false and p.voided = false " +
                " and m.end_date is null ";
        if (syncDate != null) {
            hqlQuery = hqlQuery +
                    " and ((p.date_created is not null and p.date_changed is null and p.date_voided is null and p.date_created >= :syncDate) or " +
                    "       (p.date_created is not null and p.date_changed is not null and p.date_voided is null and p.date_changed >= :syncDate) or " +
                    "       (p.date_created is not null and p.date_changed is not null and p.date_voided is not null and p.date_voided >= :syncDate)) ";
        }
        SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(hqlQuery);
        query.setParameter("uuid", cohortUuid);
        if (syncDate != null) {
            query.setParameter("syncDate", syncDate);
        }
        List patientIds = query.list();
        for(int memberId:removedMembersIds) {
            int index = addedMembersIds.indexOf(memberId);
            if(index >= 0) {
                addedMembersIds.remove(index);
            }
        }
        patientIds.addAll(addedMembersIds);

        if (!patientIds.isEmpty()) {
            Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Patient.class);
            criteria.add(Restrictions.in("patientId", patientIds));
            return criteria.list();
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public Number countPatients(final String cohortUuid, final Date syncDate) throws DAOException {
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate);
        List<Integer> patientIds = new ArrayList<Integer>();

        String hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                " and c.cohort_id = m.cohort_id " +
                " and c.voided = false and p.voided = false "+
                " and m.end_date is null ";
        if (syncDate != null) {
            hqlQuery = hqlQuery +
                    " and ((p.date_created is not null and p.date_changed is null and p.date_voided is null and p.date_created >= :syncDate) or " +
                    "       (p.date_created is not null and p.date_changed is not null and p.date_voided is null and p.date_changed >= :syncDate) or " +
                    "       (p.date_created is not null and p.date_changed is not null and p.date_voided is not null and p.date_voided >= :syncDate)) ";
        }

        SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(hqlQuery);
        query.setParameter("uuid", cohortUuid);
        if (syncDate != null) {
            query.setParameter("syncDate", syncDate);
        }

        patientIds = query.list();
        List<Integer> finalPatientIds = new ArrayList<Integer>();
        for(int memberId:removedMembersIds) {
            int index = addedMembersIds.indexOf(memberId);
            if(index >= 0) {
                addedMembersIds.remove(index);
            }
        }
        finalPatientIds.addAll(addedMembersIds);
        if(patientIds.size()>0) {
            for (Integer patientId : patientIds) {
                if (!finalPatientIds.contains(patientId)) {
                    finalPatientIds.add(patientId);
                }
            }
        }
        return  finalPatientIds.size();
    }



    @Transactional(readOnly = true)
    public List<Patient> getPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate) throws DAOException{
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate);

        if(!removedMembersIds.isEmpty()){
            for(int memberId:addedMembersIds) {
                int index = removedMembersIds.indexOf(memberId);
                if(index >= 0) {
                    removedMembersIds.remove(index);
                }
            }

            if(removedMembersIds.size() > 0) {
                Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Patient.class);
                criteria.add(Restrictions.in("patientId", removedMembersIds));
                return criteria.list();
            }
        }
        return Collections.emptyList();
    }

    public boolean hasCohortChangedSinceDate(final String cohortUuid, final Date syncDate) throws DAOException{
        List<Patient> removedPatients = getPatientsRemovedFromCohort(cohortUuid,syncDate);
        if(removedPatients.size() > 0){
            return true;
        }
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate);

        for(int memberId:removedMembersIds) {
            int index = addedMembersIds.indexOf(memberId);
            if(index >= 0) {
                addedMembersIds.remove(index);
            }
        }

        if(addedMembersIds.size() > 0){
            return true;
        }

        return false;

    }
}
