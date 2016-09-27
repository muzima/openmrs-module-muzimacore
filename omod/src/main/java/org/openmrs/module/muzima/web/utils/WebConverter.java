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
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.model.ErrorMessage;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.RegistrationData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
public class WebConverter {

    public static String emptyString = "";

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


            System.out.println("data" + map.toString());

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
            System.out.println("Unable to read string value with path: " + path + " from: " + String.valueOf(jsonObject));
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
            map.put("uuid", form.getUuid());
            map.put("name", form.getName());
            map.put("metaJson", muzimaForm.getMetaJson());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaCohort(final Cohort cohort) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (cohort != null) {
            map.put("uuid", cohort.getUuid());
            map.put("name", cohort.getName());
        }
        return map;
    }

    public static Map<String, Object> convertMuzimaLocation(final Location location) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (location != null) {
            map.put("uuid", location.getUuid());
            map.put("name", location.getName());
        }
        return map;
    }

    public static Object convertMuzimaConcept(Concept concept) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (concept != null) {
            map.put("uuid", concept.getUuid());
            map.put("name", concept.getName());
        }
        return map;
    }
}