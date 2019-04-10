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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaConfigDAO;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateMuzimaConfigDAO implements MuzimaConfigDAO {
    private DbSessionFactory factory;

    public HibernateMuzimaConfigDAO(DbSessionFactory factory) {
        this.factory = factory;
    }

    private DbSession session() {
        return factory.getCurrentSession();
    }

    @Override
    @Transactional
    public List<MuzimaConfig> getAll() {
        Criteria criteria = session().createCriteria(MuzimaConfig.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    @Override
    @Transactional
    public MuzimaConfig findById(Integer id) {
        return (MuzimaConfig) session().get(MuzimaConfig.class, id);
    }

    @Override
    @Transactional
    public MuzimaConfig getConfigByUuid(String uuid) {
        return (MuzimaConfig) session().createQuery("from MuzimaConfig config where config.uuid = '" + uuid + "'").uniqueResult();
    }

    @Override
    @Transactional
    public MuzimaConfig save(MuzimaConfig config) {
        session().saveOrUpdate(config);
        return config;
    }

    @Override
    @Transactional
    public void delete(MuzimaConfig config) {
        session().delete(config);
    }

    @Override
    @Transactional
    public Number countConfigs(String search) {
        Criteria criteria = session().createCriteria(MuzimaConfig.class);
        criteria.add(Restrictions.eq("retired", false));

        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("description", search, MatchMode.ANYWHERE));
            if (StringUtils.isNumeric(search)) {
                disjunction.add(Restrictions.eq("id", Integer.parseInt(search)));
            }
            criteria.add(disjunction);
        }
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    @Override
    public List<MuzimaConfig> getPagedConfigs(String search, Integer pageNumber, Integer pageSize) {
        Criteria criteria = session().createCriteria(MuzimaConfig.class);
        criteria.add(Restrictions.eq("retired", false));

        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("description", search, MatchMode.ANYWHERE));
            if (StringUtils.isNumeric(search)) {
                disjunction.add(Restrictions.eq("id", Integer.parseInt(search)));
            }
            criteria.add(disjunction);
        }
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
    public List<MuzimaConfig> getConfigByName(String configName, boolean includeRetired) {
        Criteria criteria = session().createCriteria(MuzimaConfig.class);
        criteria.add(Restrictions.eq("name", configName));
        if (!includeRetired)
            criteria.add(Restrictions.eq("retired", false));
        return (List<MuzimaConfig>) criteria.list();
    }
}