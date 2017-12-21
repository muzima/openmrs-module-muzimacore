package org.openmrs.module.muzima.context;

import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenmrsContextRequestTest {

    @Test
    public void setUp() throws Exception {
//        ApplicationContext applicationContext =
//                new ClassPathXmlApplicationContext("service-test-context.xml");

        /**
         * Programmatically create a user context
         *
         */
        Context.startup("jdbc:mysql://localhost:3306/openmrs?autoReconnect=true", "root", "", new Properties());
        try {
            Context.openSession();
            Context.authenticate("admin", "test");

        } finally {
            Context.closeSession();
        }
    }

    @Test
    public void shouldget_usercontextTest() throws Exception {
        UserContext userContext = Context.getUserContext();
        assertThat(userContext).isNotNull();
    }

    @Test
    public void shouldget_servicecontextTest() throws Exception {
        PatientService patientService = Context.getService(PatientService.class);
        assertThat(patientService).isNotNull();
    }
}
