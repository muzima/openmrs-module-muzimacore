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
    public Map<String, Object> convertHtmlForms(final @RequestParam(value = "id") Integer id) {
        Map<String, Object> response = new HashMap<String, Object>();
        // some dummy values for the dummy response
		String uuid; //= "e72c4bd8-6329-4eb4-b0bd-1c4729ada98e";
		String formId;// = "10";
		String name;// = "mUzima Registration Form";
		String discriminator = "json-encounter";
		String description;// = "mUzima Registration Form for testing purposes";
		String html;
		
        if (Context.isAuthenticated()) {

			
			HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
			Form form = Context.getService(FormService.class).getForm(id);
			HtmlForm htmlForm = htmlFormEntryService.getHtmlFormByForm(form);
			String htmlFormXmlString = htmlForm.getXmlData();
			formId = id.toString();
			uuid = htmlForm.getUuid();
			name = htmlForm.getName();
			description = htmlForm.getDescription();
			
			MuzimaFormService formService = Context.getService(MuzimaFormService.class);
			html = formService.convertHtmlformToMuzima(htmlFormXmlString);
			
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
    @RequestMapping(value = "module/muzimacore/htmlFormEntryModuleStatus.form", method = RequestMethod.GET)
    public boolean htmlFormEntryModuleStatus() {
        return ModuleFactory.isModuleStarted("htmlformentry");
    }
}
