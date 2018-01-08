package org.openmrs.module.muzima.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class RegistrationControllerTest extends BaseContextMockTest{

    private Log logger = LogFactory.getLog(RegistrationControllerTest.class);
    private static final String TAG = "RegistrationControllerTest";

    @Mock
    private RegistrationDataService registrationDataService;
    private RegistrationController registrationController;
    private PatientService patientService;
    private SimpleDateFormat dateFormat;
    @Before
    public void beforeClass() throws Exception {
        logger.info("Attempting to bootstrap test dependency set up");
        registrationController = new RegistrationController();
        registrationDataService = mock(RegistrationDataService.class);
        patientService = mock(PatientService.class);
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        mockStatic(Context.class);
        when(Context.getService(RegistrationDataService.class)).thenReturn(registrationDataService);
        when(Context.getPatientService()).thenReturn(patientService);
        when(Context.getDateFormat()).thenReturn(dateFormat);
        Assert.assertNotNull(registrationDataService);
        Assert.assertNotNull(registrationController);

    }

    @Test
    public void getRegistrationTest() throws Exception {

        RegistrationData registrationData = new RegistrationData();
        registrationData.setAssignedUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f");
        registrationData.setTemporaryUuid("8d871d18-c3bb-11de-8d13-0010c6dffd0f");
        registrationData.setId(1);
        registrationData.setDateCreated( new Date());
        registrationData.setUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f");

        Patient patient = new Patient();
        Set<PersonName> personNames = new HashSet<>();
        PersonName fullName = new PersonName();
        fullName.setMiddleName("John");
        fullName.setGivenName("Doe");
        fullName.setFamilyName("Jane");
        personNames.add(fullName);
        patient.setNames(personNames);
        patient.setGender("male");
        patient.setBirthdate( new Date());
        Set<PatientIdentifier> identifiers = new HashSet<>();
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setId(1);
        patientIdentifierType.setName("KENYA NATIONAL ID");
        patientIdentifier.setId(1);
        patientIdentifier.setPreferred(true);
        patientIdentifier.setIdentifier("32332271");
        patient.addIdentifier(patientIdentifier);

        when(registrationDataService.getRegistrationDataByUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f"))
                .thenReturn(registrationData);
        when(patientService.getPatientByUuid("8d871d18-c2cc-11de-8d13-0010c6dffd0f")).thenReturn(patient);
        Map<String,Object> registrationControllerHttpResponseBody = registrationController.getRegistration("8d871d18-c2cc-11de-8d13-0010c6dffd0f");
        logger.info(registrationControllerHttpResponseBody);
        //verify entries
        assertThat(registrationControllerHttpResponseBody).isNotNull();
        assertThat(registrationControllerHttpResponseBody.size()).isGreaterThan(0);
        assertThat(registrationControllerHttpResponseBody.entrySet().size()).isGreaterThan(0);
        assertThat(registrationControllerHttpResponseBody.keySet().size()).isEqualTo(5);
        assertThat(registrationControllerHttpResponseBody.get("temporaryUuid")).isEqualTo(registrationData.getTemporaryUuid());
        assertThat(registrationControllerHttpResponseBody.get("patient")).isNotNull();
        assertThat(registrationControllerHttpResponseBody.get("uuid")).isEqualTo(registrationData.getUuid());
        assertThat(registrationControllerHttpResponseBody.get("assignedUuid")).isEqualTo(registrationData.getAssignedUuid());

    }

}