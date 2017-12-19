/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.api.service.impl;

import junit.framework.Assert;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.api.db.hibernate.HibernateRegistrationDataDao;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * Tests Service layer tests case for  {@link RegistrationDataService}.
 */
public class RegistrationDataServiceTest {

    private RegistrationDataServiceImpl service;

    private HibernateRegistrationDataDao hibernateRegistrationDataDao;

    private DbSessionFactory dbSessionFactory;

    @InjectMocks
    private RegistrationDataDao registrationDataDao;

    @Test
    public void shouldSetupContext() {
        assertNotNull(service);

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("a");
        registrationData.setTemporaryUuid("b");
        RegistrationData savedRegistrationData = service.saveRegistrationData(registrationData);

        Assert.assertNotNull(savedRegistrationData.getId());
    }

    @Before
    public void setUp() throws Exception {
        System.out.print("Loading service layer test application context ...");
        ApplicationContext testApplicationContext =
                new ClassPathXmlApplicationContext("service-test-context.xml");
        this.service = testApplicationContext.getBean(RegistrationDataServiceImpl.class);
        this.dbSessionFactory = testApplicationContext.getBean(DbSessionFactory.class);
        this.hibernateRegistrationDataDao = new HibernateRegistrationDataDao();
    }

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @Repeat(1)
    public void creationTest() throws Exception {
        assertThat(this.service).isNotNull();
        assertThat(this.service.getDao()).isNotNull();
        assertThat(this.registrationDataDao).isNotNull();
        assertThat(this.dbSessionFactory).isNotNull();
        assertThat(this.hibernateRegistrationDataDao).isNotNull();
    }

    @Transactional
    @Rollback
    @Test
    public void getRegistrationDataByIdTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("a");
        registrationData.setTemporaryUuid("b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataById(1);
        assertThat(obtainedRegistrationData).isNotNull();
        assertThat(obtainedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(obtainedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());

    }

    @Transactional
    @Rollback
    @Test
    public void getRegistrationDataByUuidTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("a");
        registrationData.setTemporaryUuid("b");
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataByUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(obtainedRegistrationData).isNotNull();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());

    }

    @Transactional
    @Rollback
    @Test
    public void getRegistrationDataByTemporaryUuidTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataByTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(obtainedRegistrationData).isNotNull();
        assertThat(obtainedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isNotEmpty();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
    }

    @Test
    public void getRegistrationDataByAssignedUuidTest() throws Exception {
        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataByTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(obtainedRegistrationData).isNotNull();
        assertThat(obtainedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isNotEmpty();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isNotEmpty();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isInstanceOf(String.class);
        assertThat(obtainedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
    }

    @Transactional
    @Rollback
    @Test
    public void saveRegistrationDataTest() throws Exception {
        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataByTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(obtainedRegistrationData).isNotNull();
        assertThat(obtainedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isNotEmpty();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isNotEmpty();
        assertThat(obtainedRegistrationData.getAssignedUuid()).isInstanceOf(String.class);
        assertThat(obtainedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
    }

    @Transactional
    @Rollback
    @Test
    public void deleteRegistrationDataTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        assertThat(savedRegistrationData).isNotNull();
        assertThat(savedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(savedRegistrationData.getTemporaryUuid()).isNotEmpty();
        assertThat(savedRegistrationData.getAssignedUuid()).isNotEmpty();
        assertThat(savedRegistrationData.getAssignedUuid()).isInstanceOf(String.class);
        assertThat(savedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(savedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());

        this.service.deleteRegistrationData(registrationData);

        RegistrationData obtainedRegistrationData = this.service.getRegistrationDataByTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        assertThat(obtainedRegistrationData).isNull();

    }

    @Transactional
    @Rollback
    @Test
    public void getRegistrationDataTest() throws Exception {
        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);

        Transaction transaction = dbSessionFactory.getCurrentSession().beginTransaction();

        RegistrationData savedRegistrationData = this.service.saveRegistrationData(registrationData);

        assertThat(savedRegistrationData).isNotNull();
        assertThat(savedRegistrationData.getId()).isEqualTo(registrationData.getId());
        assertThat(savedRegistrationData.getTemporaryUuid()).isNotEmpty();
        assertThat(savedRegistrationData.getAssignedUuid()).isNotEmpty();
        assertThat(savedRegistrationData.getAssignedUuid()).isInstanceOf(String.class);
        assertThat(savedRegistrationData.getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(savedRegistrationData.getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());

        List<RegistrationData> obtainedRegistrationData = this.service.getRegistrationData(1,1);
        assertThat(obtainedRegistrationData).isNull();
        assertThat(obtainedRegistrationData.iterator().next()).isInstanceOf(RegistrationData.class);
        assertThat(obtainedRegistrationData.iterator().next().getAssignedUuid()).isEqualTo(registrationData.getAssignedUuid());
        assertThat(obtainedRegistrationData.iterator().next().getTemporaryUuid()).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(obtainedRegistrationData.iterator().next().getId()).isEqualTo(registrationData.getId());

    }

    @Transactional
    @Rollback
    @Test
    public void countRegistrationDataTest() throws Exception {
        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setTemporaryUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");
        registrationData.setId(1);
        registrationData.setUuid("074108d9-3fbf-4b1c-8f58-8ea34c3bff8b");

        Mockito.when(this.hibernateRegistrationDataDao.saveOrUpdate(registrationData))
                .thenReturn(registrationData);
        Integer registrationsCount = this.service.countRegistrationData().intValue();
        assertThat(registrationsCount).isNotNull();
        assertThat(registrationsCount).isGreaterThan(0);
        assertThat(registrationsCount).isEqualTo(1);
        assertThat(registrationsCount).isBetween(0,1);
    }

}
