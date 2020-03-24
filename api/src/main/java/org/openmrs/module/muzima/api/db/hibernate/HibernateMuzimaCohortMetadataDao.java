package org.openmrs.module.muzima.api.db.hibernate;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaCohortMetadataDao;
import org.openmrs.module.muzima.model.MuzimaCohortMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HibernateMuzimaCohortMetadataDao implements MuzimaCohortMetadataDao {
    @Autowired
    protected DbSessionFactory sessionFactory;

    public HibernateMuzimaCohortMetadataDao(){
        super();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<MuzimaCohortMetadata> saveOrUpdate(List<MuzimaCohortMetadata> object) {
        sessionFactory.getCurrentSession().saveOrUpdate(object);
        return object;
    }

    @Override
    public void delete(List<MuzimaCohortMetadata> object) {
        sessionFactory.getCurrentSession().delete(object);
    }
}
