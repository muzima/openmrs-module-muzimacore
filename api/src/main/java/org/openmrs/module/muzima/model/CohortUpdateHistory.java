/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.model;

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
