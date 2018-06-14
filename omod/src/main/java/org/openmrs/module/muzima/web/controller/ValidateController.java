package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.ErrorMessage;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shwethathammaiah on 07/04/15.
 */
@Controller
@RequestMapping(value = "/module/muzimacore/validate.json")
public class ValidateController {
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> validate(final @RequestParam(value = "uuid") String uuid,
                                       final @RequestBody String formData){
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            errorMessages = dataService.validateData(uuid, formData);
        }
        return WebConverter.convertErrorMessages(errorMessages);
    }
}
