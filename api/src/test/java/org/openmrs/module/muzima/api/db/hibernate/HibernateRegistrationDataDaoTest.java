package org.openmrs.module.muzima.api.db.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openmrs.api.db.hibernate.HibernateSessionFactoryBean;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.Timed;

import static org.assertj.core.api.Assertions.assertThat;
public class HibernateRegistrationDataDaoTest {


    private HibernateRegistrationDataDao hibernateRegistrationDataDao;

    private HibernateSessionFactoryBean hibernateSessionFactoryBean;

    @InjectMocks
    private RegistrationDataDao registrationDataDao;

    @Before
    @Timed(millis = 5000)
    public void setUp() throws Exception {
        System.out.print("Loading app ctx");
        ApplicationContext testApplicationContext =
                new ClassPathXmlApplicationContext("dao-test-context.xml");
        this.hibernateSessionFactoryBean = testApplicationContext.getBean(HibernateSessionFactoryBean.class);
    }

    @Test
    public void creationTest() throws Exception {
        assertThat(this.hibernateSessionFactoryBean).isNotNull();
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
    //    dbSessionFactory.getCurrentSession().close();
    }

}
