package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateSessionFactoryBean;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.test.BaseContextMockTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.annotation.Timed;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests methods on the {@link HibernateRegistrationDataDao} class
 */
public class HibernateRegistrationDataDaoTest extends BaseContextMockTest{


    private HibernateRegistrationDataDao hibernateRegistrationDataDao;

    private HibernateSessionFactoryBean hibernateSessionFactoryBean;

    private DbSessionFactory dbSessionFactory;

    private RegistrationData registrationData;

    private RegistrationDataDao registrationDataDao;

    @Before
    @Timed(millis = 5000)
    public void setUp() throws Exception {
        System.out.print("Loading app ctx");
        ApplicationContext testApplicationContext =
                new ClassPathXmlApplicationContext("dao-test-context.xml");
        this.hibernateSessionFactoryBean = testApplicationContext.getBean(HibernateSessionFactoryBean.class);
        this.dbSessionFactory = testApplicationContext.getBean(DbSessionFactory.class);
        hibernateRegistrationDataDao = new HibernateRegistrationDataDao();
        hibernateRegistrationDataDao.setSessionFactory(dbSessionFactory);
        this.registrationData = new RegistrationData();
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public void creationTest() throws Exception {
        assertThat(this.hibernateSessionFactoryBean).isNotNull();
        assertThat(this.dbSessionFactory).isNotNull();
        assertThat(this.hibernateRegistrationDataDao).isNotNull();
        assertThat(this.hibernateRegistrationDataDao.getSessionFactory()).isNotNull();
        assertThat(this.registrationData).isNotNull();
    }

    @Test
    public void getSessionFactoryTest() throws Exception {
        assertThat(this.hibernateRegistrationDataDao.getSessionFactory()).isNotNull();
        assertThat(this.hibernateRegistrationDataDao.getSessionFactory()).isInstanceOf(DbSessionFactory.class);
    }

    @Rollback
    @Test
    public void getRegistrationDataByIdTest() throws Exception {
        Transaction transaction = null; //TODO refactor to use aop driven declarative tx management.

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-4lel-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074119d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        hibernateRegistrationDataDao.saveRegistrationData(registrationData);

        RegistrationData flushedRegistrationData = hibernateRegistrationDataDao
                .getRegistrationDataById(1);

        assertThat(flushedRegistrationData).isNotNull();
        assertThat(flushedRegistrationData.getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(flushedRegistrationData.getId()).isEqualTo(1);
        assertThat(flushedRegistrationData.getDateCreated()).isInstanceOf(Date.class);
        assertThat(flushedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(flushedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        transaction.rollback();
        dbSessionFactory.getCurrentSession().close();
    }

    @Rollback
    @Test
    public void getRegistrationDataByUuidTest() throws Exception {

        Transaction transaction = null;
        //TODO refactor to use aop driven declarative tx management.
        /**
         * Clas throws aa org.hibernate.StaleStateException : yet to be fixed.
         */

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        this.hibernateRegistrationDataDao.saveRegistrationData(this.registrationData);

        RegistrationData flushedRegistrationData = this.hibernateRegistrationDataDao
                .getRegistrationDataByUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+flushedRegistrationData.toString());

        assertThat(flushedRegistrationData).isNotNull();
        assertThat(flushedRegistrationData.getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(flushedRegistrationData.getId()).isEqualTo(1);
        assertThat(flushedRegistrationData.getDateCreated()).isInstanceOf(Date.class);
        assertThat(flushedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(flushedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        transaction.rollback();
        dbSessionFactory.getCurrentSession().close();
    }

    @Rollback
    @Test
    public void getRegistrationDataTest() throws Exception {
        Transaction transaction = null;
        //TODO refactor to use aop driven declarative tx management.
        /**
         * Class throws an org.hibernate.StaleStateException : yet to be fixed.
         */

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();
        dbSessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
        this.hibernateRegistrationDataDao.saveRegistrationData(registrationData);

        List<RegistrationData> flushedRegistrationData = hibernateRegistrationDataDao
                .getRegistrationData("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b","074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+flushedRegistrationData.toString());

        assertThat(flushedRegistrationData).isNotNull();
        assertThat(flushedRegistrationData.size()).isGreaterThan(0);
        assertThat(flushedRegistrationData.size()).isEqualTo(1);
        assertThat(flushedRegistrationData).isInstanceOf(List.class);
        assertThat(flushedRegistrationData.iterator().next().getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(flushedRegistrationData.iterator().next().getId()).isEqualTo(1);
        assertThat(flushedRegistrationData.iterator().next().getDateCreated()).isInstanceOf(Date.class);
        assertThat(flushedRegistrationData.iterator().next().getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(flushedRegistrationData.iterator().next().getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        dbSessionFactory.getCurrentSession().close();
    }

    @Rollback
    @Test
    public void saveRegistrationDataTest() throws Exception {

        Transaction transaction = null; //TODO refactor to use aop driven declarative tx management.

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-4lel-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074119d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();
        dbSessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
        RegistrationData savedRegistrationDate = this.hibernateRegistrationDataDao
                .saveRegistrationData(this.registrationData);
        System.out.println("flushedRegistrationDate ["+savedRegistrationDate.toString());

        assertThat(savedRegistrationDate).isNotNull();
        assertThat(savedRegistrationDate.getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(savedRegistrationDate.getId()).isEqualTo(1);
        assertThat(savedRegistrationDate.getDateCreated()).isInstanceOf(Date.class);
        assertThat(savedRegistrationDate.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(savedRegistrationDate.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        dbSessionFactory.getCurrentSession().close();
    }

    @Rollback
    @Test
    public void deleteRegistrationDataTest() throws Exception {
        Transaction transaction = null;
        //TODO refactor to use aop driven declarative tx management.
        /**
         * Class throws aa org.hibernate.StaleStateException : yet to be fixed.
         */

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        this.hibernateRegistrationDataDao.saveRegistrationData(this.registrationData);

        this.hibernateRegistrationDataDao.deleteRegistrationData(this.registrationData);

        RegistrationData deletedFlushedRegistrationData = this.hibernateRegistrationDataDao
                .getRegistrationDataByUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+deletedFlushedRegistrationData);

        assertThat(deletedFlushedRegistrationData).isNull();

        dbSessionFactory.getCurrentSession().close();
    }

    @Rollback
    @Test
    public void countRegistrationDataTest() throws Exception {
        Transaction transaction = null;
        //TODO refactor to use aop driven declarative tx management.
        /**
         * Class throws aa org.hibernate.StaleStateException : yet to be fixed.
         */

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();
        DbSession session = dbSessionFactory.getCurrentSession();//.setFlushMode(FlushMode.COMMIT);
        session.setFlushMode(FlushMode.MANUAL);

        hibernateRegistrationDataDao.saveRegistrationData(registrationData);

        Long rowCount = (Long)hibernateRegistrationDataDao.countRegistrationData();
        assertThat(rowCount).isEqualTo(1);
        assertThat(rowCount).isGreaterThan(0);
        assertThat(this.hibernateRegistrationDataDao.countRegistrationData().intValue()).isLessThan(2);

        session.flush();
        dbSessionFactory.getCurrentSession().close();
    }

    @After
    public void tearDown() throws Exception {
        if(dbSessionFactory.getCurrentSession().isOpen()){
            System.out.println("Clsssing hibernate session.");
            dbSessionFactory.getCurrentSession().close();
        }
    }

}
