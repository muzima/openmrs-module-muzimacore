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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Cohort;
import org.openmrs.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportConfiguration extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String reportDesignUuid;
    private String cohortUuid;
    private User user;
    private Boolean priority;

    public ReportConfiguration(){
    }    // used by hibernate

    public ReportConfiguration(String reportDesignUuid, String cohortUuid){
        setReportDesignUuid(reportDesignUuid);
        setCohortUuid(cohortUuid);
       
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setReportDesignUuid(final String reportDesignUuid) {
        this.reportDesignUuid = reportDesignUuid;
    }
    
    public String getReportDesignUuid(){
        return reportDesignUuid;
    }

    public String getCohortUuid(){
        return cohortUuid;
    }

    public void setCohortUuid(final String cohortUuid) {
        this.cohortUuid = cohortUuid;
    }
    
    public Boolean getPriority(){
        return priority;
    }
    
    public void setPriority(final Boolean priority) {
        this.priority = priority;
    }
    
    public User getCreator(){
        return user;
    }
    public void setCreator(User creator){
        this.user = creator;
    }

    @Override
    public String toString() {
        return "MuzimaSetting{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", name='" + getName() +
                ", reportUuid='" + getReportDesignUuid() +
                ", cohortUuid='" + getCohortUuid()+
                ", description='" + getDescription() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }
}
