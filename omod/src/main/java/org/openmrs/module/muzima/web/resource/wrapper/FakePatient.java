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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

public class FakePatient extends BaseOpenmrsData {

    private static final Logger log = LoggerFactory.getLogger(FakePatient.class.getSimpleName());

    private static final String[] properties = new String[]{
            "uuid", "gender", "birthdate", "birthdateEstimated",
            "names", "identifier", "attributes", "addresses"
    };

    private Integer id;
    private String gender;
    private Date birthdate;
    private Boolean birthdateEstimated = Boolean.FALSE;

    private Set<PersonAddress> addresses;
    private Set<PersonName> names;
    private Set<PersonAttribute> attributes;
    private Set<PatientIdentifier> identifiers;

    private FakePatient() {
    }

    public static FakePatient copyPatient(final Patient patient) {
        FakePatient fakePatient = new FakePatient();
        for (String property : properties) {
            try {
                Object o = PropertyUtils.getProperty(patient, property);
                PropertyUtils.setProperty(fakePatient, property, o);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        fakePatient.setVoided(patient.getVoided());
        return fakePatient;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public Set<PersonAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    public Set<PersonName> getNames() {
        return names;
    }

    public void setNames(Set<PersonName> names) {
        this.names = names;
    }

    public Set<PersonAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<PersonAttribute> attributes) {
        this.attributes = attributes;
    }

    public Set<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Set<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }
}
