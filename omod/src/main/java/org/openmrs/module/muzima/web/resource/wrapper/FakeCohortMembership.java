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
package org.openmrs.module.muzima.web.resource.wrapper;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Patient;

import java.util.Date;

/**
 * TODO: Write brief description about the class here.
 */
public class FakeCohortMembership extends CohortMembership {

    private Cohort cohort;

    private Patient patient;
    /**
     * Copier constructor to set fields
     *
     * @param patientId
     * @param startDate
     */
    public FakeCohortMembership(final Integer patientId, final Date startDate) {
    	super(patientId, startDate);
    }

    /**
     * Default constructor
     */
    public FakeCohortMembership() {
    }

    @Override
    public Cohort getCohort() {
        return cohort;
    }

    @Override
    public void setCohort(Cohort cohort) {
        this.cohort = cohort;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
