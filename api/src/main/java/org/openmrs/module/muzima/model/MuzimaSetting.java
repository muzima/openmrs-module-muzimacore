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

import org.openmrs.BaseOpenmrsMetadata;

public class MuzimaSetting extends BaseOpenmrsMetadata {

    private Integer id;
    private String property;
    private String value;

    /**
     * @return id - The unique Identifier for the setting object
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param id - The unique Identifier for the setting object
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * @return property - The unique property for the setting object
     */
    public String getProperty(){
        return property;
    }

    /**
     * @param property - The unique property for the setting object
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return value - The value for the setting object
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value - The value for the setting object
     */
    public void setValue(String value) {
        this.value = value;
    }
}
