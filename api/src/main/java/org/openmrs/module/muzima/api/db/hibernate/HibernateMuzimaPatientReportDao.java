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
import org.openmrs.module.muzima.api.db.MuzimaPatientReportDao;
import org.openmrs.module.muzima.model.MuzimaPatientReport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateMuzimaPatientReportDao implements MuzimaPatientReportDao {
    private DbSessionFactory sessionFactory;
    protected Class mappedClass = MuzimaPatientReport.class;
    private final Log log = LogFactory.getLog(this.getClass());

    public HibernateMuzimaPatientReportDao(DbSessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<MuzimaPatientReport> getAllMuzimaPatientReports() {
        Criteria criteria = session().createCriteria(MuzimaPatientReport.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    /**
     * Get Patient Reports with matching search term for particular page.
     *
     * @param patientId     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of patient reports for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MuzimaPatientReport> getPagedMuzimaPatientReports(final Integer patientId, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = session().createCriteria(mappedClass);
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.eq("patientId", patientId));
            criteria.add(disjunction);
      
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("status", "completed"));
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
     * Get the total number of patient reports with matching search term.
     *
     * @param patientId the search term.
     * @return total number of patient reports in the database.
     */
    @Override
    @Transactional
    public Number countMuzimaPatientReports(final Integer patientId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.eq("patientId", patientId));
        criteria.add(disjunction);
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("status", "completed"));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public Number countMuzimaPatientReports(){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("status", "completed"));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
    
    @Override
    public MuzimaPatientReport getMuzimaPatientReportById(Integer id) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return (MuzimaPatientReport) criteria.uniqueResult();
    }

    @Override
    public MuzimaPatientReport getLatestPatientReportByPatientId(Integer patientId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("status", "completed"));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setMaxResults(1);
        return (MuzimaPatientReport) criteria.uniqueResult();
    }
    
    @Override
    @Transactional
    public List<MuzimaPatientReport> getMuzimaPatientReportByPatientId(Integer patientId){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.add(Restrictions.eq("status", "completed"));
        return criteria.list();
    }

    @Override
    @Transactional
    public MuzimaPatientReport getMuzimaPatientReportByUuid(String uuid){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return (MuzimaPatientReport)criteria.uniqueResult();
    }

    @Override
    @Transactional
    public MuzimaPatientReport getLatestPatientReportByPatientIdAndConfigId(Integer patientId, Integer configId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("cohortReportConfigId", configId));
        criteria.add(Restrictions.eq("patientId", patientId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.addOrder(Order.desc("dateCreated"));
        criteria.setMaxResults(1);
       return (MuzimaPatientReport)criteria.uniqueResult();
    }
    
    @Override
    public List<MuzimaPatientReport> getMuzimaPatientReportByConfigId(Integer configId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("cohortReportConfigId", configId));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return criteria.list();
    }
    
    @Override
    @Transactional
    public MuzimaPatientReport saveOrUpdateMuzimaPatientReport(final MuzimaPatientReport muzimaPatientReport){
        session().saveOrUpdate(muzimaPatientReport);
        return muzimaPatientReport;
    }

    @Override
    public void deleteMuzimaPatientReport(MuzimaPatientReport muzimaPatientReport){
        session().delete(muzimaPatientReport);
    }

    private DbSession session() {
        return sessionFactory.getCurrentSession();
    }
}
