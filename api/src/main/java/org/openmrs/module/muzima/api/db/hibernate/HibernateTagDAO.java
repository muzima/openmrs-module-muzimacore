package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.openmrs.module.muzima.model.MuzimaFormTag;
import org.openmrs.module.muzima.api.db.TagDAO;

import java.util.List;

public class HibernateTagDAO implements TagDAO {
    private SessionFactory factory;

    public HibernateTagDAO(SessionFactory factory) {
        this.factory = factory;
    }

    public List<MuzimaFormTag> getAll() {
        return (List<MuzimaFormTag>) session().createCriteria(MuzimaFormTag.class).list();
    }

    public void save(MuzimaFormTag tag) {
        session().save(tag);
    }

    private Session session() {
        return factory.getCurrentSession();
    }

}
