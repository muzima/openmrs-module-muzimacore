package org.openmrs.module.muzima.api.db.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
@ContextConfiguration
public class HibernateRegistrationDataDaoTest {



    @Before
    public void setUp() throws Exception {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext("org.openmrs.module.muzima.api");
        applicationContext.getBean(DbSessionFactory.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSessionFactory() throws Exception {
    }

    @Test
    public void getRegistrationDataById() throws Exception {
    }

    @Test
    public void getRegistrationDataByUuid() throws Exception {
    }

    @Test
    public void getRegistrationData() throws Exception {
    }

    @Test
    public void saveRegistrationData() throws Exception {
    }

    @Test
    public void deleteRegistrationData() throws Exception {
    }

    @Test
    public void getRegistrationData1() throws Exception {
    }

    @Test
    public void countRegistrationData() throws Exception {
    }

}