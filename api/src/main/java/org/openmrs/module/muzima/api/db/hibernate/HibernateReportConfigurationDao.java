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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.ReportConfigurationDao;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateReportConfigurationDao implements ReportConfigurationDao {
    private DbSessionFactory sessionFactory;
    protected Class mappedClass = ReportConfiguration.class;
    private final Log log = LogFactory.getLog(this.getClass());

    public HibernateReportConfigurationDao(DbSessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<ReportConfiguration> getAll() {
        Criteria criteria = session().createCriteria(ReportConfiguration.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    /**
     * Get reportConfigurations with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of reportConfigurations for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ReportConfiguration> getPagedReportConfigurations(final String search, final Integer pageNumber, final Integer pageSize) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaa\n");
        Criteria criteria = session().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            //disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("reportId", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("cohortId", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\n");
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        criteria.addOrder(Order.desc("dateCreated"));
        System.out.println("cccccccccccccccccccccccccccccccccc\n");
        return criteria.list();
    }

    /**
     * Get the total number of settings with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of settings in the database.
     */
    @Override
    @Transactional
    public Number countReportConfigurations(final String search) {
        System.out.println("7777777777777777777777777777\n");
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            System.out.println("888888888888888888888888888888\n");
            Disjunction disjunction = Restrictions.disjunction();
            //disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("reportId", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("cohortId", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        System.out.println("99999999999999999999999999999999\n");
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        System.out.println("1010101010101010101010101010\n");
        return (Number) criteria.uniqueResult();
        
    }

    @Override
    @Transactional
    public Number countReportConfigurations(){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public ReportConfiguration getReportConfigurationById(Integer id){
        return (ReportConfiguration)session().get(mappedClass,id);
    }

    @Override
    @Transactional
    public ReportConfiguration getReportConfigurationByUuid(String uuid){
        ReportConfiguration setting = null;
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        setting = (ReportConfiguration)criteria.uniqueResult();
        return setting;
    }

    @Override
    @Transactional
    public ReportConfiguration getReportConfigurationByReportId(String reportId){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("reportId", reportId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return (ReportConfiguration)criteria.uniqueResult();
    }

    @Override
    @Transactional
    public ReportConfiguration saveOrUpdateReportConfiguration(final ReportConfiguration reportConfiguration){
        session().saveOrUpdate(reportConfiguration);
        return reportConfiguration;
    }

    @Override
    public void deleteReportConfiguration(ReportConfiguration reportConfiguration){
        session().delete(reportConfiguration);
    }

    private DbSession session() {
        return sessionFactory.getCurrentSession();
    }
}
