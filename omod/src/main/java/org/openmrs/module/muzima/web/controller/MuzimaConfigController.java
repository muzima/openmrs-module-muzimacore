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
package org.openmrs.module.muzima.web.controller;

import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.CohortService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CohortDefinitionDataService;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.module.muzima.web.resource.utils.JsonUtils;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
public class MuzimaConfigController {
    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/config.json", method = RequestMethod.GET)
    public Map<String, Object> getConfiguration(final @RequestParam(value = "uuid") String uuid) {
        MuzimaConfig config = null;
        if (Context.isAuthenticated()) {
            MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
            config = configService.getConfigByUuid(uuid);
        }
        return WebConverter.convertMuzimaConfig(config);
    }

    @RequestMapping(value = "/module/muzimacore/config.json", method = RequestMethod.POST)
    public void saveConfig(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String uuid = (String) map.get("uuid");
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            String configJson = (String) map.get("configJson");
            String retireReason = (String) map.get("retireReason");

            List<Object> cohortObjects = JsonUtils.readAsObjectList(configJson,"$['config']['cohorts']");
            List<Object> cohortObb = new ArrayList<Object>();
            JSONObject configJsonObject = new JSONObject();
            JSONObject modifiedConfigJson = new JSONObject();
            if(cohortObjects != null){
                for(Object cohortObject:cohortObjects){
                    JSONObject cohort = (JSONObject)cohortObject;
                    String cohortUuid = (String)cohort.get("uuid");
                    CohortService cohortService = Context.getService(CohortService.class);
                    Cohort cohort1 = cohortService.getCohortByUuid(cohortUuid);
                    CohortDefinitionDataService cohortDefinitionDataService = Context.getService(CohortDefinitionDataService.class);
                    CohortDefinitionData cohortDefinitionData = cohortDefinitionDataService.getCohortDefinitionDataByCohortId(cohort1.getCohortId());
                    if(cohortDefinitionData != null) {
                        cohort.put("isFilterByLocationEnabled", cohortDefinitionData.getIsFilterByLocationEnabled());
                        cohort.put("isFilterByProviderEnabled", cohortDefinitionData.getIsFilterByProviderEnabled());
                    }
                    cohortObb.add(cohort);
                }
            }
            configJsonObject.put("name",JsonUtils.readAsString(configJson,"$['config']['name']"));
            configJsonObject.put("description",JsonUtils.readAsString(configJson,"$['config']['description']"));
            configJsonObject.put("concepts",JsonUtils.readAsObjectList(configJson,"$['config']['concepts']"));
            configJsonObject.put("forms",JsonUtils.readAsObjectList(configJson,"$['config']['forms']"));
            configJsonObject.put("locations",JsonUtils.readAsObjectList(configJson,"$['config']['locations']"));
            configJsonObject.put("providers",JsonUtils.readAsObjectList(configJson,"$['config']['providers']"));
            configJsonObject.put("settings",JsonUtils.readAsObjectList(configJson,"$['config']['settings']"));
            configJsonObject.put("cohorts",cohortObb.toArray());

            modifiedConfigJson.put("config",configJsonObject);

            String modifiedConfigJsonString = modifiedConfigJson.toJSONString();

            MuzimaConfigService configService = Context.getService(MuzimaConfigService.class);
            if (StringUtils.isNotBlank(uuid)) {
                MuzimaConfig config = configService.getConfigByUuid(uuid);
                if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(description)) {
                    config.setName(name);
                    config.setDescription(description);
                    config.setConfigJson(modifiedConfigJsonString);
                } else {
                    config.setRetired(true);
                    config.setRetireReason(retireReason);
                    config.setRetiredBy(Context.getAuthenticatedUser());
                    config.setDateRetired(new Date());
                }
                configService.save(config);
            } else {
                MuzimaConfig config = new MuzimaConfig();
                config.setName(name);
                config.setDescription(description);
                config.setConfigJson(modifiedConfigJsonString);
                configService.save(config);
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/mUzimaForms.json", method = RequestMethod.GET)
    public Map<String, Object> getForms(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();

        if (Context.isAuthenticated()) {
            List<Form> forms = Context.getFormService().getForms(search, null, null, null, null, null, null);

            MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
            List<Object> objects = new ArrayList<Object>();
            List<Object> concepts = new ArrayList<Object>();

            for (Form form : forms) {
                List<MuzimaForm> muzimaForms = muzimaFormService.getMuzimaFormByForm(form.getUuid(), false);
                if (muzimaForms != null && muzimaForms.size() > 0) {
                    objects.add(WebConverter.convertMuzimaForm(form, muzimaForms.get(0)));
                    concepts.add(WebConverter.convertMuzimaFormMeta(muzimaForms.get(0)));
                }
            }

            response.put("objects", objects);
            response.put("metaObjects", concepts);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/configCohorts.json", method = RequestMethod.GET)
    public Map<String, Object> getCohorts(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();

        if (Context.isAuthenticated()) {
            List<Object> objects = new ArrayList<Object>();
            for (Cohort cohort : Context.getCohortService().getCohorts(search)) {
                objects.add(WebConverter.convertMuzimaCohort(cohort));
            }
            response.put("objects", objects);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/configLocations.json", method = RequestMethod.GET)
    public Map<String, Object> getLocations(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();

        if (Context.isAuthenticated()) {
            List<Object> objects = new ArrayList<Object>();
            for (Location location : Context.getLocationService().getLocations(search)) {
                objects.add(WebConverter.convertMuzimaLocation(location));
            }
            response.put("objects", objects);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/module/muzimacore/configSettings.json", method = RequestMethod.GET)
    public Map<String, Object> getSettings(final @RequestParam(value = "search") String search) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (Context.isAuthenticated()) {
            List<Object> objects = new ArrayList<Object>();
            MuzimaSettingService settingService = Context.getService(MuzimaSettingService.class);
            for (MuzimaSetting setting : settingService.getSettings(search)) {
                objects.add(WebConverter.convertMuzimaSetting(setting));
            }
            response.put("objects", objects);
        }
        return response;
    }

    @RequestMapping(value = "/module/muzimacore/saveLocation.json", method = RequestMethod.POST)
    public void saveLocation(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            String name = (String) map.get("name");
            String description = (String) map.get("description");

            Location location = new Location();
            location.setName(name);
            location.setDescription(description);
            location.setRetired(false);

            LocationService locationService = Context.getService(LocationService.class);
            locationService.saveLocation(location);
        }
    }

    @RequestMapping(value = "/module/muzimacore/saveProvider.json", method = RequestMethod.POST)
    public void saveProvider(final @RequestBody Map<String, Object> map) {
        if (Context.isAuthenticated()) {
            Integer personID = (Integer) map.get("person_id");
            String name = (String) map.get("name");
            String identifier = (String) map.get("identifier");

            PersonService personService = Context.getPersonService();
            Person person = personService.getPerson(personID);
            Provider provider = new Provider();
            provider.setName(name);
            provider.setPerson(person);
            provider.setIdentifier(identifier);

            ProviderService providerService = Context.getService(ProviderService.class);
            providerService.saveProvider(provider);
        }
    }
}