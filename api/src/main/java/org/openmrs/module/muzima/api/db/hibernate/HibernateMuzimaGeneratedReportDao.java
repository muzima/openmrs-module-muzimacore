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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaGeneratedReportDao;
import org.openmrs.module.muzima.model.MuzimaGeneratedReport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateMuzimaGeneratedReportDao implements MuzimaGeneratedReportDao {
    private DbSessionFactory sessionFactory;
    protected Class mappedClass = MuzimaGeneratedReport.class;
    private final Log log = LogFactory.getLog(this.getClass());

    public HibernateMuzimaGeneratedReportDao(DbSessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<MuzimaGeneratedReport> getAll() {
        Criteria criteria = session().createCriteria(MuzimaGeneratedReport.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    /**
     * Get generatedReports with matching search term for particular page.
     *
     * @param patientId     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of generatedReports for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MuzimaGeneratedReport> getPagedMuzimaGeneratedReports(final Integer patientId, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = session().createCriteria(mappedClass);
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.eq("patientId", patientId));
            criteria.add(disjunction);
      
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

    /**
     * Get the total number of settings with matching search term.
     *
     *
     * @param patientId the search term.
     * @return total number of settings in the database.
     */
    @Override
    @Transactional
    public Number countMuzimaGeneratedReports(final Integer patientId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
   
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.eq("patientId", patientId));
            criteria.add(disjunction);
        
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
        
    }

    @Override
    @Transactional
    public Number countMuzimaGeneratedReports(){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
    
    @Override
    public MuzimaGeneratedReport getMuzimaGeneratedReportById(Integer id) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return (MuzimaGeneratedReport) criteria.uniqueResult();
    }

    
    @Override
    public MuzimaGeneratedReport getLastPriorityMuzimaGeneratedReportByPatientId(Integer patientId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("priority", Boolean.TRUE));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setMaxResults(1);
        return (MuzimaGeneratedReport) criteria.uniqueResult();
        
    }
    
    @Override
    @Transactional
    public List<MuzimaGeneratedReport> getMuzimaGeneratedReportByPatientId(Integer patientId){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return criteria.list();
    }

    @Override
    @Transactional
    public MuzimaGeneratedReport getMuzimaGeneratedReportByUuid(String uuid){
        MuzimaGeneratedReport muzimaGeneratedReport = null;
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        muzimaGeneratedReport = (MuzimaGeneratedReport)criteria.uniqueResult();
        return muzimaGeneratedReport;
    }

    @Override
    @Transactional
    public MuzimaGeneratedReport  getLastMuzimaGeneratedReportByPatientIdANDCohortReportConfigId(Integer patientId, Integer cohortReportConfigId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("cohortReportConfigId", cohortReportConfigId));
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setMaxResults(1);
       return (MuzimaGeneratedReport)criteria.uniqueResult();
    }
    
    @Override
    public List<MuzimaGeneratedReport> getMuzimaGeneratedReportByCohortReportConfigId(Integer cohortReportConfigId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("cohortReportConfigId", cohortReportConfigId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return criteria.list();
    }
    
    @Override
    @Transactional
    public MuzimaGeneratedReport saveOrUpdateMuzimaGeneratedReport(final MuzimaGeneratedReport muzimaGeneratedReport){
        session().saveOrUpdate(muzimaGeneratedReport);
        return muzimaGeneratedReport;
    }

    @Override
    public void deleteMuzimaGeneratedReport(MuzimaGeneratedReport muzimaGeneratedReport){
        session().delete(muzimaGeneratedReport);
    }

    private DbSession session() {
        return sessionFactory.getCurrentSession();
    }
}
