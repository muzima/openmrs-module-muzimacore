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
     * Get Report Configurations with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of Report Configurations for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ReportConfiguration> getPagedReportConfigurations(final String search, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = session().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("reportId", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("cohortId", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        criteria.addOrder(Order.desc("dateCreated"));
        return criteria.list();
    }

    @Override
    @Transactional
    public Number countReportConfigurations(final String search) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("reportId", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("cohortId", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
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
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        ReportConfiguration reportConfiguration = (ReportConfiguration)criteria.uniqueResult();
        return reportConfiguration;
    }

    @Override
    @Transactional
    public ReportConfiguration getReportConfigurationByReportUuid(String reportUuid){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("reportUuid", reportUuid));
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
