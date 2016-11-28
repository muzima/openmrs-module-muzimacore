package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.module.xforms.Xform;
import org.openmrs.module.xforms.XformsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MuzimaXFormsController {

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/xforms.form", method = RequestMethod.GET)
    public Map<String, Object> getXForms(final @RequestParam(value = "search") String search,
                                         final @RequestParam(value = "pageNumber") Integer pageNumber,
                                         final @RequestParam(value = "pageSize") Integer pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            MuzimaFormService formService = Context.getService(MuzimaFormService.class);

            List<MuzimaXForm> xforms = formService.getPagedXForms(search, pageNumber, pageSize);
            response.put("totalItems", formService.countXForms(search).intValue());
            response.put("objects", xforms);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/moduleStatus.form", method = RequestMethod.GET)
    public boolean xFormsModuleStatus() {
        return ModuleFactory.isModuleStarted("xforms");
    }

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/xforms.form", method = RequestMethod.POST)
    public void importXForm(final @RequestParam Integer id,
                            final @RequestParam String form,
                            final @RequestParam String discriminator) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            XformsService xformsService = Context.getService(XformsService.class);
            Xform xform = xformsService.getXform(id);

            service.create(xform.getXformXml(), form,  discriminator);
        }
    }
}