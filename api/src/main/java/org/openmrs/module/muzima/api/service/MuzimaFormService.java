package org.openmrs.module.muzima.api.service;

import org.javarosa.xform.parse.ValidationMessages;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.MuzimaForm;
import org.openmrs.module.muzima.MuzimaXForm;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface MuzimaFormService extends OpenmrsService {
    @Transactional(readOnly = true)
    List<MuzimaForm> getAll();

    @Transactional(readOnly = true)
    List<MuzimaXForm> getXForms();

    @Transactional
    MuzimaForm importExisting(Integer xFormId, String form, String discriminator) throws Exception;

    MuzimaForm findById(Integer id);

    MuzimaForm findByUniqueId(String uuid);

    List<MuzimaForm> findByName(final String name, final Date syncDate);

    @Transactional
    MuzimaForm create(String xformXml, String form,  String discriminator) throws Exception;

    @Transactional
    MuzimaForm update(String html, String form) throws Exception;

    @Transactional
    MuzimaForm importODK(String xformXml, String form, String discriminator) throws Exception;

    @Transactional
    MuzimaForm createHTMLForm(String html, String form, String discriminator) throws Exception;

    @Transactional
    MuzimaForm updateHTMLForm(String html,  String form) throws Exception;

    @Transactional
    MuzimaForm save(MuzimaForm form) throws Exception;

    ValidationMessages validateJavaRosa(String xml);

    ValidationMessages validateODK(String xml) throws Exception;
}
