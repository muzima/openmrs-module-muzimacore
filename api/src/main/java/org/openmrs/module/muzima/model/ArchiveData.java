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


import java.util.Date;

/**
 */
public class ArchiveData extends AuditableData {

    private String message;

    private Date dateArchived;

    public ArchiveData() {
    }

    public ArchiveData(final AuditableData data) {
        super(data);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Date getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(final Date dateArchived) {
        this.dateArchived = dateArchived;
    }

    @Override
    public String toString() {
        return "ArchiveData.message = "+message +
                "ArchiveData.dateArchived = " + dateArchived;
    }
}
