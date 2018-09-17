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

import org.openmrs.module.muzima.model.CohortDefinitionData;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CohortDefinitionDataDao {
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    CohortDefinitionData getById(Integer id);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    CohortDefinitionData getByUuid(String uuid);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    List<CohortDefinitionData> getAll();

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    List<CohortDefinitionData> getByScheduled(Boolean scheduled);

    @Transactional
    CohortDefinitionData saveOrUpdate(CohortDefinitionData object);

    @Transactional
    CohortDefinitionData update(CohortDefinitionData object);

    @Transactional
    void delete(CohortDefinitionData object);

    Number count();
}
