package org.openmrs.module.muzima.model;

import org.openmrs.BaseOpenmrsMetadata;

public class MinimumSupportedAppVersion extends BaseOpenmrsMetadata {
    //Minimum version code of App supported by the module
    private int version;
    private int id;
    private long timestamp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private String uuid;
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
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
