package org.openmrs.module.muzima.model;

public enum MuzimaSettingDataType {
    BOOLEAN("BOOLEAN"),
    PASSWORD("PASSWORD"),
    STRING("STRING");
    private String name;
    MuzimaSettingDataType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
