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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.muzima.api.db.MuzimaSettingDao;
import org.openmrs.module.muzima.model.MuzimaSetting;

import java.util.List;

public class HibernateMuzimaSettingDao implements MuzimaSettingDao{
    private SessionFactory sessionFactory;
    protected Class mappedClass = MuzimaSetting.class;

    public HibernateMuzimaSettingDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<MuzimaSetting> getAll() {
        Criteria criteria = session().createCriteria(MuzimaSetting.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    /**
     * Get settings with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of settings for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MuzimaSetting> getPagedSettings(final String search, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = session().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("property", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("description", search, MatchMode.ANYWHERE));
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

    /**
     * Get the total number of settings with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of settings in the database.
     */
    @Override
    public Number countDataSource(final String search) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("property", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("description", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    public Number countSettings(){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    public MuzimaSetting getSettingById(Integer id){
        return (MuzimaSetting)session().get(mappedClass,id);
    }

    @Override
    public MuzimaSetting getSettingByUuid(String uuid){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (MuzimaSetting)criteria.uniqueResult();
    }

    @Override
    public MuzimaSetting getSettingByProperty(String property){
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("property", property));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (MuzimaSetting)criteria.uniqueResult();
    }

    @Override
    public MuzimaSetting saveOrUpdateSetting(MuzimaSetting setting){
        session().saveOrUpdate(setting);
        return setting;
    }

    @Override
    public void deleteSetting(MuzimaSetting setting){
        session().delete(setting);
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
