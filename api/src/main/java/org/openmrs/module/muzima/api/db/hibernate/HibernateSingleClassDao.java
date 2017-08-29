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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.openmrs.module.muzima.api.db.SingleClassDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
public class HibernateSingleClassDao<T> implements SingleClassDao<T> {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Class<T> mappedClass;

    /**
     * Marked private because you *must* provide the class at runtime when instantiating one of
     * these, using the next constructor
     */
    @SuppressWarnings("unused")
    private HibernateSingleClassDao() {
    }

    /**
     * You must call this before using any of the data access methods, since it's not actually
     * possible to write them all with compile-time class information.
     *
     * @param mappedClass
     */
    protected HibernateSingleClassDao(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public T getById(Integer id) {
        return (T) sessionFactory.getCurrentSession().get(mappedClass, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<T> getAll() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
        return (List<T>) criteria.list();
    }

    @Override
    @Transactional
    public T saveOrUpdate(T object) {
            sessionFactory.getCurrentSession().saveOrUpdate(object);
        return object;
    }

    @Override
    @Transactional
    public T update(T object) {
        sessionFactory.getCurrentSession().update(object);
        return object;
    }

    @Override
    @Transactional
    public void delete(T object) {
        sessionFactory.getCurrentSession().delete(object);
    }
}
