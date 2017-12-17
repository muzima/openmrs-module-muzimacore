package org.openmrs.module.muzima.testContexts;

import org.hibernate.SessionFactory;
import org.openmrs.module.muzima.api.db.hibernate.HibernateRegistrationDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                .buildSessionFactory();
    }

//    @Bean
//    public DbSessionFactory dbSessionFactory(){
//        return new DbSessionFactory(sessionFactory());
//    }

    @Bean
    public HibernateRegistrationDataDao hibernateRegistrationDataDao(){
        System.out.println("Loading HibernateRegistrationDataDao bean in context configs.");
        return new HibernateRegistrationDataDao();
    }

}
