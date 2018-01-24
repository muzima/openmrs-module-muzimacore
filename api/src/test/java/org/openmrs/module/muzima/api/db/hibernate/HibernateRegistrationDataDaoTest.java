package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.testContexts.DaoTestContextsConfigurations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class HibernateRegistrationDataDaoTest {


    private HibernateRegistrationDataDao hibernateRegistrationDao;

    private SessionFactory dbSessionFactory;

    @InjectMocks
    private RegistrationDataDao registrationDataDao;

    @Before
    public void setUp() throws Exception {
        ApplicationContext testApplicationContext =
                new AnnotationConfigApplicationContext(DaoTestContextsConfigurations.class);
        this.hibernateRegistrationDao = testApplicationContext.getBean(HibernateRegistrationDataDao.class);
        this.dbSessionFactory = testApplicationContext.getBean(SessionFactory.class);
    }

    @Test
    public void creationTest() throws Exception {
      assertThat(hibernateRegistrationDao).isNotNull();
      assertThat(hibernateRegistrationDao).isInstanceOf(HibernateRegistrationDataDao.class);
      assertThat(dbSessionFactory).isNotNull();
      assertThat(dbSessionFactory).isInstanceOf(DbSessionFactory.class);
    }

    @Test
    public void getSessionFactoryTest() throws Exception {

    }

    @Test
    public void getRegistrationDataByIdTest() throws Exception {
    }

    @Test
    public void getRegistrationDataByUuidTest() throws Exception {
    }

    @Test
    public void getRegistrationDataTest() throws Exception {
    }

    @Test
    public void saveRegistrationData() throws Exception {
    }

    @Test
    public void deleteRegistrationDataTest() throws Exception {
    }

    @Test
    public void getRegistrationData1Test() throws Exception {
    }

    @Test
    public void countRegistrationDataTest() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        dbSessionFactory.getCurrentSession().close();
    }

}
