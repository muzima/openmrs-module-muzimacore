/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.web.utils;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jettison.json.JSONArray;
import org.openmrs.Cohort;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.model.ErrorMessage;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaPatientReport;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;
import org.openmrs.module.muzima.model.ReportConfiguration;
import org.openmrs.module.reporting.report.ReportDesign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
public class WebConverter {

    public static String emptyString = "";
    private static final Logger logger = LoggerFactory.getLogger(WebConverter.class.getSimpleName());

    public static Map<String, Object> convertDataSource(final DataSource dataSource) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (dataSource != null) {
            map.put("uuid", dataSource.getUuid());
            map.put("name", dataSource.getName());
            map.put("description", dataSource.getDescription());
            map.put("created", Context.getDateFormat().format(dataSource.getDateCreated()));
        }
        return map;
    }

    public static Map<String, Object> convertQueueData(final QueueData queueData) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (queueData != null) {
            map.put("uuid", queueData.getUuid());
            map.put("discriminator", queueData.getDiscriminator());
            map.put("source", queueData.getDataSource().getName());
            map.put("payload", queueData.getPayload());
            map.put("submitted", Context.getDateFormat().format(queueData.getDateCreated()));

            if(queueData.getPatientUuid() == null){
                map.put("patientUuid", emptyString);
            } else {
                map.put("patientUuid", queueData.getPatientUuid());
            }
        }
        return map;
    }

    public static Map<String, Object> convertErrorData(final List<ErrorData> errorDataList) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("newErrors",errorDataList.size());
        return map;
    }

    public static Map<String, Object> convertErrorData(final ErrorData errorData) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (errorData != null) {
            map.put("uuid", errorData.getUuid());
            map.put("discriminator", errorData.getDiscriminator());
            map.put("source", errorData.getDataSource().getName());
            map.put("message", errorData.getMessage());
            map.put("payload", errorData.getPayload());
            if(errorData.getLocation() == null){
                map.put("locationId", emptyString);
                map.put("locationName", emptyString);
            }else{
                map.put("locationId", errorData.getLocation().getLocationId());
                map.put("locationName", errorData.getLocation().getName());
            }
            if(errorData.getProvider() == null){
                map.put("providerId", emptyString);
                map.put("providerName", emptyString);
            }else{
                map.put("providerId", errorData.getProvider().getIdentifier());
                map.put("providerName", errorData.getProvider().getName());
            }
            if(errorData.getFormName() == null){
                map.put("formName", emptyString);
            }else{
                map.put("formName", errorData.getFormName());
            }
            map.put("submitted", Context.getDateFormat().format(errorData.getDateCreated()));
            map.put("processed", Context.getDateFormat().format(errorData.getDateProcessed()));
            map.put("regErrorUuid", emptyString);
            if(errorData.getPatientUuid() == null){
                map.put("patientUuid", emptyString);
            } else {
                map.put("patientUuid", errorData.getPatientUuid());
                //get the registration errordata uuid if any for this patient
                if(!StringUtils.equals("json-registration",errorData.getDiscriminator())){
                    ErrorData regErrorData = getRegErrorData(errorData.getPatientUuid());
                    if(regErrorData != null){
                        map.put("regErrorUuid", regErrorData.getUuid());
                    }
                }
            }

            Map<String, Object> errorMap = new HashMap<String, Object>();
            for(ErrorMessage e : errorData.getErrorMessages()){
                errorMap.put(e.getId().toString(), e.getMessage());
            }

            map.put("Errors", JSONObject.toJSONString(errorMap));
        }
        return map;
    }

    private static ErrorData getRegErrorData(String patientUuid) {
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            return dataService.getRegistrationErrorDataByPatientUuid(patientUuid);
        }
        return null;
    }

    public static Map<String, Object> convertRegistrationData(final RegistrationData registrationData) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (registrationData != null) {
            map.put("uuid", registrationData.getUuid());
            map.put("assignedUuid", registrationData.getAssignedUuid());

            Patient patient = Context.getPatientService().getPatientByUuid(registrationData.getAssignedUuid());
            Map<String, Object> patientMap = new HashMap<String, Object>();
            patientMap.put("name", patient.getPersonName().getFullName());
            patientMap.put("gender", patient.getGender());
            patientMap.put("birthdate", Context.getDateFormat().format(patient.getBirthdate()));
            patientMap.put("identifier", patient.getPatientIdentifier().getIdentifier());
            map.put("patient", patientMap);

            map.put("temporaryUuid", registrationData.getTemporaryUuid());
            map.put("submitted", Context.getDateFormat().format(registrationData.getDateCreated()));
        }
        return map;
    }

    public static Map<String, Object> convertEditRegistrationData(final ErrorData errorData) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (errorData != null) {
            map.put("uuid", errorData.getUuid());
            map.put("discriminator", errorData.getDiscriminator());
            //map.put("form-template-name",errorData.)
            map.put("source", errorData.getDataSource().getName());
            map.put("message", errorData.getMessage());
            XmlJsonUtil.createPatientValuesFromPayload(map, errorData.getPayload());
            map.put("submitted", Context.getDateFormat().format(errorData.getDateCreated()));
            map.put("processed", Context.getDateFormat().format(errorData.getDateProcessed()));
        }
        return map;
    }

    public static  Map<String,Object> convertErrorMessages(List<ErrorMessage> errorMessages) {
        Map<String, Object> outerMap = new HashMap<String, Object>();
        Map<String, Object> innerMap = new HashMap<String, Object>();
        int count = 0;
        for (ErrorMessage errorMessage : errorMessages) {
            count++;
            innerMap.put(Integer.toString(count), errorMessage.getMessage());
        }
        outerMap.put("Errors", innerMap);
        return outerMap;
    }

    private static Provider extractProviderFromPayload(String payload) {
        String providerString = readAsString(payload, "$['encounter']['encounter.provider_id']");
        Provider provider = Context.getProviderService().getProviderByIdentifier(providerString);
        return provider;
    }

    private static Location extractLocationFromPayload(String payload) {
        String locationString = readAsString(payload, "$['encounter']['encounter.location_id']");
        int locationId = NumberUtils.toInt(locationString, -999);
        Location location = Context.getLocationService().getLocation(locationId);
        return location;
    }

    private static String extractFormNameFromPayload(String payload) {
        String formUuid = readAsString(payload, "$['encounter']['encounter.form_uuid']");
        MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
        MuzimaForm muzimaForm = muzimaFormService.getFormByUuid(formUuid);
        return muzimaForm.getName();
    }

    /**
     * Read string value from the json object.
     *
     * @param jsonObject the json object.
     * @param path       the path inside the json object.
     * @return the string value in the json object. When the path is invalid, by default will return null.
     */
    private static String readAsString(final String jsonObject, final String path) {
        String returnedString = null;
        try {
            returnedString = JsonPath.read(jsonObject, path);
        } catch (Exception e) {
            logger.error("Unable to read string value with path: " + path);
        }
        return returnedString;
    }

    public static Map<String, Object> convertMuzimaConfig(final MuzimaConfig config) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (config != null) {
            map.put("uuid", config.getUuid());
            map.put("name", config.getName());
            map.put("description", config.getDescription());
            map.put("configJson", config.getConfigJson());
            map.put("created", Context.getDateFormat().format(config.getDateCreated()));
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaForm(final Form form, final MuzimaForm muzimaForm) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (form != null) {
            map.put("uuid", muzimaForm.getUuid());
            map.put("discriminator", muzimaForm.getDiscriminator());
            map.put("name", form.getName());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaFormMeta(final MuzimaForm muzimaForm) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("metaJson", muzimaForm.getMetaJson());
        return map;
    }

    public static Map<String, Object> convertMuzimaCohort(final Cohort cohort) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (cohort != null) {
            map.put("id",cohort.getId());
            map.put("uuid", cohort.getUuid());
            map.put("name", cohort.getName());
            map.put("description",cohort.getDescription());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaLocation(final Location location) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (location != null) {
            map.put("uuid", location.getUuid());
            map.put("id",location.getId());
            map.put("name", location.getName());
        }
        return map;
    }
    public static Map<String, Object> convertMuzimaSetting(final MuzimaSetting setting) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (setting != null) {
            map.put("uuid", setting.getUuid());
            map.put("name", setting.getName());
            map.put("property", setting.getProperty());
            map.put("description", setting.getDescription());
            map.put("datatype", setting.getSettingDataType());
            map.put("value", setting.getSettingValue());
        }
        return map;
    }
    public static Map<String, Object> convertCohortDefinitionData(final CohortDefinitionData cohortDefinitionData){
        Map<String, Object> map = new HashMap<String, Object>();
        if(cohortDefinitionData!=null){
            Cohort cohort = Context.getCohortService().getCohort(cohortDefinitionData.getCohortId());
            map.put("cohortid",cohortDefinitionData.getCohortId());
            map.put("name",cohort.getName());
            map.put("description",cohort.getDescription());
            map.put("definition",cohortDefinitionData.getDefinition());
            map.put("isScheduledForExecution",cohortDefinitionData.getIsScheduledForExecution());
            map.put("isMemberAdditionEnabled",cohortDefinitionData.getIsMemberAdditionEnabled());
            map.put("isMemberRemovalEnabled",cohortDefinitionData.getIsMemberRemovalEnabled());
            map.put("isFilterByProviderEnabled",cohortDefinitionData.getIsFilterByProviderEnabled());
            map.put("isFilterByLocationEnabled",cohortDefinitionData.getIsFilterByLocationEnabled());
            map.put("filterQuery",cohortDefinitionData.getFilterQuery());
            map.put("uuid",cohortDefinitionData.getUuid());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaReportConfiguration(final ReportConfiguration reportConfiguration) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (reportConfiguration!= null) {
            map.put("uuid", reportConfiguration.getUuid());
            map.put("reports", reportConfiguration.getReportDesigns());
            map.put("cohort", Context.getCohortService().getCohortByUuid(reportConfiguration.getCohortUuid()).getName());
            map.put("user",reportConfiguration.getCreator().toString());
            map.put("priority",reportConfiguration.getPriority());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaPatientReport(final MuzimaPatientReport patientReport) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (patientReport!= null) {
            map.put("uuid", patientReport.getUuid());
            map.put("patientId", patientReport.getPatientId());
            map.put("reportJson", patientReport.getPatientId());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaReport( ReportDesign reportDesign) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (reportDesign != null) {
            map.put("uuid", reportDesign.getUuid());
            map.put("name", reportDesign.getName());
        }
        return map;
    }

    public static Map<String, Object>  convertForm(Form form) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (form != null) {
            map.put("uuid", form.getUuid());
            map.put("name", form.getName());
            map.put("version", form.getVersion());
            map.put("description", form.getDescription());
            map.put("retired",form.getRetired());
        }
        return map;
    }

    public static List<Map<String,Object>>  convertForms(List<Form> forms) {
        List<Map<String,Object>> map = new ArrayList<Map<String, Object>>();
        for (Form form:forms) {
            map.add(convertForm(form));
        }
        return map;
    }

    public static List<Map<String,Object>>  convertList(List<Object[]> results) {
         List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
         for (Object[] result : results) {
             Map<String, Object> map = new HashMap<String, Object>();
             map.put("discriminator", result[0]);
             map.put("count",result[1]);
             mapList.add(map);
        }
         return mapList;
    }

    public static Map<String, Object>  convertProvider(Provider provider) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (provider != null) {
            map.put("uuid", provider.getUuid());
            map.put("name", provider.getName());
            map.put("identifier", provider.getIdentifier());
        }
        return map;
    }

    public static Map<String, Object>  convertLocale(String locale) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (locale != null) {
            map.put("locale", locale);
        }
        return map;
    }
}