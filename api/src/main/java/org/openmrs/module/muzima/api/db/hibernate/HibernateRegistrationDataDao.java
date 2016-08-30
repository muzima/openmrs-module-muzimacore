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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.muzima.api.db.RegistrationDataDao;
import org.openmrs.module.muzima.api.db.hibernate.HibernateSingleClassDao;
import org.openmrs.module.muzima.model.RegistrationData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * It is a default implementation of  {@link org.openmrs.module.muzima.api.db.RegistrationDataDao}.
 */
public class HibernateRegistrationDataDao extends HibernateSingleClassDao<RegistrationData> implements RegistrationDataDao {

    private final Log log = LogFactory.getLog(this.getClass());

    public HibernateRegistrationDataDao() {
        super(RegistrationData.class);
    }

    /**
     * @return the sessionFactory
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Get registration data by the internal database id of the registration data.
     *
     * @param id the internal database id.
     * @return the registration data with matching internal database id.
     */
    @Override
    public RegistrationData getRegistrationDataById(final Integer id) {
        return getById(id);
    }

    /**
     * Get registration data by the uuid of the registration data.
     *
     * @param uuid the uuid of the registration data.
     * @return the registration data with matching uuid.
     */
    @Override
    public RegistrationData getRegistrationDataByUuid(final String uuid) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("uuid", uuid));
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return (RegistrationData) criteria.uniqueResult();
    }

    /**
     * Get registration data based on the temporary uuid assigned to a patient created through the registration form
     * and / or the real uuid of the patient data created after processing the registration form (for new patient) or
     * the real uuid of the existing patient (for existing patient).
     *
     * @param temporaryUuid the temporary uuid assigned to a patient.
     * @param assignedUuid  the real uuid of a newly created patient or the real uuid of an existing patient.
     * @return the registration data based on the temporary uuid and / or the assigned uuid.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<RegistrationData> getRegistrationData(final String temporaryUuid, final String assignedUuid) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (!StringUtils.isBlank(temporaryUuid)) {
            criteria.add(Restrictions.eq("temporaryUuid", temporaryUuid));
        }
        if (!StringUtils.isBlank(assignedUuid)) {
            criteria.add(Restrictions.eq("assignedUuid", assignedUuid));
        }
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return criteria.list();
    }

    /**
     * Create a new registration data entry in the database.
     *
     * @param registrationData the registration data to be created.
     * @return the new registration data.
     */
    @Override
    @Transactional
    public RegistrationData saveRegistrationData(final RegistrationData registrationData) {
        return saveOrUpdate(registrationData);
    }

    /**
     * Delete a registration data.
     *
     * @param registrationData the registration data to be deleted.
     */
    @Override
    @Transactional
    public void deleteRegistrationData(final RegistrationData registrationData) {
        delete(registrationData);
    }

    /**
     * Get all registration data information from the database.
     *
     * @param pageNumber the page number.
     * @param pageSize   the page size.
     * @return all registration data in the database.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<RegistrationData> getRegistrationData(final Integer pageNumber, final Integer pageSize) {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        if (pageNumber != null) {
            criteria.setFirstResult((pageNumber - 1) * pageSize);
        }
        if (pageSize != null) {
            criteria.setMaxResults(pageSize);
        }
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        return criteria.list();
    }

    /**
     * Count the number of registration data in the database.
     * @return the number of registration data in the database.
     */
    public Number countRegistrationData() {
        Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(mappedClass);
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));
        criteria.setProjection(Projections.rowCount());
        return (Number) criteria.uniqueResult();
    }
}