package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "module/muzimacore/xforms.form")

public class MuzimaXFormsController {
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<MuzimaXForm> xForms() {
        List<MuzimaXForm> xForms = new ArrayList<MuzimaXForm>();
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            xForms = service.getXForms();
        }
        return xForms;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void importXForm(final @RequestParam Integer id,
                            final @RequestParam String form,
                            final @RequestParam String discriminator) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            service.importExisting(id, form, discriminator);
        }
    }
}