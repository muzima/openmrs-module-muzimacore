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
public class GeneratedReport extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer cohortReportConfigId;
    private Integer patientId;
    private String reportJson;

    public GeneratedReport(){
    }    // used by hibernate

    public GeneratedReport(Integer patientId, Integer cohortReportConfigId, String reportJson){
        setPatientId(patientId);
        setCohortReportConfigId(cohortReportConfigId);
        setReportJson(reportJson);
       
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setPatientId(final Integer patientId) {
        this.patientId = patientId;
    }
    
    public Integer getPatientId(){
        return patientId;
    }
    
    

    public Integer getCohortReportConfigId(){
        return cohortReportConfigId;
    }

    public void setCohortReportConfigId(final Integer cohortReportConfigId) {
        this.cohortReportConfigId = cohortReportConfigId;
    }
    
    public String getReportJson(){
        return reportJson;
    }
    
    public void setReportJson(final String reportJson) {
        this.reportJson= reportJson;
    }
    
   
    @Override
    public String toString() {
        return "MuzimaSetting{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", name='" + getName() +
                ", cohortReportConfigId='" + getCohortReportConfigId() +
                ", patientId='" + getPatientId()+
                ", reportJson='" + getReportJson() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }
}
