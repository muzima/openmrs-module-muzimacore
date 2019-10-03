package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.DataSource;
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
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
            int pages = (settingService.countMuzimaSettings(search, null).intValue() + pageSize - 1) / pageSize;
            List<Object> objects = new ArrayList<Object>();
            for (MuzimaSetting setting : settingService.getPagedSettings(search, null, pageNumber, pageSize)) {
                objects.add(WebConverter.convertMuzimaSetting(setting));
            }
            response.put("pages", pages);
            response.put("totalItems", settingService.countMuzimaSettings(search, null).intValue());
            response.put("objects", objects);
        }
        return response;
    }
}
