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
package org.openmrs.module.muzima.api.db;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public interface CoreDao {

    @Transactional(readOnly = true)
    List<Obs> getObservations(final List<String> patientUuids, final List<String> conceptUuids,
                              final Date syncDate, final int startIndex, final int size) throws DAOException;

    @Transactional(readOnly = true)
    Number countObservations(final List<String> patientUuids, final List<String> conceptUuids,
                             final Date syncDate) throws DAOException;

    @Transactional(readOnly = true)
    List<Encounter> getEncounters(final List<String> patientUuids, final Date syncDate,
                                  final int startIndex, final int size) throws DAOException;

    @Transactional(readOnly = true)
    Number countEncounters(final List<String> patientUuids, final Date syncDate) throws DAOException;

    @Transactional(readOnly = true)
    List<Cohort> getCohorts(final String name, final Date syncDate,
                            int startIndex, int size) throws DAOException;

    @Transactional(readOnly = true)
    Number countCohorts(final String name, final Date syncDate) throws DAOException;

    @Transactional(readOnly = true)
    List<Patient> getPatients(final String cohortUuid, final Date syncDate,
                              final int startIndex, final int size) throws DAOException;

    @Transactional(readOnly = true)
    Number countPatients(final String cohortUuid, final Date syncDate) throws DAOException;

    @Transactional(readOnly = true)
    Number countCohortMemberships(String cohortUuid, Date syncDate) throws DAOException;
}
