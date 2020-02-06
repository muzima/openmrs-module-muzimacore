package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;

@Controller
public class MuzimaHtmlFormEntryController {
	
	@ResponseBody
	@RequestMapping(value = "module/muzimacore/htmlFormEntry.form", method = RequestMethod.GET)
	public Map<String, Object> getHtmlForms() {
		Map<String, Object> response = new HashMap<String, Object>();
		List<Object> objects = new ArrayList<Object>();
		
		if (Context.isAuthenticated()) {
			HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
			List<HtmlForm> htmlForms = htmlFormEntryService.getAllHtmlForms();
			if (htmlForms != null && htmlForms.size() > 0) {
				for (HtmlForm form : htmlForms) {
					objects.add(WebConverter.convertHtmlForm(form));
				}
			}
			
			response.put("objects", objects);
		}
		
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "module/muzimacore/htmlFormEntry.form", method = RequestMethod.POST)
	public Map<String, Object> convertHtmlform(final @RequestParam Integer id, final @RequestParam String form)
	        throws Exception {
		Map<String, Object> response = new HashMap<String, Object>();
		String muzimaHtml;
		if (Context.isAuthenticated()) {
			MuzimaFormService service = Context.getService(MuzimaFormService.class);
			HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
			HtmlForm htmlForm = htmlFormEntryService.getHtmlForm(id);
			muzimaHtml = service.convertHtmlformToMuzima(htmlForm.getXmlData());
			
			String uuid = htmlForm.getUuid();
			String formId = id.toString();
			String name = htmlForm.getName();
			String discriminator = "json-registration";
			String description = htmlForm.getDescription();
			
			response.put("uuid", uuid);
			response.put("id", formId);
			response.put("name", name);
			response.put("discriminator", discriminator);
			response.put("description", description);
			response.put("html", muzimaHtml);
		}
		return response;
	}
	
	@ResponseBody
	@RequestMapping(value = "module/muzimacore/htmlFormEntryModuleStatus.form", method = RequestMethod.GET)
	public boolean htmlFormEntryModuleStatus() {
		return ModuleFactory.isModuleStarted("htmlformentry");
	}
}
