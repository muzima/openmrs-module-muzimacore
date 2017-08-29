package org.openmrs.module.muzima.web.controller;/*
 * Copyright (c) 2014. The Trustees of Indiana University.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license with additional
 * healthcare disclaimer. If the user is an entity intending to commercialize any application
 * that uses this code in a for-profit venture, please contact the copyright holder.
 */

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/module/muzimacore/discriminator.json")
public class DiscriminatorController {
        @RequestMapping(method = RequestMethod.GET)
        @ResponseBody
        public List<String> getDiscriminatorTypes(){
            List<String> discriminatorTypes = null;
            if (Context.isAuthenticated()) {
                DataService dataService = Context.getService(DataService.class);
                discriminatorTypes = dataService.getDiscriminatorTypes();
                Collections.sort(discriminatorTypes);
            }
            return discriminatorTypes;
        }
    }
