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
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Role;

import java.util.Set;

/**
 * TODO: Write brief description about the class here.
 */
public class NotificationData extends BaseOpenmrsData implements Data {

    private Integer id;

    private Person receiver;

    private Person sender;

    private String subject;

    private String status;

    private String source;

    private String payload;

    private Role role;

    private Patient patient;

    /**
     * @return id - The unique Identifier for the object
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param id - The unique Identifier for the object
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Get the person who will receive this notification.
     *
     * @return the person who will receive this notification.
     */
    public Person getReceiver() {
        return receiver;
    }

    /**
     * Set the person who will receive this notification.
     *
     * @param recipient the person who will receive this notification.
     */
    public void setReceiver(final Person recipient) {
        this.receiver = recipient;
    }

    /**
     * Get the person who will send this notification.
     *
     * @return the person who will send this notification.
     */
    public Person getSender() {
        return sender;
    }

    /**
     * Set the person who will send this notification.
     *
     * @param sender the person who will send this notification.
     */
    public void setSender(final Person sender) {
        this.sender = sender;
    }

    /**
     * Set the subject of the notification.
     *
     * @return the subject of the notification.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject of the notification.
     *
     * @param subject the subject of the notification.
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Get the source of the notification.
     *
     * @return the source of the notification.
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the source of the notification information.
     *
     * @param source the source of the notification information.
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * Get the status of the notification.
     *
     * @return the status of the notification.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the notification.
     *
     * @param status the status of the notification.
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Get the data payload of this data.
     *
     * @return the payload of this data.
     */
    @Override
    public String getPayload() {
        return payload;
    }

    /**
     * Set the data payload of this data.
     */
    public void setPayload(final String payload) {
        this.payload = payload;
    }

    /**
     * Get the role to which this notification is assigned.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Set the role to which this notification is assigned.
     *
     * @param role the role
     */
    public void setRole(final Role role) {
        this.role = role;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
