package org.openmrs.module.muzima;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MuzimaFormTag extends BaseOpenmrsData {
    private Integer id;
    private String name;

    public MuzimaFormTag() {
    } // Used by hibernate

    public MuzimaFormTag(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MuzimaFormTag tag = (MuzimaFormTag) o;

        if (id != null ? !id.equals(tag.id) : tag.id != null) return false;
        if (name != null ? !name.equals(tag.name) : tag.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MuzimaFormTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
