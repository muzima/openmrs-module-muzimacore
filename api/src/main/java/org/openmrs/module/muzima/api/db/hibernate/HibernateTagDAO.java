package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.module.muzima.api.db.TagDAO;
import org.openmrs.module.muzima.model.MuzimaFormTag;

import java.util.List;

public class HibernateTagDAO implements TagDAO {
    private SessionFactory factory;

    public HibernateTagDAO(SessionFactory factory) {
        this.factory = factory;
    }

    public List<MuzimaFormTag> getAll() {
        return (List<MuzimaFormTag>) factory.getCurrentSession().createCriteria(MuzimaFormTag.class).list();
    }

    public void save(MuzimaFormTag tag) {
        factory.getCurrentSession().save(tag);
    }

}
