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
import org.openmrs.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MuzimaGeneratedReport extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String reportUuid;
    private String cohortUuid;
    private User user;

    public MuzimaGeneratedReport(){
    }    // used by hibernate

    public MuzimaGeneratedReport(String reportUuid, String cohortUuid){
        setReportUuid(reportUuid);
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
    
    public void setReportUuid(final String reportUuid) {
        this.reportUuid = reportUuid;
    }
    
    public String getReportUuid(){
        return reportUuid;
    }
    
    

    public String getCohortUuid(){
        return cohortUuid;
    }

    public void setCohortUuid(final String cohortUuid) {
        this.cohortUuid = cohortUuid;
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
                ", reportId='" + getReportUuid() +
                ", cohortId='" + getCohortUuid()+
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
