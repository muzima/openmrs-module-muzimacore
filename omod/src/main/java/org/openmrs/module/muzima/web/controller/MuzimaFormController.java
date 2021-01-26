package org.openmrs.module.muzima.web.controller;

import org.javarosa.xform.parse.ValidationMessages;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Controller
@RequestMapping(value = "module/muzimacore")
public class MuzimaFormController {

    //TODO: Use MuzimaFormResource to handle the save
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "form.form")
    public void save(final @RequestBody MuzimaForm form) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            service.save(form);
        }
    }

    @ResponseBody
    @RequestMapping(value = "validateMuzimaForm.form", method = RequestMethod.POST)
    public ValidationMessages validate(final MultipartHttpServletRequest request) throws Exception {
        MuzimaFormService service = Context.getService(MuzimaFormService.class);
        return service.validateMuzimaForm(extractFile(request));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "retire/{formId}.form")
    public void retire(final @PathVariable Integer formId, final @RequestParam String retireReason) throws Exception {
        if (Context.isAuthenticated()) {
            MuzimaFormService service = Context.getService(MuzimaFormService.class);
            MuzimaForm form = service.getFormById(formId);
            form.setRetired(true);
            if (isNotBlank(retireReason)) {
                form.setRetireReason(retireReason);
            }
            form.setRetiredBy(Context.getAuthenticatedUser());
            form.setDateRetired(new Date());
            service.save(form);
        }
    }

    @ResponseBody
    @RequestMapping(value = "nonMuzimaForms.json", method = RequestMethod.GET)
    public List<Form> getNonMuzimaForms() throws Exception {
        MuzimaFormService service = Context.getService(MuzimaFormService.class);
        List<Form> forms = service.getNonMuzimaForms();
        return forms;
    }

    private String extractFile(final MultipartHttpServletRequest request) throws Exception {
        MultipartFile file = request.getFile("file");
        return readStream(file.getInputStream());
    }

    private String readStream(final InputStream stream) throws IOException {
        return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }
}