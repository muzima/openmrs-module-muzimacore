package org.openmrs.module.muzima.api.service.impl;

import org.javarosa.xform.parse.ValidationMessages;
import org.javarosa.xform.parse.XFormParser;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.module.muzima.utils.HTMLConceptParser;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

public class MuzimaFormServiceImpl extends BaseOpenmrsService implements MuzimaFormService {
    private MuzimaFormDAO dao;

    public MuzimaFormServiceImpl(MuzimaFormDAO dao) {
        this.dao = dao;
    }

    public List<MuzimaForm> getAll() {
        return dao.getAll();
    }

    //TODO: Handle records which do not have a form in the forms table.
    public List<MuzimaXForm> getXForms() {
        return dao.getXForms();
    }

    @Override
    public Number countXForms(String search) {
        return dao.countXForms(search);
    }

    @Override
    public List<MuzimaXForm> getPagedXForms(String search, Integer pageNumber, Integer pageSize) {
        return dao.getPagedXForms(search, pageNumber, pageSize);
    }

    private boolean isFormExists(String formUUID) {
        MuzimaForm muzimaforms = dao.getFormByUuid(formUUID);
        return muzimaforms != null;
    }

    private boolean isFormDefinitionExists(String formUUID) {
        List<MuzimaForm> formsWithUUID = getMuzimaFormByForm(formUUID, false);
        for (MuzimaForm form : formsWithUUID) {
            if (form.getForm().equals(formUUID)) {
                return true;
            }
        }
        return false;
    }

    public MuzimaForm createHTMLForm(String html,  String form,  String discriminator) throws Exception {
        if (!isFormDefinitionExists(form)) {
            HTMLConceptParser parser = new HTMLConceptParser();
            String metaJson = parser.createConceptMetadata(parser.parse(html));
            return save(new MuzimaForm(form, discriminator, html, null, null,metaJson, Context.getFormService().getFormByUuid(form)));
        }
        throw new Exception("The file name already Exists !");
    }

    public MuzimaForm updateHTMLForm(String html,  String formUUID) throws Exception {
        if (isFormExists(formUUID)) {
            MuzimaForm retrievedForm = dao.getFormByUuid(formUUID);
            if (retrievedForm != null) {
                HTMLConceptParser parser = new HTMLConceptParser();
                String metaJson = parser.createConceptMetadata(parser.parse(html));
                retrievedForm.setMetaJson(metaJson);
                retrievedForm.setHtml(html);
                return save(retrievedForm);
            }
        }
        throw new Exception("Unable to update form with id !" + formUUID);
    }

    public MuzimaForm save(MuzimaForm form) throws Exception {
        if(form.getFormDefinition() == null)
            form.setFormDefinition(Context.getFormService().getFormByUuid(form.getForm()));
        dao.saveForm(form);
        return form;
    }

    public ValidationMessages validateJavaRosa(String xml) {
        return new XFormParser(new StringReader(xml)).validate();
    }

    public ValidationMessages validateMuzimaForm(String html) {
        HTMLConceptParser parser = new HTMLConceptParser();

        return parser.validateForm(html);
    }


    public MuzimaForm getFormById(Integer id) {
        return dao.getFormById(id);
    }

    public MuzimaForm getFormByUuid(String uuid) {
        return dao.getFormByUuid(uuid);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<MuzimaForm> getFormByName(final String name, final Date syncDate) {
        return dao.getFormByName(name, syncDate);
    }

    public List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired){
        return dao.getMuzimaFormByForm(form, includeRetired);
    }

    public List<Form> getNonMuzimaForms(String search){
        return dao.getNonMuzimaForms(search);
    }

    @Override
    public List<Object[]> getFormCountGroupedByDiscriminator() {
        return dao.getFormCountGroupedByDiscriminator();
    }
}