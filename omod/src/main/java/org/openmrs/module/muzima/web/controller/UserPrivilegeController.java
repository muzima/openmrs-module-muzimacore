package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserPrivilegeController {

    @RequestMapping(value = "/module/muzimacore/checkViewLocationPrivilege.json", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkAddLocationPrivilege(){
        return Context.getUserContext().hasPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
    }

    @RequestMapping(value = "/module/muzimacore/checkManageProviderPrivilege.json", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkManageProviderPrivilege(){
        return Context.getUserContext().hasPrivilege(PrivilegeConstants.MANAGE_PROVIDERS);
    }

    @RequestMapping(value = "/module/muzimacore/checkManageFormsPrivilege.json", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkManageFormsPrivilege(){
        return Context.getUserContext().hasPrivilege(PrivilegeConstants.MANAGE_FORMS);
    }

    @RequestMapping(value = "/module/muzimacore/checkAddCohortsPrivilege.json", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkAddCohortsPrivilege(){
        return Context.getUserContext().hasPrivilege(PrivilegeConstants.ADD_COHORTS);
    }
}
