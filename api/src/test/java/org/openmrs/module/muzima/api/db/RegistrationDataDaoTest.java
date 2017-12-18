package org.openmrs.module.muzima.api.db;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.muzima.api.db.hibernate.HibernateRegistrationDataDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.Timed;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Bean is an interface that should be easily mocked or autowired as needed.
 * Goal of test is determine an instantiation mechanism for <code>{org.openmrs.module.muzima.db.RegistrationDataDao}</code> interface
 * by its implementor classes or interfaces apart from ... implements RegistrationDataDao.
 *
 * @see {org.openmrs.module.muzima.db.RegistrationDataDao.}
 */
public class RegistrationDataDaoTest {

    @Mock
    private RegistrationDataDao registrationDataDao;

    @Before
    @Timed(millis = 5000)
    public void setUp() throws Exception {
        System.out.print("Loading app ctx");
        ApplicationContext testApplicationContext =
                new ClassPathXmlApplicationContext("dao-test-context.xml");
    }

    @Test
    public void creationTest() throws  Exception{
        assertThat(registrationDataDao).isNotNull();
        assertThat(registrationDataDao).isInstanceOf(HibernateRegistrationDataDao.class);
    }
}
