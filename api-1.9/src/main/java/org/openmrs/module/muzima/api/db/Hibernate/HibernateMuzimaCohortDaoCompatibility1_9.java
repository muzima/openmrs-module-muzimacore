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
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaCohortDao;
import org.openmrs.module.muzima.api.service.CohortDefinitionDataService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component("muzima.MuzimaCohortDao")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 2.0.*")
public class HibernateMuzimaCohortDaoCompatibility1_9 implements MuzimaCohortDao {

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
                                   final int startIndex, final int size, final String defaultLocation,
                                   final String providerId) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Cohort.class);
        criteria.add(Expression.ilike("name", name, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("name"));
        if (syncDate != null) {
            String sql = "select distinct expanded_cohort_update_history.cohort_id,case when enable_filter_by_provider" +
                    " is null then 0 else enable_filter_by_provider end enable_filter_by_provider,case when" +
                    " enable_filter_by_location is null then 0 else enable_filter_by_location end enable_filter_by_location" +
                    " from expanded_cohort_update_history left join expanded_cohort_definition on" +
                    " expanded_cohort_update_history.cohort_id=expanded_cohort_definition.cohort_id where date_updated >= :syncDate";
            SQLQuery myquery = getSessionFactory().getCurrentSession().createSQLQuery(sql);
            myquery.setParameter("syncDate", syncDate);
            List<?> cohortIdList = myquery.list();
            List<Integer> cohortsWithoutFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithProviderFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithLocationFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithBothLocationAndProviderFilters = new ArrayList<Integer>();
            if(cohortIdList.size()>0) {
                for (int j = 0; j < cohortIdList.size(); j++) {
                    Object[] obj = (Object[]) cohortIdList.get(j);
                    boolean isfilterByLocation = false;
                    boolean isfilterByProvider = false;
                    int cohortId = 0;
                    for (int i = 0; i < obj.length; i++) {
                        int value = Integer.valueOf(obj[i].toString());
                        if (i == 0) {
                            cohortId = value;
                        }
                        if (i == 1) {
                            if (value == 1) {
                                isfilterByProvider = true;
                            }
                        }
                        if (i == 2) {
                            if (value == 1) {
                                isfilterByLocation = true;
                            }
                        }
                    }
                    if (isfilterByLocation && isfilterByProvider) {
                        cohortsWithBothLocationAndProviderFilters.add(cohortId);
                    } else if (isfilterByLocation) {
                        cohortsWithLocationFilter.add(cohortId);
                    } else if (isfilterByProvider) {
                        cohortsWithProviderFilter.add(cohortId);
                    } else {
                        cohortsWithoutFilter.add(cohortId);
                    }
                }
            }
            List<Integer> newCohortIdList = new ArrayList<Integer>();
            if(cohortsWithoutFilter.size()>0) {
                newCohortIdList.addAll(cohortsWithoutFilter);
            }
            if(cohortsWithLocationFilter.size()>0 && StringUtils.isNotEmpty(defaultLocation)) {
                String locationSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and location_id = :defaultLocation";
                SQLQuery locationQuery = getSessionFactory().getCurrentSession().createSQLQuery(locationSql);
                locationQuery.setParameter("syncDate", syncDate);
                locationQuery.setParameter("defaultLocation", defaultLocation);
                if (locationQuery.list().size() > 0) {
                    newCohortIdList.addAll(locationQuery.list());
                }
            }

            if(cohortsWithProviderFilter.size()>0 && StringUtils.isNotEmpty(providerId)) {
                String providerSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and provider_id = :providerId";
                SQLQuery providerQuery = getSessionFactory().getCurrentSession().createSQLQuery(providerSql);
                providerQuery.setParameter("syncDate", syncDate);
                providerQuery.setParameter("providerId", providerId);
                if (providerQuery.list().size() > 0) {
                    newCohortIdList.addAll(providerQuery.list());
                }
            }

            if(cohortsWithBothLocationAndProviderFilters.size()>0 && StringUtils.isNotEmpty(defaultLocation) &&
                    StringUtils.isNotEmpty(providerId)) {
                String providerAndLocationSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and provider_id = :providerId and" +
                        " location_id = :defaultLocation";
                SQLQuery providerAndLocationQuery = getSessionFactory().getCurrentSession().createSQLQuery(providerAndLocationSql);
                providerAndLocationQuery.setParameter("syncDate", syncDate);
                providerAndLocationQuery.setParameter("providerId", providerId);
                providerAndLocationQuery.setParameter("defaultLocation", defaultLocation);
                if (providerAndLocationQuery.list().size() > 0) {
                    newCohortIdList.addAll(providerAndLocationQuery.list());
                }
            }

            Disjunction disjunction = Restrictions.disjunction();
            if(newCohortIdList.size() > 0) {
                disjunction.add(Restrictions.in("id", newCohortIdList));
                criteria.add(disjunction);
            }

            if(newCohortIdList.size()==0){
                newCohortIdList.add(0);
            }

            criteria.add(
                    Restrictions.or(
                        Restrictions.in("id", newCohortIdList),
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
    public Number countCohorts(final String name, final Date syncDate, final String defaultLocation,
                               final String providerId) throws DAOException {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Cohort.class);
        criteria.add(Expression.ilike("name", name, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("name"));
        if (syncDate != null) {
            String sql = "select distinct expanded_cohort_update_history.cohort_id,case when enable_filter_by_provider " +
                    " is null then 0 else enable_filter_by_provider end enable_filter_by_provider,case when enable_filter_by_location" +
                    " is null then 0 else enable_filter_by_location end enable_filter_by_location from expanded_cohort_update_history" +
                    " left join expanded_cohort_definition on expanded_cohort_update_history.cohort_id=expanded_cohort_definition.cohort_id" +
                    " where date_updated >= :syncDate";
            SQLQuery myquery = getSessionFactory().getCurrentSession().createSQLQuery(sql);
            myquery.setParameter("syncDate", syncDate);
            List<?> cohortIdList = myquery.list();
            List<Integer> cohortsWithoutFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithProviderFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithLocationFilter = new ArrayList<Integer>();
            List<Integer> cohortsWithBothLocationAndProviderFilters = new ArrayList<Integer>();
            for(int j=0;j<cohortIdList.size();j++){
                Object [] obj= (Object[])cohortIdList.get(j);
                boolean isfilterByLocation = false;
                boolean isfilterByProvider = false;
                int cohortId = 0;
                for(int i=0;i<obj.length;i++) {
                    int value = Integer.valueOf(obj[i].toString());
                    if (i == 0) {
                        cohortId = value;
                    }
                    if (i == 1) {
                        if (value == 1) {
                            isfilterByProvider = true;
                        }
                    }
                    if (i == 2) {
                        if (value == 1) {
                            isfilterByLocation = true;
                        }
                    }
                }
                if(isfilterByLocation && isfilterByProvider){
                    cohortsWithBothLocationAndProviderFilters.add(cohortId);
                }else if(isfilterByLocation){
                    cohortsWithLocationFilter.add(cohortId);
                }else if(isfilterByProvider){
                    cohortsWithProviderFilter.add(cohortId);
                }else{
                    cohortsWithoutFilter.add(cohortId);
                }
            }
            List<Integer> newCohortIdList = new ArrayList<Integer>();
            if(cohortsWithoutFilter.size()>0) {
                newCohortIdList.addAll(cohortsWithoutFilter);
            }
            if(cohortsWithLocationFilter.size()>0 && StringUtils.isNotEmpty(defaultLocation)) {
                String locationSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and location_id = :defaultLocation";
                SQLQuery locationQuery = getSessionFactory().getCurrentSession().createSQLQuery(locationSql);
                locationQuery.setParameter("syncDate", syncDate);
                locationQuery.setParameter("defaultLocation", defaultLocation);
                if (locationQuery.list().size() > 0) {
                    newCohortIdList.addAll(locationQuery.list());
                }
            }

            if(cohortsWithProviderFilter.size()>0 && StringUtils.isNotEmpty(providerId)) {
                String providerSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and provider_id = :providerId";
                SQLQuery providerQuery = getSessionFactory().getCurrentSession().createSQLQuery(providerSql);
                providerQuery.setParameter("syncDate", syncDate);
                providerQuery.setParameter("providerId", providerId);
                if (providerQuery.list().size() > 0) {
                    newCohortIdList.addAll(providerQuery.list());
                }
            }

            if(cohortsWithBothLocationAndProviderFilters.size()>0 && StringUtils.isNotEmpty(defaultLocation) && StringUtils.isNotEmpty(providerId)) {
                String providerAndLocationSql = "select distinct cohort_id from muzima_cohort_metadata where (date_created >=" +
                        " :syncDate or date_changed >= :syncDate or date_voided >= :syncDate) and provider_id = :providerId and" +
                        " location_id = :defaultLocation";
                SQLQuery providerAndLocationQuery = getSessionFactory().getCurrentSession().createSQLQuery(providerAndLocationSql);
                providerAndLocationQuery.setParameter("syncDate", syncDate);
                providerAndLocationQuery.setParameter("providerId", providerId);
                providerAndLocationQuery.setParameter("defaultLocation", defaultLocation);
                if (providerAndLocationQuery.list().size() > 0) {
                    newCohortIdList.addAll(providerAndLocationQuery.list());
                }
            }

            Disjunction disjunction = Restrictions.disjunction();
            if(newCohortIdList.size() > 0) {
                disjunction.add(Restrictions.in("id", newCohortIdList));
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

    private List getAddedCohortMembersList(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws DAOException{
        String increaseConcatLimit = "SET SESSION group_concat_max_len=1000000";
        getSessionFactory().getCurrentSession().createSQLQuery(increaseConcatLimit).executeUpdate();
        CohortService cohortService = Context.getService(CohortService.class);
        Cohort cohort = cohortService.getCohortByUuid(cohortUuid);
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByCohortId(cohort.getId());

        String addedMembersSql = "";
        boolean isFilterByLocationSet = false;
        boolean isFilterByProviderSet = false;
        List addedMembersList = new ArrayList();
        if(cohortDefinitionData != null){
            if(cohortDefinitionData.getIsFilterByLocationEnabled() && cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation) && StringUtils.isNotEmpty(providerId)) {
                    isFilterByLocationSet = true;
                    isFilterByProviderSet = true;
                    addedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId" +
                            " and date_created >= :syncDate and provider_id = :providerId and location_id = :locationId";
                }else{
                    return addedMembersList;
                }
            }else if(cohortDefinitionData.getIsFilterByLocationEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation)) {
                    isFilterByLocationSet = true;
                    addedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId" +
                            " and date_created >= :syncDate and location_id = :locationId";
                }else{
                    return addedMembersList;
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(providerId)) {
                    isFilterByProviderSet = true;
                    addedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId and" +
                            " date_created >= :syncDate and provider_id = :providerId";
                }else{
                   return addedMembersList;
                }
            }else{
                addedMembersSql = "select GROUP_CONCAT(members_added) from expanded_cohort_update_history where date_updated >=" +
                        " :syncDate and cohort_id = :cohortId";
            }
        }else{
            addedMembersSql = "select GROUP_CONCAT(members_added) from expanded_cohort_update_history where date_updated >=" +
                    " :syncDate and cohort_id = :cohortId";
        }

        SQLQuery addedMembersQuery = getSessionFactory().getCurrentSession().createSQLQuery(addedMembersSql);
        if (syncDate != null) {
            addedMembersQuery.setParameter("syncDate", syncDate);
            addedMembersQuery.setParameter("cohortId", cohort.getCohortId());
            if(isFilterByLocationSet){
                addedMembersQuery.setParameter("locationId", defaultLocation);
            }if(isFilterByProviderSet){
                addedMembersQuery.setParameter("providerId", providerId);
            }
            List addedMembersQueryResult = addedMembersQuery.list();
            if (addedMembersQueryResult.size() > 0) {
                String memberIdResult = (String) addedMembersQueryResult.get(0);

                if (StringUtils.isNotBlank(memberIdResult)) {
                    String[] ids = memberIdResult.split(",");
                    for (String id : ids) {
                        if (StringUtils.isNotBlank(id)) {
                            addedMembersList.add(Integer.parseInt(id));
                        }
                    }
                }
            }
        }
        return addedMembersList;
    }

    private List getRemovedCohortMembersList(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws DAOException{
        String increaseConcatLimit = "SET SESSION group_concat_max_len=1000000";
        getSessionFactory().getCurrentSession().createSQLQuery(increaseConcatLimit).executeUpdate();
        CohortService cohortService = Context.getService(CohortService.class);
        Cohort cohort = cohortService.getCohortByUuid(cohortUuid);
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByCohortId(cohort.getId());

        String removedMembersSql = "";
        boolean isFilterByLocationSet = false;
        boolean isFilterByProviderSet = false;
        List removedMembersIds = new ArrayList();
        if(cohortDefinitionData != null){
            if(cohortDefinitionData.getIsFilterByLocationEnabled() && cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation) && StringUtils.isNotEmpty(providerId)) {
                    isFilterByLocationSet = true;
                    isFilterByProviderSet = true;
                    removedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId" +
                            " and date_voided >= :syncDate and provider_id = :providerId and location_id = :locationId";
                }else{
                    return removedMembersIds;
                }
            }else if(cohortDefinitionData.getIsFilterByLocationEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation)) {
                    isFilterByLocationSet = true;
                    removedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId" +
                            " and date_voided >= :syncDate and location_id = :locationId";
                }else{
                    return removedMembersIds;
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(providerId)) {
                    isFilterByProviderSet = true;
                    removedMembersSql = "select GROUP_CONCAT(patient_id) from muzima_cohort_metadata where cohort_id = :cohortId" +
                            " and date_voided >= :syncDate and provider_id = :providerId";
                }else{
                    return removedMembersIds;
                }
            }else{
                removedMembersSql = "select GROUP_CONCAT(members_removed) from expanded_cohort_update_history where date_updated >=" +
                        " :syncDate and cohort_id = :cohortId";
            }
        }else{
            removedMembersSql = "select GROUP_CONCAT(members_removed) from expanded_cohort_update_history where date_updated >=" +
                    " :syncDate and cohort_id = :cohortId";
        }

        SQLQuery removedMembersSqlQuery = getSessionFactory().getCurrentSession().createSQLQuery(removedMembersSql);
        if (syncDate != null) {
            removedMembersSqlQuery.setParameter("syncDate", syncDate);
            removedMembersSqlQuery.setParameter("cohortId", cohort.getCohortId());
            if(isFilterByLocationSet){
                removedMembersSqlQuery.setParameter("locationId", defaultLocation);
            }if(isFilterByProviderSet){
                removedMembersSqlQuery.setParameter("providerId", providerId);
            }
            List members = removedMembersSqlQuery.list();
            if (members.size() > 0) {
                String memberIdResult = (String) members.get(0);
                if (StringUtils.isNotBlank(memberIdResult)) {
                    String[] ids = memberIdResult.split(",");
                    for (String id : ids) {
                        if (StringUtils.isNotBlank(id)) {
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
                                     final int startIndex, final int size, final String defaultLocation, final String providerId) throws DAOException {

        //This will take care of cohort members who were added to cohort since sync date but have not been changed themselves
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> patientIds = new ArrayList<Integer>();

        CohortService cohortService = Context.getService(CohortService.class);
        Cohort cohort = cohortService.getCohortByUuid(cohortUuid);
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByCohortId(cohort.getId());
        String hqlQuery = "";
        boolean addLocationAndProviderParameter = false;
        boolean addLocationParameter = false;
        boolean addProviderParameter = false;
        if(cohortDefinitionData != null ){
            hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m, muzima_cohort_metadata mcm " +
                    " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                    " and c.cohort_id = m.cohort_id " +
                    " and mcm.patient_id = m.patient_id "+
                    " and mcm.cohort_id = m.cohort_id "+
                    " and mcm.voided = 0 "+
                    " and c.voided = false and p.voided = false ";
            if(cohortDefinitionData.getIsFilterByProviderEnabled() && cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation) && StringUtils.isNotEmpty(providerId)){
                    addLocationAndProviderParameter = true;
                    hqlQuery = hqlQuery +" and mcm.location_id = :defaultLocation and mcm.provider_id=:providerId ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation)){
                    addLocationParameter = true;
                    hqlQuery = hqlQuery +" and mcm.location_id = :defaultLocation ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(providerId)){
                    addProviderParameter = true;
                    hqlQuery = hqlQuery +" and mcm.provider_id = :providerId ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else{
                hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                        " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                        " and c.cohort_id = m.cohort_id " +
                        " and c.voided = false and p.voided = false ";
            }
        }else {
            hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                    " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                    " and c.cohort_id = m.cohort_id " +
                    " and c.voided = false and p.voided = false ";
        }

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

        if(addLocationAndProviderParameter){
            query.setParameter("defaultLocation", defaultLocation);
            query.setParameter("providerId", providerId);
        }
        if(addLocationParameter){
            query.setParameter("defaultLocation", defaultLocation);
        }
        if(addProviderParameter){
            query.setParameter("providerId", providerId);
        }
        query.setMaxResults(size);
        query.setFirstResult(startIndex);
        patientIds = query.list();
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
    public Number countPatients(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws DAOException {
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> patientIds = new ArrayList<Integer>();

        CohortService cohortService = Context.getService(CohortService.class);
        Cohort cohort = cohortService.getCohortByUuid(cohortUuid);
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByCohortId(cohort.getId());

        String hqlQuery = "";
        boolean addLocationAndProviderParameter = false;
        boolean addLocationParameter = false;
        boolean addProviderParameter = false;
        if(cohortDefinitionData != null ){
            hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m, muzima_cohort_metadata mcm " +
                    " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                    " and c.cohort_id = m.cohort_id " +
                    " and mcm.patient_id = m.patient_id "+
                    " and mcm.cohort_id = m.cohort_id "+
                    " and mcm.voided = 0 "+
                    " and c.voided = false and p.voided = false ";
            if(cohortDefinitionData.getIsFilterByProviderEnabled() && cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation) && StringUtils.isNotEmpty(providerId)){
                    addLocationAndProviderParameter = true;
                    hqlQuery = hqlQuery +" and mcm.location_id = :defaultLocation and mcm.provider_id = :providerId ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(defaultLocation)){
                    addLocationParameter = true;
                    hqlQuery = hqlQuery +" and mcm.location_id = :defaultLocation ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else if(cohortDefinitionData.getIsFilterByProviderEnabled()){
                if(StringUtils.isNotEmpty(providerId)){
                    addProviderParameter = true;
                    hqlQuery = hqlQuery +" and mcm.provider_id = :providerId ";
                }else{
                    hqlQuery = hqlQuery +" and mcm.patient_id = 0 ";
                }
            }else{
                hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                        " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                        " and c.cohort_id = m.cohort_id " +
                        " and c.voided = false and p.voided = false ";
            }
        }else {
            hqlQuery = " select p.patient_id from patient p, cohort c, cohort_member m " +
                    " where c.uuid = :uuid and p.patient_id = m.patient_id " +
                    " and c.cohort_id = m.cohort_id " +
                    " and c.voided = false and p.voided = false ";
        }

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

        if(addLocationAndProviderParameter){
            query.setParameter("defaultLocation", defaultLocation);
            query.setParameter("providerId", providerId);
        }
        if(addLocationParameter){
            query.setParameter("defaultLocation", defaultLocation);
        }
        if(addProviderParameter){
            query.setParameter("providerId", providerId);
        }

        patientIds = query.list();
        Set<Integer> finalPatientIds = new HashSet<Integer>();

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
    public List<Patient> getPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws DAOException{
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);

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

    public boolean hasCohortChangedSinceDate(final String cohortUuid, final Date syncDate,final String defaultLocation,
                                             final String providerId) throws DAOException{
        List<Patient> removedPatients = getPatientsRemovedFromCohort(cohortUuid,syncDate,defaultLocation, providerId);
        if(removedPatients.size() > 0){
            return true;
        }
        List<Integer> addedMembersIds = getAddedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);
        List<Integer> removedMembersIds = getRemovedCohortMembersList(cohortUuid, syncDate, defaultLocation, providerId);

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

    @Override
    public List<Integer> getCohortWithFilters() throws DAOException {
        CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
        List<CohortDefinitionData> cohortDefinitionData = cohortDefinitionDataService.getAllCohortDefinitionData();
        List<Integer> integerList = new ArrayList<Integer>();
        for(CohortDefinitionData cdd : cohortDefinitionData) {
            if(cdd.getIsFilterByProviderEnabled() || cdd.getIsFilterByLocationEnabled()){
                integerList.add(cdd.getCohortId());
            }
        }
        return integerList;
    }
}
