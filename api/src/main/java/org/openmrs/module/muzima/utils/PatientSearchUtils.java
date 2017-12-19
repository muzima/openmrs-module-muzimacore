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
package org.openmrs.module.muzima.utils;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;

import java.util.List;

/**
 * Patients search operations utility method that use apache commons implementation of the Levenshtein Distance algorithm.
 *
 * @see {org.apache.commons.lang.StringUtils}
 * @see {org.openmrs.Patient}
 * @see {org.openmrs.PersonName}
 */
public class PatientSearchUtils {

    private static Patient foundPatient = null;

    private PatientSearchUtils() {
    }

    /**
     * Utility method for extracting a matching org.openmrs.Patient instances from<br>
     *     <code> List<Patient> </code>
     *
     * Class has potential for a Generic implementation.
     *
     * @param patients List of type org.openmrs.Patient
     * @param unsavedPatient org.openmrs.Patient
     * @return org.openmrs.Patient - Patient instances extracted from the <code> List<Patient</code>
     */
    public static Patient findPatient(final List<Patient> patients, final Patient unsavedPatient) {

        return  patients.parallelStream()
                .filter(patient -> StringUtils.isNotBlank(patient.getPersonName().getFullName()) && StringUtils.isNotBlank(unsavedPatient.getPersonName().getFullName()))
                .filter(patient -> StringUtils.equalsIgnoreCase(patient.getGender(), unsavedPatient.getGender()))
                .filter(patient -> patient.getBirthdate() != null && unsavedPatient.getBirthdate() != null)
                .filter(patient -> DateUtils.isSameDay(patient.getBirthdate(),unsavedPatient.getBirthdate()))
                .filter(patient -> StringUtils.getLevenshteinDistance(StringUtils.lowerCase(patient.getPersonName().getGivenName()),
                                                                      StringUtils.lowerCase(unsavedPatient.getPersonName().getGivenName())) <3
                        &&
                        StringUtils.getLevenshteinDistance(StringUtils.lowerCase(patient.getPersonName().getFamilyName()),
                                                           StringUtils.lowerCase(unsavedPatient.getPersonName().getFamilyName())) < 3)
                .findFirst().get();


    }


    public static Patient findSavedPatient(Patient candidatePatient, boolean searchRegistrationData) {
        Assert.assertNotNull(searchRegistrationData);

        Patient savedPatient = null;

        if (StringUtils.isNotEmpty(candidatePatient.getUuid())) {
            savedPatient = Context.getPatientService().getPatientByUuid(candidatePatient.getUuid());
            if (savedPatient == null && searchRegistrationData) {
                String temporaryUuid = candidatePatient.getUuid();
                RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                if (registrationData != null) {
                    savedPatient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
                }
            }
        }

        if (savedPatient == null && !(candidatePatient.getPatientIdentifier() != null
                && StringUtils.isNotEmpty(candidatePatient.getPatientIdentifier().getIdentifier()))) {
            List<Patient> patients = Context.getPatientService()
                    .getPatients(candidatePatient.getPatientIdentifier().getIdentifier());
            savedPatient = PatientSearchUtils.findPatient(patients, candidatePatient);
        }
        if (savedPatient == null && candidatePatient.getPersonName() != null
                && StringUtils.isNotEmpty(candidatePatient.getPersonName().getFullName())) {
            List<Patient> patients = Context.getPatientService()
                    .getPatients(candidatePatient.getPersonName().getFullName());
            savedPatient = PatientSearchUtils.findPatient(patients, candidatePatient);
        }

        return savedPatient;
    }
}
