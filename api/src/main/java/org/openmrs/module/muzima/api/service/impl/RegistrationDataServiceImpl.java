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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.api.service.RegistrationDataService;
import org.openmrs.module.muzima.model.RegistrationData;

import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.muzima.api.service.RegistrationDataService}.
 */
public class RegistrationDataServiceImpl extends BaseOpenmrsService implements RegistrationDataService {

    private final Log log = LogFactory.getLog(this.getClass());

    private RegistrationDataDao dao;

    /**
     * @param dao the dao to set
     */
    public void setDao(RegistrationDataDao dao) {
        this.dao = dao;
    }

    /**
     * @return the dao
     */
    public RegistrationDataDao getDao() {
        return dao;
    }

    /**
     * Get registration data by the internal database id of the registration data.
     *
     * @param id the internal database id.
     * @return the registration data with matching internal database id.
     */
    @Override
    public RegistrationData getRegistrationDataById(final Integer id) {
        return dao.getRegistrationDataById(id);
    }

    /**
     * Get registration data by the uuid of the registration data.
     *
     * @param uuid the uuid of the registration data.
     * @return the registration data with matching uuid.
     */
    @Override
    public RegistrationData getRegistrationDataByUuid(final String uuid) {
        return dao.getRegistrationDataByUuid(uuid);
    }

    /**
     * Get registration data based on the temporary uuid assigned to a patient created through the registration form.
     *
     * @param temporaryUuid the temporary uuid assigned to a patient.
     * @return the registration data based on the temporary uuid.
     */
    @Override
    public RegistrationData getRegistrationDataByTemporaryUuid(final String temporaryUuid) {
        List<RegistrationData> registrationDataList = dao.getRegistrationData(temporaryUuid, StringUtils.EMPTY);
        if (registrationDataList.size() == 1) {
            return registrationDataList.get(0);
        } else if (registrationDataList.size() > 1) {
            throw new APIException("Unable to uniquely identify registration data!");
        } else {
            // the size = 0
            return null;
        }
    }

    /**
     * Get registration data based on the patient real uuid.
     *
     * @param assignedUuid the patient real uuid.
     * @return list of temporary uuid which correspond to temporary patients data created through registration.
     */
    @Override
    public List<RegistrationData> getRegistrationDataByAssignedUuid(final String assignedUuid) {
        return dao.getRegistrationData(StringUtils.EMPTY, assignedUuid);
    }

    /**
     * Create a new registration data entry in the database.
     *
     * @param registrationData the registration data to be created.
     * @return the new registration data.
     */
    @Override
    public RegistrationData saveRegistrationData(final RegistrationData registrationData) {
        return dao.saveRegistrationData(registrationData);
    }

    /**
     * Delete a registration data.
     *
     * @param registrationData the registration data to be deleted.
     */
    @Override
    public void deleteRegistrationData(final RegistrationData registrationData) {
        dao.deleteRegistrationData(registrationData);
    }

    /**
     * Get all registration data information from the database.
     *
     * @param pageNumber the page number.
     * @param pageSize   the page size.
     * @return all registration data in the database.
     */
    @Override
    public List<RegistrationData> getRegistrationData(final Integer pageNumber, final Integer pageSize) {
        return dao.getRegistrationData(pageNumber, pageSize);
    }

    /**
     * Count the number of registration data in the database.
     * @return the number of registration data in the database.
     */
    public Number countRegistrationData() {
        return dao.countRegistrationData();
    }
}