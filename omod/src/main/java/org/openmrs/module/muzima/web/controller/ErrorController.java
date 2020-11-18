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

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.model.ErrorMessage;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.web.utils.WebConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Controller
public class ErrorController {

    @RequestMapping(value = "/module/muzimacore/error.json", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getError(final @RequestParam(value = "uuid") String uuid) {
        ErrorData errorData = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            errorData = dataService.getErrorDataByUuid(uuid);
        }
        return WebConverter.convertErrorData(errorData);
    }

    @RequestMapping(value = "/module/muzimacore/error.json", method = RequestMethod.POST)
    public Map<String, Object> saveEditedFormData(final @RequestParam(value = "uuid") String uuid,
                                   final @RequestBody String formData){
        ErrorData errorData = null;
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            ErrorData errorDataEdited = dataService.getErrorDataByUuid(uuid);
            errorDataEdited.setPayload(formData);
            List<ErrorMessage> errorMessages = dataService.validateData(uuid, formData);
            errorDataEdited.setErrorMessages(new HashSet<ErrorMessage>(errorMessages));
            errorData = dataService.saveErrorData(errorDataEdited);
        }
        return WebConverter.convertErrorData(errorData);
    }

    @RequestMapping(value = "/module/muzimacore/saveAndProcess.json", method = RequestMethod.POST)
    public Map<String, Object> saveAndProcessFormData(final @RequestParam(value = "uuid") String uuid,
                                                      final @RequestBody String formData){
        ErrorData errorData = null;
        List<ErrorData> newErrorData = new ArrayList<ErrorData>();
        if (Context.isAuthenticated()) {
            DataService dataService = Context.getService(DataService.class);
            ErrorData errorDataEdited = dataService.getErrorDataByUuid(uuid);
            errorDataEdited.setPayload(formData);
            List<ErrorMessage> errorMessages = dataService.validateData(uuid, formData);
            errorDataEdited.setErrorMessages(new HashSet<ErrorMessage>(errorMessages));
            errorData = dataService.saveErrorData(errorDataEdited);
            List<QueueData> requeuedQueueData = dataService.requeueErrorData(errorData);
            newErrorData = dataService.processQueueData(requeuedQueueData);
           }
        return WebConverter.convertErrorData(newErrorData);
    }

    @RequestMapping(value = "/module/muzimacore/mergePatient.json", method = RequestMethod.POST)
    public Map<String, Object> mergePatient(final @RequestBody Map<String, String> data) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("results", new ArrayList<Map<String, Object>>());

        if(Context.isAuthenticated()) {
            String errorDataUuid = data.get("errorDataUuid");
            String patientUuid = data.get("existingPatientUuid");
            String payload = data.get("payload");
            DataService dataService = Context.getService(DataService.class);
            List<QueueData> queuedData = dataService.mergeDuplicatePatient(errorDataUuid, patientUuid, payload);
            map.put("results", convertQueueDatas(queuedData));
        }
        return map;
    }

    @RequestMapping(value = "/module/muzimacore/requeueDuplicatePatient.json", method = RequestMethod.POST)
    public Map<String, Object> createNewAndRequeueAssociatedErroredData(final @RequestBody Map<String, String> data) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("results", new ArrayList<Map<String, Object>>());
        if(Context.isAuthenticated()) {
            String errorDataUuid = data.get("errorDataUuid");
            String modifiedPayload = data.get("payload");
            DataService dataService = Context.getService(DataService.class);
            ErrorData toRequeue = dataService.getErrorDataByUuid(errorDataUuid);
            String submittedPatientUuid = toRequeue.getPatientUuid();
            List<QueueData> queueDataList = new ArrayList<QueueData>();
            toRequeue.setPayload(modifiedPayload);
            QueueData queueData = dataService.saveQueueData(new QueueData(toRequeue));
            queueDataList.add(queueData);
            dataService.purgeErrorData(toRequeue);

            List<ErrorData> toRequeueErrors = getErrorDataWithAPatientUuid(submittedPatientUuid);
            for(ErrorData errorData: toRequeueErrors) {
                queueData = dataService.saveQueueData(new QueueData(errorData));
                dataService.purgeErrorData(errorData);
                queueDataList.add(queueData);
            }
            map.put("results", convertQueueDatas(queueDataList));
        }
        return map;

    }

    private List<Map<String, Object>> convertQueueDatas(final List<QueueData> queueDatas) {
        List<Map<String, Object>> converted = new ArrayList<Map<String, Object>>();
        for(QueueData queueData: queueDatas) {
            converted.add(WebConverter.convertQueueData(queueData));
        }
        return converted;
    }

    private List<ErrorData> getErrorDataWithAPatientUuid(final String patientUuid) {
        // Fetch all ErrorData associated with the patient UUID (the one determined to be of a duplicate patient).
        DataService dataService = Context.getService(DataService.class);
        int countOfErrors = dataService.countErrorData(patientUuid).intValue();
        return dataService.getPagedErrorData(patientUuid, 1, countOfErrors);
    }
}
