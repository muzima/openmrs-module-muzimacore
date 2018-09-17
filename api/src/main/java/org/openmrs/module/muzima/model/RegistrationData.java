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
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;

import java.io.Serializable;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class RegistrationData extends BaseOpenmrsData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;

    private String temporaryUuid;

    private String assignedUuid;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(final Integer id) {
		this.id = id;
	}

    public String getTemporaryUuid() {
        return temporaryUuid;
    }

    public void setTemporaryUuid(final String temporaryUuid) {
        this.temporaryUuid = temporaryUuid;
    }

    public String getAssignedUuid() {
        return assignedUuid;
    }

    public void setAssignedUuid(final String assignedUuid) {
        this.assignedUuid = assignedUuid;
    }

    @Override
    public String toString() {
        return "Registration Data id = "+id +
                "temporaryUuid = "+ temporaryUuid +
                "assignedUuid = " + assignedUuid ;
    }
}