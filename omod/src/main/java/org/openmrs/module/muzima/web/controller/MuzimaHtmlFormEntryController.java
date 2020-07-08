package org.openmrs.module.muzima.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public Map<String, String> convertHtmlForms(final @RequestParam(value = "id") Integer id) {
		Map<String, String> response = new HashMap<String, String>();
		String uuid;
		String formId;
		String name;
		String discriminator = "json-encounter";
		String description;
		String html;
		
        if (Context.isAuthenticated()) {
			
			HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
			FormService formService = Context.getFormService();
			
			Form form = formService.getForm(id);
			HtmlForm htmlForm = htmlFormEntryService.getHtmlFormByForm(form);
			String htmlFormXmlString = htmlForm.getXmlData();
			formId = id.toString();
			//this is now Form UUID, name and description
			uuid = form.getUuid();
			name = form.getName();
			description = form.getDescription();
			
			MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
			html = muzimaFormService.convertHtmlformToMuzima(htmlFormXmlString, name);
			
			response.put("uuid", uuid);
			response.put("id", formId);
			response.put("name", name);
			response.put("discriminator", discriminator);
			response.put("description", description);
			response.put("html", html);

        }

        return response;
    }
	
	@ResponseBody
	@RequestMapping(value = "module/muzimacore/htmlFormEntrySaveConvertedForm.form", method = RequestMethod.POST)
	public void saveConvertedForm(final @RequestBody Map<String, Object> data) throws Exception {
		if (Context.isAuthenticated()) {
			String formUUID = (String) data.get("uuid");
			String discriminator = (String) data.get("discriminator");
			String html = (String) data.get("html");
			MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
			muzimaFormService.saveConvertedForm(html, formUUID, discriminator);
		}
	}

	@ResponseBody
	@RequestMapping(value = "module/muzimacore/htmlFormEntryUpdateConvertedForm.form", method = RequestMethod.POST)
	public void updateConvertedForm(final @RequestBody Map<String, Object> data) throws Exception {
		if (Context.isAuthenticated()) {
			String formUUID = (String) data.get("uuid");
			String discriminator = (String) data.get("discriminator");
			String html = (String) data.get("html");
			MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
			muzimaFormService.updateConvertedForm(html, formUUID, discriminator);
		}
	}

	@ResponseBody
    @RequestMapping(value = "module/muzimacore/htmlFormEntryModuleStatus.form", method = RequestMethod.GET)
    public boolean htmlFormEntryModuleStatus() {
        return ModuleFactory.isModuleStarted("htmlformentry");
    }
}
