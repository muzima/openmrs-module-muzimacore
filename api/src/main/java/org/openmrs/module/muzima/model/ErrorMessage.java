package org.openmrs.module.muzima.model;

public class ErrorMessage  extends AuditableData {

    private Integer id;
    private String message;

    public ErrorMessage(){

    }
    public ErrorMessage(String message){
        setMessage(message);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
