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

import org.openmrs.BaseOpenmrsData;
import java.io.Serializable;

public class CohortDefinitionData  extends BaseOpenmrsData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer cohortId;
    private String definition;
    //Defines whether this cohort definition is scheduled for execution by scheduler task
    private boolean isScheduledForExecution;
    private boolean isMemberAdditionEnabled;
    private boolean isMemberRemovalEnabled;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getCohortId(){
        return cohortId;
    }

    public void setCohortId(final Integer cohortId){
        this.cohortId=cohortId;
    }

    public String getDefinition(){
        return definition;
    }

    public void setDefinition(String definition){
        this.definition=definition;
    }

    public void setIsScheduledForExecution(boolean isScheduled){
        this.isScheduledForExecution =isScheduled;
    }

    public boolean getIsScheduledForExecution(){
        return isScheduledForExecution;
    }

    public void setIsMemberAdditionEnabled(boolean isMemberAdditionEnabled) {
        this.isMemberAdditionEnabled = isMemberAdditionEnabled;
    }

    public boolean getIsMemberAdditionEnabled(){
        return isMemberAdditionEnabled;
    }

    public void setIsMemberRemovalEnabled(boolean isMemberRemovalEnabled) {
        this.isMemberRemovalEnabled = isMemberRemovalEnabled;
    }

    public boolean getIsMemberRemovalEnabled(){
        return isMemberRemovalEnabled;
    }
}
