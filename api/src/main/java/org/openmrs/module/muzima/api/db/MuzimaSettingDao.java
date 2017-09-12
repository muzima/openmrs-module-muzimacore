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

import org.openmrs.module.muzima.model.MuzimaSetting;

import java.util.List;

public interface MuzimaSettingDao {

    List<MuzimaSetting> getAll();

    List<MuzimaSetting> getPagedSettings(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Get the total number of data source with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of data source in the database.
     */
    Number countDataSource(final String search);

    Number countSettings();

    MuzimaSetting getSettingById(Integer id);

    MuzimaSetting getSettingByUuid(String Uuid);

    MuzimaSetting getSettingByProperty(String property);

    MuzimaSetting saveOrUpdateSetting(MuzimaSetting setting);

    void deleteSetting(MuzimaSetting setting);
}
