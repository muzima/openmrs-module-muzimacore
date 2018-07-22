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
    private Integer patientId;
    private Integer cohortReportConfigId;
    private String reportRequestUuid;
    private User user;
    private String status;
    private String reportJson;
    private  Boolean priority;

    public MuzimaGeneratedReport(){
    }    // used by hibernate

    public MuzimaGeneratedReport(String reportRequestUuid, Integer cohortReportConfigId){
        setCohortReportConfigId(cohortReportConfigId);
        setReportRequestUuid(reportRequestUuid);
       
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setCohortReportConfigId(final Integer cohortReportConfigId) {
        this.cohortReportConfigId = cohortReportConfigId;
    }
    
    public Integer getCohortReportConfigId(){
        return cohortReportConfigId;
    }
    
    public void setReportRequestUuid(final String reportRequestUuid) {
        this.reportRequestUuid = reportRequestUuid;
    }
    
    public String getReportRequestUuid(){
        return reportRequestUuid;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setPatientId(final Integer patientId) {
        this.patientId = patientId;
    }
    
    public Integer getPatientId(){
        return patientId;
    }
    
    public void setReportJson(final String reportJson) {
        this.reportJson = reportJson;
    }
    
    public String getReportJson(){
        return reportJson;
    }
    
    public void setPriority(final Boolean priority) {
        this.priority = priority;
    }
    
    public Boolean getPriority(){
        return priority;
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
