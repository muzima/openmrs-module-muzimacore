package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.NotificationTokenDataDao;
import org.openmrs.module.muzima.model.NotificationToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HibernateNotificationTokenDataDao implements NotificationTokenDataDao {
    @Autowired
    protected DbSessionFactory sessionFactory;
    protected Class mappedClass = NotificationToken.class;

    public HibernateNotificationTokenDataDao(){
        super();
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Test
    public void Teste(){

    }
    @Override
    public NotificationToken getNotificationTokenById(String id) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("id",id));
        return (NotificationToken) criteria.uniqueResult();
    }

    @Override
    public NotificationToken saveNotificationToken(NotificationToken notificationToken) {
        sessionFactory.getCurrentSession().save(notificationToken);
        return notificationToken;
    }

    @Override
    public List<NotificationToken> getNotificationByUserId(User user) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("userId",user.getUserId()));
        List<NotificationToken> notificationTokens = criteria.list();
        return notificationTokens;
    }
}
