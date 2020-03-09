package org.openmrs.module.muzima.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.javarosa.xform.parse.ValidationMessages;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler.TagHandler;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.springframework.transaction.annotation.Transactional;

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
	MuzimaForm create(String xformXml, String form, String discriminator) throws Exception;
	
	@Transactional
	MuzimaForm update(String html, String form) throws Exception;
	
	@Transactional
	MuzimaForm importODK(String xformXml, String form, String discriminator) throws Exception;
	
	@Transactional
	MuzimaForm createHTMLForm(String html, String form, String discriminator) throws Exception;
	
	@Transactional
	MuzimaForm updateHTMLForm(String html, String form) throws Exception;
	
	@Transactional
	MuzimaForm save(MuzimaForm form) throws Exception;
	
	ValidationMessages validateJavaRosa(String xml);
	
	ValidationMessages validateODK(String xml) throws Exception;
	
	/**
	 * Get a tag handler by tag name
	 * 
	 * @param tagName the tag name
	 * @return the tag handler associated with the tag name
	 */
	@Transactional(readOnly = true)
	public TagHandler getHtmlformTagHandlerByTagName(String tagName);
	
	/**
	 * Returns a map of all tag handlers
	 * 
	 * @return a map of all tag handlers
	 */
	@Transactional(readOnly = true)
	public Map<String, TagHandler> getHtmlformTagHandlers();
	
	Map<String, TagHandler> getHandlers();
	
	void addHtmlformTagHandler(String tagName, TagHandler handler);
	
	@Transactional
	String convertHtmlformToMuzima(String htmlformXml, String formName);
	
	@Transactional
	MuzimaForm saveConvertedForm(String html, String formUUID, String discriminator) throws Exception;
	
}
