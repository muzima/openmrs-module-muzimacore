package org.openmrs.module.muzima.model;

import org.openmrs.BaseOpenmrsMetadata;

public class MuzimaModuleVersion extends BaseOpenmrsMetadata {
    private String version;
    private int id;
    private long timestamp;
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
