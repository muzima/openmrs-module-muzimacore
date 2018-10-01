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
import org.openmrs.Location;
import org.openmrs.Provider;

/**
 */
public abstract class AuditableData extends BaseOpenmrsData implements Data {

    private Integer id;

    private String payload;

    private String discriminator;

    private DataSource dataSource;

    private Location location;

    private Provider provider;

    private String formName;

    private String patientUuid;

    /**
     * **** Audit information ******
     */

    public AuditableData() {
    }

    public AuditableData(final AuditableData data) {
        setPayload(data.getPayload());
        setDataSource(data.getDataSource());
        setDiscriminator(data.getDiscriminator());
        setLocation(data.getLocation());
        setProvider(data.getProvider());
        setFormName(data.getFormName());
        setPatientUuid(data.getPatientUuid());
    }

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
     * Get the data payload of this data.
     *
     * @return the payload of this data.
     */
    @Override
    public String getPayload() {
        return payload;
    }

    /**
     * Set the data payload for this data.
     *
     * @param payload the payload for this data
     */
    public void setPayload(final String payload) {
        this.payload = payload;
    }

    /**
     * Get the discriminating value to determine which handler to execute.
     *
     * @return the discriminating value to determine which handler to execute.
     */
    public String getDiscriminator() {
        return discriminator;
    }

    /**
     * Set the discriminating value to determine which handler to execute.
     *
     * @param discriminator the discriminating value to determine which handler to execute.
     */
    public void setDiscriminator(final String discriminator) {
        this.discriminator = discriminator;
    }

    /**
     * Get the data source of this data.
     *
     * @return the data source of this data.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Set the data source for this data.
     *
     * @param dataSource the data source for this data.
     */
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    @Override
    public String toString() {
        return "AuditableData.id = " +id+
                " AuditableData.payload = "+ payload +
                " AuditableData.discriminator = "+ discriminator +
                " AuditableData.dataSource = "+dataSource +
                " AuditableData.location = "+location+
                " AuditableData.provider = "+provider+
                " AuditableData.formName = "+formName+
                " AuditableData.patientUuid = "+patientUuid;
    }
}
