package org.openmrs.module.muzima.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;

import java.util.Date;
import java.util.List;

public class HibernateMuzimaFormDAO implements MuzimaFormDAO {
    private SessionFactory factory;

    public HibernateMuzimaFormDAO(SessionFactory factory) {
        this.factory = factory;
    }

    private Session session() {
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
