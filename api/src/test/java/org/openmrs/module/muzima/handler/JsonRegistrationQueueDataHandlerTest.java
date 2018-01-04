package org.openmrs.module.muzima.handler;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.muzima.model.QueueData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "/TestingApplicationContext.xml")
public class JsonRegistrationQueueDataHandlerTest {


    private JsonGenericRegistrationQueueDataHandler jsonRegistrationQueueDataHandler;
    @Autowired
    private QueueData queueData;

    @Before
    public void setUp() {
        ApplicationContext testApplicationContext = new ClassPathXmlApplicationContext("handlerTestingApplicationContext.xml");
        jsonRegistrationQueueDataHandler = new JsonGenericRegistrationQueueDataHandler();

    }

    @Test
    public void getDiscriminatorValueTest() {
        Assertions.assertThat(jsonRegistrationQueueDataHandler.getDiscriminator()).isNotNull();
        Assertions.assertThat(jsonRegistrationQueueDataHandler.getDiscriminator()).isNotEmpty();
        Assertions.assertThat(jsonRegistrationQueueDataHandler.getDiscriminator()).isEqualTo("json-registration");
    }


}