package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.MuzimaFormTag;
import org.openmrs.module.muzima.api.service.MuzimaTagService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = "module/muzimacore/tags.form")

public class TagController {
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<MuzimaFormTag> tags() {
        List<MuzimaFormTag> tags = new ArrayList<MuzimaFormTag>();
        if (Context.isAuthenticated()) {
            MuzimaTagService service = Context.getService(MuzimaTagService.class);
            tags = service.getAll();
        }
        return tags;
    }

}
