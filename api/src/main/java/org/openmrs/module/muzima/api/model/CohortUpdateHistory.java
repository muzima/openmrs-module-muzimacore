package org.openmrs.module.muzima.api.model;

import java.io.Serializable;
import java.util.Date;

public class CohortUpdateHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer cohortId;
    private String membersAdded;
    private String membersRemoved;
    private Date dateUpdated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCohortId() {
        return cohortId;
    }

    public void setCohortId(Integer cohortId) {
        this.cohortId = cohortId;
    }

    public String getMembersAdded() {
        return membersAdded;
    }

    public void setMembersAdded(String membersAdded) {
        this.membersAdded = membersAdded;
    }

    public String getMembersRemoved() {
        return membersRemoved;
    }

    public void setMembersRemoved(String membersRemoved) {
        this.membersRemoved = membersRemoved;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
