package org.openmrs.module.muzima.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(JUnit4.class)

@ContextConfiguration(locations ="/TestingApplicationContext.xml")
public class RegistrationControllerTest {

    private Log logger = LogFactory.getLog(RegistrationControllerTest.class);
    private static final String TAG = "RegistrationControllerTest";

    @Mock
    private RegistrationDataService registrationDataService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        logger.info("Attempting to bootstrap test dependency set up");
        MockitoAnnotations.initMocks(this);

        Assert.assertNotNull(registrationDataService);
    }

    @Test
    public void getRegistrationTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f");
        registrationData.setTemporaryUuid("8d871d18-c3bb-11de-8d13-0010c6dffd0f");
        registrationData.setId(1);
        registrationData.setDateCreated( new Date());
        registrationData.setUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f");

        when(registrationDataService.getRegistrationDataByTemporaryUuid(java.util.UUID.randomUUID().toString()))
                .thenReturn(registrationData);

        mockMvc.perform(get("/module/muzimacore/registration.json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

}