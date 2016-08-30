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
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.module.muzima.api.db.NotificationDataDao;
import org.openmrs.module.muzima.model.NotificationData;

import java.util.List;

/**
 */
public class HibernateNotificationDataDao extends HibernateDataDao<NotificationData> implements NotificationDataDao {

    private final Log log = LogFactory.getLog(HibernateNotificationDataDao.class);

    /**
     * Default constructor.
     */
    protected HibernateNotificationDataDao() {
        super(NotificationData.class);
    }

    /**
     * Get all notification for this particular person.
     *
     * @param person the person for whom the notification designated to.
     * @return the list of all notification for that particular person.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NotificationData> getNotificationsByReceiver(final Person person, final String search,
                                                             final Integer pageNumber, final Integer pageSize,
                                                             final String status) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("receiver", person));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.addOrder(Order.desc("dateCreated"));
        return criteria.list();
    }

    /**
     * Get the total number of notification data with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of notification data in the database.
     */
    @Override
    public Number countNotificationsByReceiver(final Person person, final String search, final String status) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("receiver", person));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    /**
     * Get all notification from this particular person.
     *
     * @param person the person from where the notification originated from.
     * @return the list of all notification from that particular person.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NotificationData> getNotificationsBySender(final Person person, final String search,
                                                           final Integer pageNumber, final Integer pageSize,
                                                           final String status) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("sender", person));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.addOrder(Order.desc("dateCreated"));
        return criteria.list();
    }

    /**
     * Get the total number of notification data with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of notification data in the database.
     */
    @Override
    public Number countNotificationsBySender(final Person person, final String search, final String status) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("sender", person));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    public List<NotificationData> getNotificationsByRole(final Role role, final String search,
                                                         final Integer pageNumber, final Integer pageSize,
                                                         final String status) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("role", role));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.addOrder(Order.desc("dateCreated"));
        return criteria.list();
    }

    @Override
    public Number countNotificationsByRole(final Role role, final String search, final String status) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("subject", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("payload", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("role", role));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        if (StringUtils.isNotEmpty(status))
            criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}
