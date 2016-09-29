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