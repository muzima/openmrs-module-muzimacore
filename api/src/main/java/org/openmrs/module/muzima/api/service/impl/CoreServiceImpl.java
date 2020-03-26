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
package org.openmrs.module.muzima.api.service.impl;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.CoreDao;
import org.openmrs.module.muzima.api.db.MuzimaCohortDao;
import org.openmrs.module.muzima.api.service.CoreService;

import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public class CoreServiceImpl extends BaseOpenmrsService implements CoreService {

    private CoreDao coreDao;
    private MuzimaCohortDao muzimaCohortDao;

    public CoreDao getCoreDao() {
        return coreDao;
    }

    public MuzimaCohortDao getMuzimaCohortDao() {
        if(muzimaCohortDao == null){
            muzimaCohortDao = Context.getRegisteredComponent("muzima.MuzimaCohortDao",MuzimaCohortDao.class);
        }
        return muzimaCohortDao;
    }

    public void setCoreDao(final CoreDao coreDao) {
        this.coreDao = coreDao;
    }

    @Override
    public List<Obs> getObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                     final Date syncDate, final int startIndex, final int size) throws APIException {
        return getCoreDao().getObservations(patientUuids, conceptUuids, syncDate, startIndex, size);
    }

    @Override
    public Number countObservations(final List<String> patientUuids, final List<String> conceptUuids,
                                    final Date syncDate) throws APIException {
        return getCoreDao().countObservations(patientUuids, conceptUuids, syncDate);
    }

    @Override
    public List<Encounter> getEncounters(final List<String> patientUuids,final int maxEncounterResultsPerPatient,
                                         final Date syncDate) throws APIException {
        return getCoreDao().getEncounters(patientUuids, maxEncounterResultsPerPatient, syncDate);
    }

    @Override
    public Number countEncounters(final List<String> patientUuids, final int maxEncounterResultsPerPatient, final Date syncDate) throws APIException {
        return getCoreDao().countEncounters(patientUuids, maxEncounterResultsPerPatient, syncDate);
    }

    @Override
    public List<Cohort> getCohorts(final String name, final Date syncDate,
                                   final int startIndex, final int size, final String defaultLocation, final String providerId) throws APIException {
        return getMuzimaCohortDao().getCohorts(name, syncDate, startIndex, size, defaultLocation, providerId);
    }

    @Override
    public Number countCohorts(final String name, final Date syncDate, final String defaultLocation, final String providerId) throws APIException {
        return getMuzimaCohortDao().countCohorts(name, syncDate, defaultLocation, providerId);
    }

    @Override
    public List<Patient> getPatients(final String cohortUuid, final Date syncDate, final int startIndex, final int size, final String defaultLocation, final String providerId) throws APIException {
        return getMuzimaCohortDao().getPatients(cohortUuid, syncDate, startIndex, size, defaultLocation, providerId);
    }

    @Override
    public Number countPatients(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException {
        return getMuzimaCohortDao().countPatients(cohortUuid, syncDate, defaultLocation, providerId);
    }

    @Override
    public List<Patient> getPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException {
        return getMuzimaCohortDao().getPatientsRemovedFromCohort(cohortUuid, syncDate, defaultLocation, providerId);
    }

    public  Number countPatientsRemovedFromCohort(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException{
        return getMuzimaCohortDao().countPatients(cohortUuid, syncDate, defaultLocation, providerId);
    }

    public  boolean hasCohortChangedSinceDate(final String cohortUuid, final Date syncDate, final String defaultLocation, final String providerId) throws APIException{
        return getMuzimaCohortDao().hasCohortChangedSinceDate(cohortUuid, syncDate, defaultLocation, providerId);
    }
}
