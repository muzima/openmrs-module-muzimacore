/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.handler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * TODO: Write brief description about the class here.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class XmlRegistrationQueueDataHandlerTest {
    /**
     * @verifies create new patient from well formed registration data
     * @see XmlRegistrationQueueDataHandler#process(org.openmrs.module.muzima.model.QueueData)
     */
    @Test
    public void process_shouldCreateNewPatientFromWellFormedRegistrationData() throws Exception {
        RegistrationDataService registrationDataService = mock(RegistrationDataService.class);
        PatientService patientService = mock(PatientService.class);
        LocationService locationService = mock(LocationService.class);

        PowerMockito.mockStatic(Context.class);
        when(Context.getService(RegistrationDataService.class)).thenReturn(registrationDataService);
        when(Context.getLocationService()).thenReturn(locationService);
        when(Context.getPatientService()).thenReturn(patientService);

        when(locationService.getLocation(anyInt())).thenReturn(new Location());

        final Patient patient1 = createPatient("Name1", "Middle1", "Family1", "identifier1");
        final Patient patient2 = createPatient("Name2", "Middle2", "Family2", "identifier2");
        final Patient patient3 = createPatient("Name3", "Middle3", "Family3", "identifier3");
        when(patientService.getPatients(anyString())).thenReturn(Arrays.asList(patient1, patient2, patient3));

        final String registrationFormData = getPayloadFromFile();
        //Object payload = JsonUtils.readAsObject(registrationFormData, "$['payload']");
        String temporaryUuid = getValueFromJSON(String.valueOf(registrationFormData), "patient.uuid");

        XmlRegistrationQueueDataHandler xmlRegistrationQueueDataHandler = new XmlRegistrationQueueDataHandler();

        final QueueData queueData = new QueueData();
        queueData.setPayload(String.valueOf(registrationFormData));
        xmlRegistrationQueueDataHandler.process(queueData);

        String identifier = getValueFromJSON(String.valueOf(registrationFormData), "patient.medical_record_number");

        verify(patientService).getPatientIdentifierType(anyInt());
        verify(patientService).getPatients(identifier);
        verify(patientService).savePatient((Patient) anyObject());

        verifyNoMoreInteractions(patientService);

        verify(registrationDataService).getRegistrationDataByTemporaryUuid(temporaryUuid);
        verify(registrationDataService).saveRegistrationData((RegistrationData) anyObject());

        verifyNoMoreInteractions(registrationDataService);
    }

    private String getPayloadFromFile() throws IOException {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("expected-well-formed-registration.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    /**
     * @verifies skip already processed registration data
     * @see XmlRegistrationQueueDataHandler#process(org.openmrs.module.muzima.model.QueueData)
     */
    @Test
    public void process_shouldSkipAlreadyProcessedRegistrationData() throws Exception {
        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }


    private Patient createPatient(final String givenName, final String middleName, final String familyName, final String identifier) {
        Patient unsavedPatient = new Patient();
        PatientIdentifier patientIdentifier = new PatientIdentifier();

        final Location location = new Location();
        location.setName("Here");
        patientIdentifier.setLocation(location);
        final PatientIdentifierType identifierType = new PatientIdentifierType();
        identifierType.setName("Patient_ID");
        patientIdentifier.setIdentifierType(identifierType);
        patientIdentifier.setIdentifier(identifier);
        unsavedPatient.addIdentifier(patientIdentifier);

        unsavedPatient.setBirthdate(new Date());
        unsavedPatient.setBirthdateEstimated(false);
        unsavedPatient.setGender("male");

        PersonName personName = new PersonName();
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);
        unsavedPatient.addName(personName);
        return unsavedPatient;
    }

    private String getValueFromJSON(final String payload, final String name) {
        return  JsonUtils.readAsString(payload, "$.payload['" + name + "']");
    }


}
