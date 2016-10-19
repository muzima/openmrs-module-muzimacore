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
import org.openmrs.module.muzima.api.db.MuzimaConfigDAO;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateMuzimaConfigDAO implements MuzimaConfigDAO {
    private SessionFactory factory;

    public HibernateMuzimaConfigDAO(SessionFactory factory) {
        this.factory = factory;
    }

    private Session session() {
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