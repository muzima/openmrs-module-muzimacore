package org.openmrs.module.muzima.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Form;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MuzimaForm extends BaseOpenmrsMetadata {
    private Integer id;
    private String discriminator;
    private String model;
    private String html;
    private String modelJson;
    private String form; ///uuid to form table
    private Set<MuzimaFormTag> tags = new HashSet<MuzimaFormTag>();
    private Form formDefinition;

    public MuzimaForm() {
    }    // used by hibernate

    public MuzimaForm(String form, String discriminator, String html, String model, String modelJson, Form formDefinition) {
        // Correcting for the fact that in v1.8.2 of the stand-alone server the uuid of BaseOpenmrsObject is
        // not computed each time the empty constructor is called as is the case in v1.9.3
        if (getUuid()==null) {
            setUuid(UUID.randomUUID().toString());
        }
        this.form = form;
        this.discriminator = discriminator;
        // form structure
        this.html = html;
        this.model = model;
        this.modelJson = modelJson;
        this.formDefinition = formDefinition;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return formDefinition.getName() == null ? "" : formDefinition.getName();
    }

    public String getDescription() {
        return formDefinition.getDescription() == null ? "" : formDefinition.getDescription();
    }

    public String getDiscriminator() {
        return discriminator == null ? "" : discriminator;
    }

    public void setDiscriminator(final String discriminator) {
        this.discriminator = discriminator;
    }

    public String getModelJson() {
        return modelJson;
    }

    public void setModelJson(String modelJson) {
        this.modelJson = modelJson;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Set<MuzimaFormTag> getTags() {
        return tags;
    }

    public void setTags(Set<MuzimaFormTag> tags) {
        this.tags = tags;
    }

    public String getForm() {
        return form;
    }

    public void setForm(final String form) {
        this.form = form;
    }

    public Form getFormDefinition() {
        return formDefinition;
    }

    public void setFormDefinition(Form formDefinition) {
        this.formDefinition = formDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MuzimaForm muzimaForm = (MuzimaForm) o;

        if (getFormDefinition().getDescription() != null ? !getFormDefinition().getDescription().equals(muzimaForm.getFormDefinition().getDescription()) : muzimaForm.getFormDefinition().getDescription() != null)
            return false;
        if (id != null ? !id.equals(muzimaForm.id) : muzimaForm.id != null) return false;
        if (getFormDefinition().getName() != null ? !getFormDefinition().getName().equals(muzimaForm.getFormDefinition().getName()) : muzimaForm.getFormDefinition().getName() != null) return false;
        if (getForm() != null ? !getForm().equals(muzimaForm.getForm()) : muzimaForm.getForm() != null) return false;
        if (getDiscriminator() != null ? !getDiscriminator().equals(muzimaForm.getDiscriminator()) : muzimaForm.getDiscriminator() != null) return false;
        if (tags != null ? !tags.equals(muzimaForm.tags) : muzimaForm.tags != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (getFormDefinition().getName() != null ? getFormDefinition().getName().hashCode() : 0);
        result = 31 * result + (getForm() != null ? getForm().hashCode() : 0);
        result = 31 * result + (getDiscriminator() != null ? getDiscriminator().hashCode() : 0);
        result = 31 * result + (getFormDefinition().getDescription() != null ? getFormDefinition().getDescription().hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MuzimaForm{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", form='" + getForm() + '\'' +
                ", discriminator='" + getDiscriminator() + '\'' +
                ", tags=" + tags +
                '}';
    }

    public String getVersion() {
        return formDefinition.getVersion() == null ? "" : formDefinition.getVersion();
    }
}
