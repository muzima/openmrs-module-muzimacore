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

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public interface SingleClassDao<T> {
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    T getById(Integer id);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    List<T> getAll();

    @Transactional
    T saveOrUpdate(T object);

    @Transactional
    T update(T object);

    @Transactional
    void delete(T object);
}
