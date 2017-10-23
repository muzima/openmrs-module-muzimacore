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
import org.openmrs.module.muzima.exception.InvalidSettingException;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MuzimaSetting extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String property;
    private String valueString;
    private Boolean valueBoolean;

    private MuzimaSettingDataType settingDataType;

    public MuzimaSetting(){
    }    // used by hibernate

    public MuzimaSetting(String property, MuzimaSettingDataType settingDataType){
        setProperty(property);
        setSettingDataType(settingDataType);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    public String getProperty(){
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Enumerated(EnumType.STRING)
    public MuzimaSettingDataType getSettingDataType() {
        return settingDataType;
    }

    public void setSettingDataType(MuzimaSettingDataType settingDataType) {
        this.settingDataType = settingDataType;
    }

    public Boolean getValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(Boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Object getSettingValue() {
        if (MuzimaSettingDataType.BOOLEAN.equals(getSettingDataType())) {
            return valueBoolean;
        } else {
            return valueString;
        }
    }

    public void setSettingValue(Object value, MuzimaSettingDataType settingDataType) throws InvalidSettingException {
        if(settingDataType.equals(MuzimaSettingDataType.BOOLEAN)) {
            setValueBoolean((Boolean)value);
        } else if(value instanceof String){
            setValueString((String)value);
        } else {
            throw new InvalidSettingException("Cannot set Setting value. Value object type not supported");
        }
    }

    public void setSettingValue(Object value) throws InvalidSettingException {
        if(getSettingDataType() == null){
            throw new InvalidSettingException("Cannot set Setting value. Setting DataType is not defined");
        }
        setSettingValue(value, getSettingDataType());
    }

    @Override
    public String toString() {
        return "MuzimaSetting{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", name='" + getName() +
                ", property='" + getProperty() +
                ", description='" + getDescription() +
                ", value='" + getSettingValue().toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }
}
