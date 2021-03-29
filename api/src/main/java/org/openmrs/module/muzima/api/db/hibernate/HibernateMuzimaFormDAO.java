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
package org.openmrs.module.muzima.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Form;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HibernateMuzimaFormDAO implements MuzimaFormDAO {
    private DbSessionFactory factory;

    public HibernateMuzimaFormDAO(DbSessionFactory factory) {
        this.factory = factory;
    }

    private DbSession session() {
        return factory.getCurrentSession();
    }

    public List<MuzimaForm> getAll() {
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();
    }

    /* For the xforms, internal id = 0 is reserved by the xform module and has no related form in @org.openmrs.form*/
    public List<MuzimaXForm> getXForms() {
        Criteria criteria = session().createCriteria(MuzimaXForm.class);
        criteria.add(Restrictions.ne("id", 0));

        return criteria.list();
    }

    public Number countXForms(String search) {
        Criteria criteria = session().createCriteria(MuzimaXForm.class, "muzimaXForm");
        criteria.add(Restrictions.ne("id", 0));
        criteria.createAlias("muzimaXForm.form", "form");

        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("form.name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("form.description", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("form.retired", Boolean.FALSE));

        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }

    public List<MuzimaXForm> getPagedXForms(final String search, final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = session().createCriteria(MuzimaXForm.class, "muzimaXForm");
        criteria.add(Restrictions.ne("id", 0));
        criteria.createAlias("muzimaXForm.form", "form");

        if (StringUtils.isNotEmpty(search)) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("form.name", search, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.ilike("form.description", search, MatchMode.ANYWHERE));
            criteria.add(disjunction);
        }
        criteria.add(Restrictions.eq("form.retired", Boolean.FALSE));

        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }

        return criteria.list();
    }

    public void saveForm(MuzimaForm form) {
        session().saveOrUpdate(form);
    }

    public MuzimaForm getFormById(Integer id) {
        return (MuzimaForm) session().get(MuzimaForm.class, id);
    }

    public MuzimaForm getFormByUuid(String uuid) {
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (MuzimaForm) criteria.uniqueResult();
    }
    public List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired){
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("form", form));
        if (!includeRetired)
            criteria.add(Restrictions.eq("retired", false));
        return (List<MuzimaForm>) criteria.list();
    }

    @Override
    public List<Form> getNonMuzimaForms(String search) {
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("retired", false));
        List<MuzimaForm> muzimaForms = criteria.list();
        List<String> formUuids = new ArrayList<String>();
        for(MuzimaForm muzimaForm : muzimaForms){
            formUuids.add(muzimaForm.getForm());
        }

        Criteria criteria1 = session().createCriteria(Form.class);
        criteria1.add(Restrictions.eq("retired", false));
        if(formUuids.size()>0)
            criteria1.add(Restrictions.not(Restrictions.in("uuid",formUuids)));

        if(StringUtils.isNotEmpty(search)){
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
            criteria1.add(disjunction);
        }

        return criteria1.list();
    }

    @Override
    public List<Object[]> getFormCountGroupedByDiscriminator() {
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("retired", false));
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("discriminator"));
        projectionList.add(Projections.rowCount());
        criteria.setProjection(projectionList);
        List<Object[]> results = criteria.list();
        return  results;
    }

    public List<MuzimaForm> getFormByName(final String name, final Date syncDate) {
        Criteria criteriaform = session().createCriteria(MuzimaForm.class);
        if (syncDate != null) {
            criteriaform.add(Restrictions.or(
                    Restrictions.or(
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.ge("dateCreated", syncDate)),
                                    Restrictions.and(Restrictions.isNull("dateChanged"), Restrictions.isNull("dateRetired"))),
                            Restrictions.and(
                                    Restrictions.and(Restrictions.isNotNull("dateChanged"), Restrictions.ge("dateChanged", syncDate)),
                                    Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNull("dateRetired")))),
                    Restrictions.and(
                            Restrictions.and(Restrictions.isNotNull("dateRetired"), Restrictions.ge("dateRetired", syncDate)),
                            Restrictions.and(Restrictions.isNotNull("dateCreated"), Restrictions.isNotNull("dateChanged")))));
        }
        Criteria criteria  = criteriaform.createCriteria("formDefinition").add(Restrictions.ilike("name", name, MatchMode.ANYWHERE));
        return criteria.list();
    }
}
