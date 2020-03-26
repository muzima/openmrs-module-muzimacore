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
package org.openmrs.module.muzima.api.service;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;

import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public interface CoreService extends OpenmrsService {

    List<Obs> getObservations(final List<String> patientUuids, final List<String> conceptUuids, final Date syncDate,
                              final int startIndex, final int size) throws APIException;

    Number countObservations(final List<String> patientUuids, final List<String> conceptUuids,
                             final Date syncDate) throws APIException;

    List<Encounter> getEncounters(final List<String> patientUuids, final int maxEncounterResultsPerPatient,
                                  final Date syncDate) throws APIException;

    Number countEncounters(final List<String> patientUuids, final int maxEncounterResultsPerPatient, final Date syncDate)throws APIException;

    List<Cohort> getCohorts(final String name, final Date syncDate,
                            final int startIndex, final int size, final String defaultLocation, final String providerId) throws APIException;

    Number countCohorts(final String name, final Date syncDate, final String defaultLocation, final String providerId) throws APIException;

    List<Patient> getPatients(final String cohortUuid, final Date syncDate,
                              final int startIndex, final int size,final String defaultLocation, final String providerId) throws APIException;

    Number countPatients(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException;

    List<Patient> getPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException;

    Number countPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException;
    boolean hasCohortChangedSinceDate(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException;
}
