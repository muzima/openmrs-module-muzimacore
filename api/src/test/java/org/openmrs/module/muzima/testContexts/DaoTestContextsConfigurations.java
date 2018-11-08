package org.openmrs.module.muzima.testContexts;

import org.hibernate.SessionFactory;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.api.db.hibernate.HibernateRegistrationDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DaoTestContextsConfigurations {

    @Bean
    @Scope("prototype")
    Logger logger(){
        return LoggerFactory.getLogger("DAO Test");
    }

    @Bean
    public SessionFactory sessionFactory(){
        return new org.hibernate.cfg.Configuration()
                .configure("/muzima-hibernate.cfg.xml")
                .buildSessionFactory();
    }

    @Bean
    public DbSessionFactory dbSessionFactory(){
        return new DbSessionFactory(sessionFactory());
    }

    @Bean
    public HibernateRegistrationDataDao hibernateRegistrationDataDao(){
        return new HibernateRegistrationDataDao();
    }

    @Autowired
    public RegistrationDataDao registrationDataDao(RegistrationDataDao registrationDataDao){
        return  registrationDataDao;
    }

}
