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
package org.openmrs.module.muzima.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsMetadata;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MuzimaConfig extends BaseOpenmrsMetadata {
    private Integer id;
    private String configJson;

    public MuzimaConfig() {
    }    // used by hibernate

    public MuzimaConfig(String configJson) {
        // Correcting for the fact that in v1.8.2 of the stand-alone server the uuid of BaseOpenmrsObject is
        // not computed each time the empty constructor is called as is the case in v1.9.3
        if (getUuid()==null) {
            setUuid(UUID.randomUUID().toString());
        }
        this.configJson = configJson;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }

    @Override
    public String toString() {
        return "MuzimaConfig{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", config='" + configJson +
                '}';
    }
}