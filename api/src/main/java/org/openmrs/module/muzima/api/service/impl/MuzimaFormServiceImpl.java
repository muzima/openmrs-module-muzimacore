package org.openmrs.module.muzima.api.service.impl;

import org.dom4j.DocumentException;
import org.javarosa.xform.parse.ValidationMessages;
import org.javarosa.xform.parse.XFormParser;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler.TagHandler;
import org.openmrs.module.muzima.model.CompositeEnketoResult;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.module.muzima.utils.HTMLConceptParser;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ModelXml2JsonTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2HTML5Transformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2JavarosaTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.XForm2Html5Transformer;
//import org.openmrs.module.muzima.htmlform2MuzimaTransform.MuzimaHtmlform;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransform;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MuzimaFormServiceImpl extends BaseOpenmrsService implements MuzimaFormService {
	
	private XForm2Html5Transformer html5Transformer;
	
	private ModelXml2JsonTransformer modelXml2JsonTransformer;
	
	private ODK2JavarosaTransformer odk2JavarosaTransformer;
	
	private ODK2HTML5Transformer odk2HTML5Transformer;
	
	private MuzimaFormDAO dao;
	
	private Htmlform2MuzimaTransform htmlform2MuzimaTransform;
	
	private static Map<String, TagHandler> handlers = new LinkedHashMap<String, TagHandler>();
	
	public MuzimaFormServiceImpl(MuzimaFormDAO dao, XForm2Html5Transformer html5Transformer,
	    ModelXml2JsonTransformer modelXml2JsonTransformer, ODK2JavarosaTransformer odk2JavarosaTransformer,
	    ODK2HTML5Transformer odk2HTML5Transformer, Htmlform2MuzimaTransform htmlform2MuzimaTransform) {
		this.dao = dao;
		this.html5Transformer = html5Transformer;
		this.modelXml2JsonTransformer = modelXml2JsonTransformer;
		this.odk2JavarosaTransformer = odk2JavarosaTransformer;
		this.odk2HTML5Transformer = odk2HTML5Transformer;
		this.htmlform2MuzimaTransform = htmlform2MuzimaTransform;
	}
	
	@Override
	public List<MuzimaForm> getAll() {
		return dao.getAll();
	}
	
	//TODO: Handle records which do not have a form in the forms table.
	@Override
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
	
	@Override
	public MuzimaForm create(String xformXml, String form, String discriminator) throws Exception {
		if (!isFormDefinitionExists(form)) {
			CompositeEnketoResult result = (CompositeEnketoResult) modelXml2JsonTransformer
			        .transform(html5Transformer.transform(xformXml).getResult());
			
			return save(new MuzimaForm(form, discriminator, result.getForm(), result.getModel(), result.getModelAsJson(),
			        null, Context.getFormService().getFormByUuid(form)));
		}
		throw new DocumentException("The file name already Exists !");
	}
	
	@Override
	public MuzimaForm update(String xformXml, String formUUID) throws Exception {
		if (isFormExists(formUUID)) {
			CompositeEnketoResult result = (CompositeEnketoResult) modelXml2JsonTransformer
			        .transform(html5Transformer.transform(xformXml).getResult());
			MuzimaForm retrievedForm = dao.getFormByUuid(formUUID);
			if (retrievedForm != null) {
				retrievedForm.setHtml(result.getForm());
				retrievedForm.setModelXml(result.getModel());
				retrievedForm.setModelJson(result.getModelAsJson());
			}
			return save(retrievedForm);
		} else {
			throw new DocumentException("Unable to update form with form definition !" + formUUID);
		}
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
	
	@Override
	public MuzimaForm importODK(String xformXml, String form, String discriminator) throws Exception {
		if (!isFormDefinitionExists(form)) {
			CompositeEnketoResult result = (CompositeEnketoResult) modelXml2JsonTransformer
			        .transform(odk2HTML5Transformer.transform(xformXml).getResult());
			return save(new MuzimaForm(form, discriminator, result.getForm(), result.getModel(), result.getModelAsJson(),
			        null, Context.getFormService().getFormByUuid(form)));
		}
		throw new DocumentException("The file name already Exists !");
	}
	
	@Override
	public MuzimaForm createHTMLForm(String html, String form, String discriminator) throws Exception {
		if (!isFormDefinitionExists(form)) {
			HTMLConceptParser parser = new HTMLConceptParser();
			String metaJson = parser.createConceptMetadata(parser.parse(html));
			return save(new MuzimaForm(form, discriminator, html, null, null, metaJson,
			        Context.getFormService().getFormByUuid(form)));
		}
		throw new DocumentException("The file name already Exists !");
	}
	
	@Override
	public MuzimaForm updateHTMLForm(String html, String formUUID) throws Exception {
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
		throw new DocumentException("Unable to update form with id !" + formUUID);
	}
	
	@Override
	public MuzimaForm save(MuzimaForm form) throws Exception {
		if (form.getFormDefinition() == null)
			form.setFormDefinition(Context.getFormService().getFormByUuid(form.getForm()));
		dao.saveForm(form);
		return form;
	}
	
	@Override
	public ValidationMessages validateJavaRosa(String xml) {
		return new XFormParser(new StringReader(xml)).validate();
	}
	
	@Override
	public ValidationMessages validateODK(String xml) throws Exception {
		String result = odk2JavarosaTransformer.transform(xml).getResult();
		return new XFormParser(new StringReader(result)).validate();
	}
	
	@Override
	public MuzimaForm getFormById(Integer id) {
		return dao.getFormById(id);
	}
	
	@Override
	public MuzimaForm getFormByUuid(String uuid) {
		return dao.getFormByUuid(uuid); //To change body of implemented methods use File | Settings | File Templates.
	}
	
	@Override
	public List<MuzimaForm> getFormByName(final String name, final Date syncDate) {
		return dao.getFormByName(name, syncDate);
	}
	
	@Override
	public List<MuzimaForm> getMuzimaFormByForm(String form, boolean includeRetired) {
		return dao.getMuzimaFormByForm(form, includeRetired);
	}
	
	@Override
	public TagHandler getHtmlformTagHandlerByTagName(String tagName) {
		return handlers.get(tagName);
	}
	
	@Override
	public Map<String, TagHandler> getHtmlformTagHandlers() {
		return handlers;
	}
	
	@Override
	public void addHtmlformTagHandler(String tagName, TagHandler handler) {
		handlers.put(tagName, handler);
	}
	
	@Override
	public Map<String, TagHandler> getHandlers() {
		return handlers;
	}
	
	public void setHandlers(Map<String, TagHandler> handlersToSet) {
		handlers.putAll(handlersToSet);
	}
	
	@Override
	public String convertHtmlformToMuzima(String htmlformXml) {
		htmlform2MuzimaTransform.setHtmlformXml(htmlformXml);
		htmlform2MuzimaTransform.transformHtmlToMuzima();
		String muzimaHtml = htmlform2MuzimaTransform.getMuzimaHtml();
		return muzimaHtml;
	}
	
	//	@Override
	//	public List<MuzimaHtmlform> getHtmlForms() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//	
	//	@Override
	//	public Number countHtmlForms(String search) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	//	
	//	@Override
	//	public List<MuzimaXForm> getPagedHtmlForms(String search, Integer pageNumber, Integer pageSize) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	
	//	@Override
	//	public MuzimaForm importHtmlForm(String htmlformXml, String form, String discriminator) throws Exception {
	//		if (!isFormDefinitionExists(form)) {
	//			htmlform2MuzimaTransform = new Htmlform2MuzimaTransform(htmlformXml);
	//			htmlform2MuzimaTransform.transformHtmlToMuzima();
	//			String muzimaHtml = htmlform2MuzimaTransform.getMuzimaHtml();
	//			//			CompositeEnketoResult result = (CompositeEnketoResult) modelXml2JsonTransformer
	//			//			        .transform(odk2HTML5Transformer.transform(xformXml).getResult());
	//			//			return save(new MuzimaForm(form, discriminator, result.getForm(), result.getModel(), result.getModelAsJson(),
	//			//			        null, Context.getFormService().getFormByUuid(form)));
	//			
	//			//			if (!isFormDefinitionExists(form)) {
	//			//				HTMLConceptParser parser = new HTMLConceptParser();
	//			//				String metaJson = parser.createConceptMetadata(parser.parse(html));
	//			//				return save(new MuzimaForm(form, discriminator, html, null, null, metaJson,
	//			//				        Context.getFormService().getFormByUuid(form)));
	//			//			}
	//		}
	//		throw new DocumentException("The file name already Exists !");
	//	}
	
}
