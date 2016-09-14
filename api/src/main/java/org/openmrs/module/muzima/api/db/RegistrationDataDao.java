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


import org.openmrs.module.muzima.api.db.SingleClassDao;
import org.openmrs.module.muzima.model.RegistrationData;

import java.util.List;

/**
 * Database methods for {@link org.openmrs.module.muzima.api.service.RegistrationDataService}.
 */
public interface RegistrationDataDao extends SingleClassDao<RegistrationData> {

    /**
     * Get registration data by the internal database id of the registration data.
     *
     * @param id the internal database id.
     * @return the registration data with matching internal database id.
     */
    RegistrationData getRegistrationDataById(final Integer id);

    /**
     * Get registration data by the uuid of the registration data.
     *
     * @param uuid the uuid of the registration data.
     * @return the registration data with matching uuid.
     */
    RegistrationData getRegistrationDataByUuid(final String uuid);

    /**
     * Get registration data based on the temporary uuid assigned to a patient created through the registration form
     * and / or the real uuid of the patient data created after processing the registration form (for new patient) or
     * the real uuid of the existing patient (for existing patient).
     *
     * @param temporaryUuid the temporary uuid assigned to a patient.
     * @param assignedUuid  the real uuid of a newly created patient or the real uuid of an existing patient.
     * @return the registration data based on the temporary uuid and / or the assigned uuid.
     */
    List<RegistrationData> getRegistrationData(final String temporaryUuid, final String assignedUuid);

    /**
     * Create a new registration data entry in the database.
     *
     * @param registrationData the registration data to be created.
     * @return the new registration data.
     */
    RegistrationData saveRegistrationData(final RegistrationData registrationData);

    /**
     * Delete a registration data.
     *
     * @param registrationData the registration data to be deleted.
     */
    void deleteRegistrationData(final RegistrationData registrationData);

    /**
     * Get all registration data information from the database.
     *
     * @param pageNumber the page number.
     * @param pageSize   the page size.
     * @return all registration data in the database.
     */
    List<RegistrationData> getRegistrationData(final Integer pageNumber, final Integer pageSize);

    /**
     * Count the number of registration data in the database.
     * @return the number of registration data in the database.
     */
    Number countRegistrationData();
}