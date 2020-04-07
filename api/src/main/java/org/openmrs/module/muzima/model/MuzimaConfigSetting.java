package org.openmrs.module.muzima.model;

import org.openmrs.BaseOpenmrsMetadata;

public class MuzimaConfigSetting extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private MuzimaConfig config;
    private MuzimaSetting muzimaSetting;
    private Boolean valueBoolean;
    private String valueString;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public MuzimaConfig getConfig() {
        return config;
    }

    public void setConfig(MuzimaConfig config) {
        this.config = config;
    }

    public MuzimaSetting getMuzimaSetting() {
        return muzimaSetting;
    }

    public void setMuzimaSetting(MuzimaSetting muzimaSetting) {
        this.muzimaSetting = muzimaSetting;
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
}
