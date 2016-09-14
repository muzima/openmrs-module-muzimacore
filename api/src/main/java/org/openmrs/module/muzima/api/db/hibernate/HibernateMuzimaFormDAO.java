package org.openmrs.module.muzima.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.muzima.MuzimaForm;
import org.openmrs.module.muzima.MuzimaXForm;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.xforms.Xform;

import java.util.Date;
import java.util.List;

public class HibernateMuzimaFormDAO implements MuzimaFormDAO {
    private SessionFactory factory;

    public HibernateMuzimaFormDAO(SessionFactory factory) {
        this.factory = factory;
    }

    public List<MuzimaForm> getAll() {
        Criteria criteria = session().createCriteria(MuzimaForm.class);
        criteria.add(Restrictions.eq("retired", false));
        return criteria.list();

    }

    //TODO: Move this to a named query
    public List<MuzimaXForm> getXForms() {
        return (List<MuzimaXForm>) session().createCriteria(MuzimaXForm.class).list();
    }

    public void saveForm(MuzimaForm form) {
        session().saveOrUpdate(form);
    }

    public MuzimaForm findById(Integer id) {
        return (MuzimaForm) session().get(MuzimaForm.class, id);
    }

    public Xform getXform(int id) {
        return (Xform) session().get(Xform.class, id);
    }

    public MuzimaForm findByUuid(String uuid) {
        return (MuzimaForm) session().createQuery("from MuzimaForm form where form.uuid = '" + uuid + "'").uniqueResult();
    }
    public List<MuzimaForm> findByForm(String form){
        return (List<MuzimaForm>) session().createQuery("from MuzimaForm form where form.form = '" + form + "'").list();
    }

    public List<MuzimaForm> findByName(final String name, final Date syncDate) {
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

    private Session session() {
        return factory.getCurrentSession();
    }
}
