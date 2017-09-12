package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.web.utils.WebConverter;
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
@RequestMapping(value = "/module/muzimacore/settings.json")
public class MuzimaSettingsController {
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSettings(final @RequestParam(value = "search") String search,
                                          final @RequestParam(value = "pageNumber") Integer pageNumber,
                                          final @RequestParam(value = "pageSize") Integer pageSize) {

        return null;
    }
}
