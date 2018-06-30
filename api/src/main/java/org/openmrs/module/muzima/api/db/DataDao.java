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

import org.openmrs.module.muzima.model.Data;

import java.util.List;

/**
 */
public interface DataDao<T extends Data> extends SingleClassDao<T> {

    /**
     * Return the data with the given id.
     *
     * @param id the data id.
     * @return the data with the matching id.
     * @should return data with matching id.
     * @should return null when no data with matching id.
     */
    T getData(final Integer id);

    /**
     * Return the data with the given uuid.
     *
     * @param uuid the data uuid.
     * @return the data with the matching uuid.
     * @should return data with matching uuid.
     * @should return null when no data with matching uuid.
     */
    T getDataByUuid(final String uuid);

    /**
     * Return all saved data.
     *
     * @return all saved data.
     */
    List<T> getAllData();

    /**
     * Save data into the database.
     *
     * @param data the data.
     * @return saved data.
     * @should save data into the database.
     */
    T saveData(final T data);

    /**
     * Delete data from the database.
     *
     * @param data the data
     * @should remove data from the database
     */
    void purgeData(final T data);

    /**
     * Get data with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of data for the page.
     */
    List<T> getPagedData(final String search, final Integer pageNumber, final Integer pageSize);

    List<T> getPagedData(final String search, final Integer pageNumber, final Integer pageSize, final List<Integer> errorIds);

    /**
     * Get the total number of data with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of data in the database.
     */
    Number countData(final String search);
}
