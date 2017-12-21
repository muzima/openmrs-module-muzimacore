package org.openmrs.module.muzima.api.db;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.hibernate.HibernateRegistrationDataDao;
import org.openmrs.module.muzima.model.RegistrationData;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationDataDaoTest {
    private RegistrationDataDao registrationDataDao;

    private HibernateRegistrationDataDao hibernateRegistrationDataDao;

    private DbSessionFactory dbSessionFactory;

    @Before
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public void setUp(){
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("dao-test-context.xml");

        this.hibernateRegistrationDataDao = applicationContext.getBean(HibernateRegistrationDataDao.class);
        this.registrationDataDao = this.hibernateRegistrationDataDao;
        this.dbSessionFactory = applicationContext.getBean(DbSessionFactory.class);

    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Repeat(1)
    public void creationTest() throws Exception {
        assertThat(registrationDataDao).isNotNull();
        assertThat(registrationDataDao).isInstanceOf(RegistrationDataDao.class);
    }

    @Test
    public void getRegistrationDataById() throws Exception {
    }

    @Transactional
    @Rollback(true)
    @Test
    public void getRegistrationDataByUuidTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();

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

        this.registrationDataDao.saveRegistrationData(registrationData);

        RegistrationData flushedregistrationData = this.hibernateRegistrationDataDao
                .getRegistrationDataByUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+flushedregistrationData.toString());

        assertThat(flushedregistrationData).isNotNull();
        assertThat(flushedregistrationData.getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(flushedregistrationData.getId()).isEqualTo(1);
        assertThat(flushedregistrationData.getDateCreated()).isInstanceOf(Date.class);
        assertThat(flushedregistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(flushedregistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        transaction.rollback();
        dbSessionFactory.getCurrentSession().close();
    }

    @Transactional
    @Rollback(true)
    @Test
    public void getRegistrationDataTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();

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

        this.registrationDataDao.saveRegistrationData(registrationData);

        List<RegistrationData> flushedregistrationData = this.hibernateRegistrationDataDao
                .getRegistrationData("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b","074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+flushedregistrationData.toString());

        assertThat(flushedregistrationData).isNotNull();
        assertThat(flushedregistrationData.size()).isGreaterThan(0);
        assertThat(flushedregistrationData.size()).isEqualTo(1);
        assertThat(flushedregistrationData).isInstanceOf(List.class);
        assertThat(flushedregistrationData.iterator().next().getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(flushedregistrationData.iterator().next().getId()).isEqualTo(1);
        assertThat(flushedregistrationData.iterator().next().getDateCreated()).isInstanceOf(Date.class);
        assertThat(flushedregistrationData.iterator().next().getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(flushedregistrationData.iterator().next().getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        transaction.rollback();
        dbSessionFactory.getCurrentSession().close();
    }

    @Test
    public void saveRegistrationDataTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();


        Transaction transaction = null; //TODO refactor to use aop driven declarative tx management.

        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setDateCreated(new Date());
        registrationData.setTemporaryUuid("074108d9-4lel-4b1c-8f58-8ea34c3bff8b");
        registrationData.setAssignedUuid("074119d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setVoided(Boolean.FALSE);

        transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationDate = this.hibernateRegistrationDataDao
                .saveRegistrationData(registrationData);
        System.out.println("flushedRegistrationDate ["+savedRegistrationDate.toString());

        assertThat(savedRegistrationDate).isNotNull();
        assertThat(savedRegistrationDate.getUuid()).isEqualTo("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(savedRegistrationDate.getId()).isEqualTo(1);
        assertThat(savedRegistrationDate.getDateCreated()).isInstanceOf(Date.class);
        assertThat(savedRegistrationDate.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(savedRegistrationDate.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());

        dbSessionFactory.getCurrentSession().close();
    }

    @Transactional
    @Rollback
    @Test
    public void deleteRegistrationDataTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();

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

        this.registrationDataDao.saveRegistrationData(registrationData);

        this.registrationDataDao.deleteRegistrationData(registrationData);

        RegistrationData deletedFlushedregistrationData = this.hibernateRegistrationDataDao
                .getRegistrationDataByUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        System.out.println("flushedRegistrationDate ["+deletedFlushedregistrationData);

        assertThat(deletedFlushedregistrationData).isNull();

        dbSessionFactory.getCurrentSession().close();
    }


    @Test
    public void methodTwo_getRegistrationDataTest() throws Exception {
    }

    @Transactional
    @Test
    public void countRegistrationDataTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();


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

        this.registrationDataDao.saveRegistrationData(registrationData);

        int rowCount = (Integer)this.registrationDataDao.countRegistrationData();
        assertThat(rowCount).isEqualTo(1);
        assertThat(this.registrationDataDao.countRegistrationData().intValue()).isLessThan(2);

        dbSessionFactory.getCurrentSession().close();
    }

}