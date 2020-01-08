package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.muzima.web.utils.WebConverter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;

@Controller
public class MuzimaHtmlFormEntryController {

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/htmlformentry.form", method = RequestMethod.GET)
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
    @RequestMapping(value = "module/muzimacore/htmlFormEntrymoduleStatus.form", method = RequestMethod.GET)
    public boolean htmlFormEntryModuleStatus() {
        return ModuleFactory.isModuleStarted("htmlformentry");
    }
}
