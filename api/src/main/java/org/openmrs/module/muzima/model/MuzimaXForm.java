package org.openmrs.module.muzima.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.openmrs.Form;

public class MuzimaXForm {
    private Integer id;
    private Form form;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public MuzimaXForm() {
    }    // used by hibernate

    //Used when serialized to JSON
    public String getDescription() {
        return getForm().getDescription();
    }

    //Used when serialized to JSON
    public String getName() {
        return getForm().getName();
    }

    //Used when serialized to JSON
    public String getUuid() {
        return getForm().getUuid();
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MuzimaXForm html5Form = (MuzimaXForm) o;

        if (id != null ? !id.equals(html5Form.id) : html5Form.id != null) return false;

        return true;
    }

}
