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
import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.CohortMember1_8;

/**
 * TODO: Write brief description about the class here.
 */
public class FakeCohortMember extends CohortMember1_8 {
    /**
     * Copier constructor to set fields
     *
     * @param patient
     * @param cohort
     */
    public FakeCohortMember(final Patient patient, final Cohort cohort) {
        super(patient, cohort);
    }

    /**
     * Default constructor
     */
    public FakeCohortMember() {
    }
}
