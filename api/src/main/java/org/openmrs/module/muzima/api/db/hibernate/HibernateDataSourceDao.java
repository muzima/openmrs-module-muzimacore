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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.muzima.api.db.DataSourceDao;
import org.openmrs.module.muzima.model.DataSource;

import java.util.List;

/**
 */
public class HibernateDataSourceDao extends HibernateSingleClassDao<DataSource> implements DataSourceDao {

    public HibernateDataSourceDao() {
        super(DataSource.class);
    }

    /**
     * @return the sessionFactory
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Return the data source with the given uuid.
     *
     * @param uuid the data source uuid.
     * @return the data source with the matching uuid.
     * @should return data with matching uuid.
     * @should return null when no data with matching uuid.
     */
    public DataSource getDataSourceByUuid(final String uuid) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        return (DataSource) criteria.uniqueResult();
    }

    /**
     * Get data source with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of data source for the page.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DataSource> getPagedDataSources(final String search, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
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
     * Get the total number of data source with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of data source in the database.
     */
    @Override
    public Number countDataSource(final String search) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("description", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("retired", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}
