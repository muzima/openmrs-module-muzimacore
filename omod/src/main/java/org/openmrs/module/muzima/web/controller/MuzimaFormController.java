package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.MuzimaForm;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Controller
@RequestMapping(value = "module/muzimacore")
public class MuzimaFormController {

    //TODO: Use MuzimaFormResource to handle the save
    @RequestMapping(method = RequestMethod.POST, value = "form.form")
    @ResponseBody
    public void save(final @RequestBody MuzimaForm form) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            service.save(form);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "retire/{formId}.form")
    @ResponseBody
    public void retire(final @PathVariable Integer formId, final @RequestParam String retireReason) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            MuzimaForm form = service.findById(formId);
            form.setRetired(true);
            if (isNotBlank(retireReason)) {
                form.setRetireReason(retireReason);
            }
            form.setRetiredBy(Context.getAuthenticatedUser());
            form.setDateRetired(new Date());
            service.save(form);
        }
    }

}
