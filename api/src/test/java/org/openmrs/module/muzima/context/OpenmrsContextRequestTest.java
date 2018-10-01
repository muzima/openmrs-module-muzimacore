package org.openmrs.module.muzima.context;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.test.BaseContextSensitiveTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenmrsContextRequestTest extends BaseContextSensitiveTest {

    @Test
    public void setUp() throws Exception {

    }

    @Test
    public void shouldGet_UserContextTest() throws Exception {
        UserContext userContext = Context.getUserContext();
        assertThat(userContext).isNotNull();
    }

    @Test
    public void shouldGet_ServiceContextTest() throws Exception {
        RegistrationDataService registrationDataService = Context.getService(RegistrationDataService.class);
        assertThat(registrationDataService).isNotNull();
    }
}
