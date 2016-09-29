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
import org.openmrs.Cohort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class FakeCohort extends BaseOpenmrsData {

    private static final Logger log = LoggerFactory.getLogger(FakeCohort.class.getSimpleName());

    private static final String[] properties = new String[]{
            "uuid", "name", "description", "memberIds",
            "creator", "dateCreated", "changedBy", "dateChanged", "voidedBy", "dateVoided", "voidReason"
    };

    private Integer id;
    private String name;
    private String description;
    private Set<Integer> memberIds;

    private FakeCohort() {
    }

    public static FakeCohort copyCohort(final Cohort cohort) {
        FakeCohort fakeCohort = new FakeCohort();
        for (String property : properties) {
            try {
                Object o = PropertyUtils.getProperty(cohort, property);
                PropertyUtils.setProperty(fakeCohort, property, o);
            } catch (Exception e) {
                log.error("Copying property failed for property: '" + property + "' with message: " + e.getMessage(), e);
            }
        }
        fakeCohort.setVoided(cohort.getVoided());
        return fakeCohort;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Integer> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<Integer> memberIds) {
        this.memberIds = memberIds;
    }
}
