package org.openmrs.module.muzima.api.service;

import org.javarosa.xform.parse.ValidationMessages;
import org.openmrs.Form;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface MuzimaFormService extends OpenmrsService {
    @Transactional(readOnly = true)
    List<MuzimaForm> getAll();

    @Transactional(readOnly = true)
    List<MuzimaXForm> getXForms();

    @Transactional(readOnly = true)
    Number countXForms(String search);

    @Transactional(readOnly = true)
    List<MuzimaXForm> getPagedXForms(final String search, final Integer pageNumber, final Integer pageSize);

    MuzimaForm getFormById(Integer id);

    MuzimaForm getFormByUuid(String uuid);

    List<MuzimaForm> getFormByName(final String name, final Date syncDate);

    @Transactional
    List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired);

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

    ValidationMessages validateMuzimaForm(String html);

    List<Form> getNonMuzimaForms();

    List<Object[]> getFormCountGroupedByDiscriminator();

    ValidationMessages validateODK(String xml) throws Exception;
}
