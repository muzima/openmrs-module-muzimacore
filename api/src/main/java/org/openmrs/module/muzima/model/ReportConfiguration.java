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

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportConfiguration extends BaseOpenmrsMetadata {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String reportId;
    private String cohortId;

    public ReportConfiguration(){
    }    // used by hibernate

    public ReportConfiguration(String reportId, String cohortId){
        setReportId(reportId);
        setCohortId(cohortId);
       
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }
    
    public String getReportId(){
        return reportId;
    }

    public String getCohortId(){
        return cohortId;
    }

    public void setCohortId(String property) {
        this.cohortId = cohortId;
    }

    @Override
    public String toString() {
        return "MuzimaSetting{" +
                "id=" + id +
                ", uuid=" + getUuid() +
                ", name='" + getName() +
                ", reportId='" + getReportId() +
                ", cohortId='" + getCohortId()+
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
