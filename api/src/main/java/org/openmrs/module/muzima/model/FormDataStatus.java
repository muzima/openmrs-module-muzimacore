package org.openmrs.module.muzima.model;

import org.openmrs.BaseOpenmrsData;

public class FormDataStatus extends BaseOpenmrsData {

    private Integer id;
    private String status;

    /**
     * @return id - The unique Identifier for the object
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param id - The unique Identifier for the object
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    public FormDataStatus(){
        super();
    }

    public FormDataStatus(String formDataUuid){
        setUuid(formDataUuid);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
