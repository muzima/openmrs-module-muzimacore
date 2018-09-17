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
import java.util.Set;

/**
 */
public class ErrorData extends AuditableData {

    private String message;

    private Date dateProcessed;

    private Set<ErrorMessage> errorMessages;

    public ErrorData() {
    }

    public ErrorData(final AuditableData data) {
        super(data);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Date getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(final Date dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public Set<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Set<ErrorMessage> errorMessages) {
        this.errorMessages = errorMessages;
    }

    @Override
    public String toString() {
        return "ErrorData.message = " + message +
                " ErrorData.dateProcessed = " + dateProcessed +
                " ErrorData.errorMessages = " +errorMessages;
    }
}