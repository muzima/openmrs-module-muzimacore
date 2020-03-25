package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaCohortMetadataDao;
import org.openmrs.module.muzima.model.MuzimaCohortMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HibernateMuzimaCohortMetadataDao implements MuzimaCohortMetadataDao {
    @Autowired
    protected DbSessionFactory sessionFactory;
    protected Class mappedClass = MuzimaCohortMetadata.class;

    public HibernateMuzimaCohortMetadataDao(){
        super();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<MuzimaCohortMetadata> saveOrUpdate(List<MuzimaCohortMetadata> object) {
        for(MuzimaCohortMetadata muzimaCohortMetadata: object) {
            sessionFactory.getCurrentSession().saveOrUpdate(muzimaCohortMetadata);
        }
        return object;
    }

    @Override
    public void delete(List<MuzimaCohortMetadata> object) {
        for(MuzimaCohortMetadata muzimaCohortMetadata: object) {
            sessionFactory.getCurrentSession().delete(muzimaCohortMetadata);
        }
    }

    @Override
    public List<Object> executeFilterQuery(String filterQuery) {
        String sql = filterQuery;
        SQLQuery sqlquery = session().createSQLQuery(sql);
        return sqlquery.list();
    }

    @Override
    public List<MuzimaCohortMetadata> getMuzimaCohortMetadata(List<Integer> patientIds, Integer cohortId) {
        Criteria criteria = session().createCriteria(mappedClass);
        criteria.add(Restrictions.in("patientId",patientIds));
        criteria.add(Restrictions.eq("cohortId",cohortId));
        return criteria.list();
    }

    private DbSession session() {
        return sessionFactory.getCurrentSession();
    }
}
