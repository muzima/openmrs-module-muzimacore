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
package org.openmrs.module.muzima.web.resource.wrapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class FakeObs extends BaseOpenmrsData {

    private static final Logger log = LoggerFactory.getLogger(FakeCohort.class.getSimpleName());

    private static final String[] properties = new String[]{
            "uuid", "obsDatetime",
            "valueText", "valueNumeric", "valueDatetime", "valueCoded",
            "location", "encounter", "person", "concept",
            "creator", "dateCreated", "changedBy", "dateChanged", "voidedBy", "dateVoided", "voidReason"
    };

    private Integer id;

    private Person person;
    private Location location;
    private Encounter encounter;

    private Concept concept;
    private Date obsDatetime;

    private String valueText;
    private Concept valueCoded;
    private Date valueDatetime;
    private Double valueNumeric;

    private FakeObs() {
    }

    public static FakeObs copyObs(final Obs obs) {
        FakeObs fakeObs = new FakeObs();
        for (String property : properties) {
            try {
                Object o = PropertyUtils.getProperty(obs, property);
                PropertyUtils.setProperty(fakeObs, property, o);
            } catch (Exception e) {
                log.error("Copying property failed for property: '" + property + "' with message: " + e.getMessage(), e);
            }
        }
        fakeObs.setVoided(obs.getVoided());
        return fakeObs;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Date getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(Date obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public Concept getValueCoded() {
        return valueCoded;
    }

    public void setValueCoded(Concept valueCoded) {
        this.valueCoded = valueCoded;
    }

    public Date getValueDatetime() {
        return valueDatetime;
    }

    public void setValueDatetime(Date valueDatetime) {
        this.valueDatetime = valueDatetime;
    }

    public Double getValueNumeric() {
        return valueNumeric;
    }

    public void setValueNumeric(Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }
}
