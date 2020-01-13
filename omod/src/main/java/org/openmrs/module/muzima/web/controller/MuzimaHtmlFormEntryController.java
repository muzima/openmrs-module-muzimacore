package org.openmrs.module.muzima.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.muzima.web.utils.WebConverter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;

@Controller
public class MuzimaHtmlFormEntryController {

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/htmlFormEntry.form", method = RequestMethod.GET)
    public Map<String, Object> getHtmlForms() {
        Map<String, Object> response = new HashMap<String, Object>();
        List<Object> objects = new ArrayList<Object>();
        
        if (Context.isAuthenticated()) {
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
            List<HtmlForm> htmlForms = htmlFormEntryService.getAllHtmlForms();
            if (htmlForms != null && htmlForms.size() > 0) {
                for (HtmlForm form : htmlForms) {
                    objects.add(WebConverter.convertHtmlForm(form));
                }
            }

            response.put("objects", objects);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/htmlFormEntry.form", method = RequestMethod.POST)
    public Map<String, Object> convertHtmlForms(final @RequestParam(value = "id") Integer id) {
        Map<String, Object> response = new HashMap<String, Object>();
        // some dummy values for the dummy response
        String uuid = "e72c4bd8-6329-4eb4-b0bd-1c4729ada98e";
        String formId = "10";
        String name = "mUzima Registration Form";
        String discriminator = "json-registration";
        String description = "mUzima Registration Form for testing purposes";
        String html = "<html>\n\n<head>\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n    <link href=\"css/bootstrap.min.css\" rel=\"stylesheet\">\n    <link href=\"css/muzima.css\" rel=\"stylesheet\">\n    <link href=\"css/ui-darkness/jquery-ui-1.10.4.custom.min.css\" rel=\"stylesheet\">\n    <script src=\"js/jquery.min.js\"></script>\n    <script src=\"js/jquery-ui-1.10.4.custom.min.js\"></script>\n    <script src=\"js/jquery.validate.min.js\"></script>\n    <script src=\"js/additional-methods.min.js\"></script>\n    <script src=\"js/muzima.js\"></script>\n\n    <title>Basic Registration Form</title>\n</head>\n\n<body class=\"col-md-10 col-md-offset-1\">\n    <div id=\"result\"></div>\n    <form id=\"basic_registration_form\" name=\"basic_registration_form\">\n    <div id=\"pre_populate_data\"></div>\n    <h2 class=\"text-center\">Basic Registration Form</h2>\n\n        <div class=\"section\">\n            <h3>Demographics</h3>\n\n            <div class=\"form-group\">\n            <input class=\"form-control\" id=\"patient.uuid\"\n                   name=\"patient.uuid\" type=\"hidden\" readonly=\"readonly\">\n</div>\n            <div class=\"form-group\">\n                <label for=\"patient.family_name\">Family Name: <span class=\"required\">*</span> </label>\n                <input class=\"form-control\" id=\"patient.family_name\" name=\"patient.family_name\" type=\"text\"\n                       required=\"required\">\n            </div>\n            <div class=\"form-group\">\n                <label for=\"patient.given_name\">Given Name: <span class=\"required\">*</span></label>\n                <input class=\"form-control\" id=\"patient.given_name\" name=\"patient.given_name\" type=\"text\"\n                       required=\"required\">\n            </div>\n            <div class=\"form-group\">\n                <label for=\"patient.middle_name\">Middle Name:</label>\n                <input class=\"form-control\" id=\"patient.middle_name\" name=\"patient.middle_name\" type=\"text\">\n            </div>\n            <div class=\"form-group\">\n                <label for=\"patient.sex\">Gender: <span class=\"required\">*</span></label>\n                <select class=\"form-control\" id=\"patient.sex\" name=\"patient.sex\" required=\"required\">\n                    <option value=\"\">...</option>\n                    <option value=\"M\">Male</option>\n                    <option value=\"F\">Female</option>\n</select>\n            </div>\n            <div class=\"form-group\">\n                <label for=\"patient.birth_date\">Date Of Birth: <span class=\"required\">*</span></label>\n                <input class=\"form-control datepicker past-date\" id=\"patient.birth_date\" name=\"patient.birth_date\" type=\"text\" required=\"required\">\n            </div>\n<div class=\"form-group\">\n                <label for=\"patient.medical_record_number\">Medical Record Number : <span class=\"required require_medical_record_number_hint\">*</span></label>\n\n                <div class=\"form-horizontal\">\n                    <div class=\"group-set\" data-group=\"patient.medical_record_number\">\n                      <input type=\"button\" class='btn barcode_btn'>\n                      <input class=\"barcode_text form-control\" id=\"identifier_value\"\n                           name=\"identifier_value\" type=\"text\">\n<input data-metadata-for=\"identifier_value\" type=\"hidden\" id=\"identifier_type_uuid\" name=\"identifier_type_uuid\" value=\"8d793bee-c2cc-11de-8d13-0010c6dffd0f\">\n</div>\n                </div>\n            </div>\n        </div>\n        <div class=\"section\">\n            <h3>Encounter Details</h3>\n\n            <div class=\"form-group show_provider_id_text\">\n                <label for=\"encounter.provider_id\">Provider's ID: <span class=\"required\">*</span></label>\n                <input class=\"form-control checkDigit\" id=\"encounter.provider_id\" name=\"encounter.provider_id\"\n                   type=\"text\" required=\"required\">\n            </div>\n            <div class=\"form-group\">\n<label for=\"encounter.location_id\">Encounter Location: <span class=\"required\">*</span></label>\n                <input class=\"form-control valid-location-only\" id=\"encounter.location_id\" type=\"text\" placeholder=\"Start typing something...\" required=\"required\">\n                <input class=\"form-control\" name=\"encounter.location_id\" type=\"hidden\">\n</div>\n\n            <div class=\"form-group hidden\">\n                <label for=\"encounter.location_id_select\">Encounter Location: <span class=\"required\">*</span></label>\n<select class=\"form-control\" id=\"encounter.location_id_select\" required=\"required\">\n                    <option>...</option>\n                </select>\n            </div>\n\n            <div class=\"form-group\">\n                <label for=\"encounter.encounter_datetime\">Encounter Date <span class=\"required\">*</span></label>\n                <input class=\"form-control nonFutureDate past-date datepicker\" readonly=\"readonly\" id=\"encounter.encounter_datetime\"\n                       name=\"encounter.encounter_datetime\" type=\"text\" required=\"required\">\n            </div>\n\n            <div class=\"form-group show_uuid_text\">\n                <label for=\"encounter.form_uuid\">Form uuid: <span class=\"required\">*</span></label>\n                <input class=\"form-control\" id=\"encounter.form_uuid\" name=\"encounter.form_uuid\"\n                       type=\"text\" required=\"required\">\n            </div>\n        </div>\n\n        <div class=\"section\">\n            <h3>Additional Details</h3>\n\n            <div class=\"section repeat\" data-group=\"patient.personattribute\">\n<h4>Attributes</h4>\n              <div class=\"form-group group-set other_identifier_type\" id=\"other_identifier_type\">\n                  <label for=\"attribute_type_uuid\">Attribute Type</label>\n                  <select class=\"form-control attribute_type_uuid\" name=\"attribute_type_uuid\"  id=\"attribute_type_uuid\">\n                      <option value=\"\">...</option>\n                      <option value=\"8d871d18-c2cc-11de-8d13-0010c6dffd0f\">Mother's Name</option>\n                      <option value=\"8037ba06-fc79-4244-9d14-687baa44bd81\">Contact Phone Number</option>\n                  </select>\n              </div>\n              <div class=\"form-group phone_number\">\n                <label for=\"patient.phone_number\">Attribute Value: </label>\n                <input class=\"form-control phoneNumber\" id=\"patient.phone_number\" name=\"attribute_value\" type=\"tel\">\n              </div>\n              <div class=\"form-group mothers_name\">\n                <label for=\"patient.mothers_name\">Attribute Value: </label>\n                <input class=\"form-control mothers_name\" id=\"patient.mothers_name\" name=\"attribute_value\" type=\"text\">\n              </div>\n            </div>\n            <div class=\"section group-set repeat\" data-group=\"patient.personaddress\">\n<h4>Address</h4>\n              <div class=\"form-group\">\n                  <label for=\"patient.county\">County: </label>\n                  <input class=\"form-control\" id=\"countyDistrict\" name=\"countyDistrict\" type=\"text\">\n              </div>\n              <div class=\"form-group\">\n                  <label for=\"patient.location\">Location: </label>\n<input class=\"form-control\" id=\"address6\" name=\"address6\" type=\"text\">\n              </div>\n              <div class=\"form-group\">\n                  <label for=\"patient.sub_location\">Sub-location: </label>\n                  <input class=\"form-control\" id=\"address5\" name=\"address5\" type=\"text\">\n              </div>\n              <div class=\"form-group\">\n                  <label for=\"patient.village\">Village: </label>\n                  <input class=\"form-control\" id=\"cityVillage\" name=\"cityVillage\" type=\"text\">\n</div>\n            </div>\n            <div class=\"section group-set repeat\" data-group=\"patient.other_medical_record_numbers\">\n              <h4>Other Medical Record Number</h4>\n              <div class=\"form-group\">\n                  <label for=\"patient.other_amrs\">AMRS Medical Record Number: </label>\n                  <input class=\"form-control checkDigit\" id=\"other_medical_record_number\" name=\"other_medical_record_number\" type=\"text\">\n              </div>\n            </div>            \n        </div>\n    </form>\n</body>\n\n<script type=\"text/javascript\">\n\n$(document).ready(function () {\n\n    $('#save_draft').click(function () {\n        $(this).prop('disabled', true);\n        document.saveDraft(this);\n        $(this).prop('disabled', false);\n    });\n\n    $('#submit_form').click(function () {\n        $(this).prop('disabled', true);\n        document.submit();\n        $(this).prop('disabled', false);\n    });\n\n    $('#basic_registration_form').validate({\n\n        submitHandler: function (form) {\n            $('#result').html(JSON.stringify($('form').serializeEncounterForm(), undefined, 2));\n        }\n    });\n\n    var show_message=function(element_id){\n        $(element_id).show();\n    }\n    var hide_message=function(element_id){\n        $(element_id).hide();\n    }\n\n    document.setupAutoCompleteData('encounter.location_id');\n\n    document.setupAutoCompleteDataForProvider('encounter.provider_id_select');\n\n    document.setupValidationForProvider(\"$('#encounter.provider_id_select').val()\",\"encounter.provider_id\");\n\n    document.setupValidationForLocation(\"$('#encounter.location_id').val()\",\"encounter.location_id\");\n\n});\n</script>\n\n</html>";
        
        if (Context.isAuthenticated()) {
            // TODO: Call convert function from htmlFormEntryService
            
            response.put("uuid", uuid);
            response.put("id", formId);
            response.put("name", name);
            response.put("discriminator", discriminator);
            response.put("description", description);
            response.put("html", html);

        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "module/muzimacore/htmlFormEntryModuleStatus.form", method = RequestMethod.GET)
    public boolean htmlFormEntryModuleStatus() {
        return ModuleFactory.isModuleStarted("htmlformentry");
    }
}
