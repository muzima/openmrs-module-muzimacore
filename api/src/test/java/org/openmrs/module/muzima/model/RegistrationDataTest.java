package org.openmrs.module.muzima.model;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class RegistrationDataTest {

    private RegistrationData registrationData;

    @Before
    public void setUp() throws Exception {
        registrationData = new RegistrationData();
    }

    @Test
    public void creationTest() {
        Assertions.assertThat(registrationData).isNotNull();
        Assertions.assertThat(registrationData).isInstanceOf(RegistrationData.class);
    }

}