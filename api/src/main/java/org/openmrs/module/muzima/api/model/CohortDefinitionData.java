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
package org.openmrs.module.muzima.api.model;

import org.openmrs.BaseOpenmrsData;
import java.io.Serializable;

public class CohortDefinitionData  extends BaseOpenmrsData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer cohortId;
    private String definition;
    private boolean scheduled;
    private boolean enableMemberAddition;
    private boolean enableMemberRemoval;

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

    public void setScheduled(boolean scheduled){
        this.scheduled=scheduled;
    }

    public boolean getScheduled(){
        return scheduled;
    }

    public void setEnableMemberAddition(boolean enableMemberAddition) {
        this.enableMemberAddition = enableMemberAddition;
    }

    public boolean getEnableMemberAddition(){
        return enableMemberAddition;
    }

    public void setEnableMemberRemoval(boolean enableMemberRemoval) {
        this.enableMemberRemoval = enableMemberRemoval;
    }

    public boolean getEnableMemberRemoval(){
        return enableMemberRemoval;
    }
}
