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
package org.openmrs.module.muzima.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;

import java.util.List;

/**
 */
public class PatientSearchUtils {
    private PatientSearchUtils(){}
    public static Patient findSimilarPatientByNameAndGender(final List<Patient> patients, final Patient unsavedPatient) {
        for (Patient patient : patients) {
            // match it using the person name and gender.
            PersonName savedPersonName = patient.getPersonName();
            PersonName unsavedPersonName = unsavedPatient.getPersonName();
            if (StringUtils.isNotBlank(savedPersonName.getFullName())
                    && StringUtils.isNotBlank(unsavedPersonName.getFullName())) {
                if (StringUtils.equalsIgnoreCase(patient.getGender(), unsavedPatient.getGender())) {
                    if (patient.getBirthdate() != null && unsavedPatient.getBirthdate() != null
                            && DateUtils.isSameDay(patient.getBirthdate(), unsavedPatient.getBirthdate())) {
                        String savedGivenName = savedPersonName.getGivenName();
                        String unsavedGivenName = unsavedPersonName.getGivenName();
                        int givenNameEditDistance = StringUtils.getLevenshteinDistance(
                                StringUtils.lowerCase(savedGivenName),
                                StringUtils.lowerCase(unsavedGivenName));
                        String savedFamilyName = savedPersonName.getFamilyName();
                        String unsavedFamilyName = unsavedPersonName.getFamilyName();
                        int familyNameEditDistance = StringUtils.getLevenshteinDistance(
                                StringUtils.lowerCase(savedFamilyName),
                                StringUtils.lowerCase(unsavedFamilyName));
                        if (givenNameEditDistance < 3 && familyNameEditDistance < 3) {
                            return patient;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Patient findSavedPatient(Patient candidatePatient, boolean searchRegistrationData){
        Patient savedPatient = null;
        if (StringUtils.isNotEmpty(candidatePatient.getUuid())) {
            savedPatient = Context.getPatientService().getPatientByUuid(candidatePatient.getUuid());
            if (savedPatient == null && searchRegistrationData == true) {
                String temporaryUuid = candidatePatient.getUuid();
                RegistrationDataService dataService = Context.getService(RegistrationDataService.class);
                RegistrationData registrationData = dataService.getRegistrationDataByTemporaryUuid(temporaryUuid);
                if(registrationData!=null) {
                    savedPatient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
                }
            }
        }

        if (savedPatient == null && (candidatePatient.getPatientIdentifier() != null
                && StringUtils.isNotEmpty(candidatePatient.getPatientIdentifier().getIdentifier()) )) {
            List<Patient> patients = Context.getPatientService()
                    .getPatients(candidatePatient.getPatientIdentifier().getIdentifier());
            savedPatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, candidatePatient);
        }

        if(savedPatient == null && candidatePatient.getPersonName() != null
                && StringUtils.isNotEmpty(candidatePatient.getPersonName().getFullName())){
            List<Patient> patients = Context.getPatientService()
                    .getPatients(candidatePatient.getPersonName().getFullName());
            savedPatient = PatientSearchUtils.findSimilarPatientByNameAndGender(patients, candidatePatient);
        }

        return savedPatient;
    }
}
