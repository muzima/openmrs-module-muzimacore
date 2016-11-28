/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.web.resource.wrapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class FakeConcept extends BaseOpenmrsMetadata {

    private static final Logger log = LoggerFactory.getLogger(FakeConcept.class.getSimpleName());

    private static final String[] properties = new String[]{
            "uuid", "descriptions", "datatype", "names",
            "creator", "dateCreated", "changedBy", "dateChanged", "retiredBy", "dateRetired", "retireReason"
    };

    private Integer id;
    private String units;
    private String displayConcept;
    private Boolean precise;
    private Boolean numeric;

    private ConceptDatatype datatype;
    private Collection<ConceptName> names;
    private Collection<ConceptDescription> descriptions;

    private FakeConcept() {
    }

    public static FakeConcept copyConcept(final Concept concept) {
        if (concept == null) return  null;
        FakeConcept fakeConcept = new FakeConcept();
        for (String property : properties) {
            try {
                Object o = PropertyUtils.getProperty(concept, property);
                PropertyUtils.setProperty(fakeConcept, property, o);
            } catch (Exception e) {
                log.error("Copying property failed for property: '" + property + "' with message: " + e.getMessage(), e);
            }
        }

        if (concept.isNumeric()) {
            ConceptNumeric numeric = Context.getConceptService().getConceptNumeric(concept.getConceptId());
            fakeConcept.setUnits(numeric.getUnits());
            fakeConcept.setPrecise(numeric.getPrecise());
            fakeConcept.setNumeric(concept.isNumeric());
        }

        fakeConcept.setRetired(concept.isRetired());
        fakeConcept.setDisplayConcept(concept.getDisplayString());
        return fakeConcept;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Boolean getPrecise() {
        return precise;
    }

    public void setPrecise(Boolean precise) {
        this.precise = precise;
    }

    public Boolean isNumeric() {
        return numeric;
    }

    public void setNumeric(Boolean numeric) {
        this.numeric = numeric;
    }

    public ConceptDatatype getDatatype() {
        return datatype;
    }

    public void setDatatype(ConceptDatatype datatype) {
        this.datatype = datatype;
    }

    public Collection<ConceptName> getNames() {
        return names;
    }

    public void setNames(Collection<ConceptName> names) {
        this.names = names;
    }

    public Collection<ConceptDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Collection<ConceptDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public String getDisplayConcept() {
        return displayConcept;
    }

    public void setDisplayConcept(String displayConcept) {
        this.displayConcept = displayConcept;
    }
}