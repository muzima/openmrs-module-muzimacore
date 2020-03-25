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
package org.openmrs.module.muzima.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CohortUpdateHistoryService;
import org.openmrs.module.muzima.api.service.ExpandedCohortProcessorService;
import org.openmrs.module.muzima.api.service.MuzimaCohortMetadataService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.Cohort;
import org.openmrs.module.muzima.model.CohortUpdateHistory;
import org.openmrs.module.muzima.model.MuzimaCohortMetadata;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("muzima.ExpandedCohortProcessorService")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 2.0.*")
public class ExpandedCohortProcessorServiceImplCompatibility1_9 implements ExpandedCohortProcessorService {
    public void process(CohortDefinitionData cohortDefinitionData){
        Cohort savedCohort = Context.getCohortService().getCohort(cohortDefinitionData.getCohortId());

        ReportingCompatibilityService reportingCompatibilityService = Context.getService(ReportingCompatibilityService.class);
        Cohort newCohort = reportingCompatibilityService.getPatientsBySqlQuery(cohortDefinitionData.getDefinition());

        Set<Integer> currentMembers = new HashSet<Integer>(savedCohort.getMemberIds());
        Set<Integer> newMembers = newCohort.getMemberIds();

        //For cohort update history
        CohortUpdateHistory cohortUpdateHistory = new CohortUpdateHistory();
        cohortUpdateHistory.setCohortId(savedCohort.getCohortId());

        Set<Integer> addedMembers = new HashSet<Integer>();
        Set<Integer> removedMembers = new HashSet<Integer>();
        String removedMemberIdsString = "";
        String addedMemberIdsString = "";

        //add members
        if(cohortDefinitionData.getIsMemberAdditionEnabled() == true) {
            addedMembers = new HashSet<Integer>(newMembers);
            addedMembers.removeAll(currentMembers);
            if (!addedMembers.isEmpty()) {
                for (Integer memberId : addedMembers) {
                    addedMemberIdsString += memberId + ",";
                }
                addedMemberIdsString = StringUtils.strip(addedMemberIdsString, ",");
                cohortUpdateHistory.setMembersAdded(addedMemberIdsString);
                savedCohort.getMemberIds().addAll(addedMembers);
            }
        }

        List<Integer> removedMembersList = new ArrayList<Integer>();
        //Remove members
        if(cohortDefinitionData.getIsMemberRemovalEnabled() == true) {
            removedMembers = new HashSet<Integer>(currentMembers);
            removedMembers.removeAll(newMembers);
            removedMembersList = new ArrayList<Integer>(currentMembers);
            removedMembersList.removeAll(newMembers);
            if (!removedMembers.isEmpty()) {
                for (Integer memberId : removedMembers) {
                    removedMemberIdsString += memberId + ",";
                }
                removedMemberIdsString = StringUtils.strip(removedMemberIdsString, ",");
                cohortUpdateHistory.setMembersRemoved(removedMemberIdsString);
                savedCohort.getMemberIds().removeAll(removedMembers);
            }
        }

        if(!addedMembers.isEmpty() || !removedMembers.isEmpty()) {
            Context.getCohortService().saveCohort(savedCohort);

            cohortUpdateHistory.setDateUpdated(new Date());
            CohortUpdateHistoryService cohortUpdateHistoryService = Context.getService(CohortUpdateHistoryService.class);
            cohortUpdateHistoryService.saveCohortUpdateHistory(cohortUpdateHistory);
        }

        //Processing of post cohort membership query
        if(!cohortDefinitionData.getFilterQuery().isEmpty()){
            MuzimaCohortMetadataService muzimaCohortMetadataService = Context.getService(MuzimaCohortMetadataService.class);

            //delete records of removed members
            if(!removedMemberIdsString.isEmpty()) {
                List<MuzimaCohortMetadata> muzimaCohortMetadata = muzimaCohortMetadataService.getMuzimaCohortMetadata(removedMembersList, cohortDefinitionData.getCohortId());
                muzimaCohortMetadataService.deleteMuzimaCohortMetadata(muzimaCohortMetadata);
            }

            //add records of added members
            if(!addedMemberIdsString.isEmpty()){
                List<Object> object = muzimaCohortMetadataService.executeFilterQuery(cohortDefinitionData.getFilterQuery());
                List<MuzimaCohortMetadata> muzimaCohortMetadataList = new ArrayList<MuzimaCohortMetadata>();
                for(int j=0;j<object.size();j++){
                    MuzimaCohortMetadata muzimaCohortMetadata = new MuzimaCohortMetadata();
                    Object [] obj= (Object[])object.get(j);
                    for(int i=0;i<obj.length;i++) {
                        int value = Integer.valueOf(obj[i].toString());
                        if(i==0) {
                            muzimaCohortMetadata.setCohortId(value);
                        }
                        if(i==1) {
                           muzimaCohortMetadata.setPatientId(value);
                        }
                        if(i==2) {
                            muzimaCohortMetadata.setLocationId(value);
                        }
                        if(i==3) {
                            muzimaCohortMetadata.setProviderId(value);
                        }
                    }

                    if(addedMembers.contains(muzimaCohortMetadata.getPatientId())){
                        muzimaCohortMetadataList.add(muzimaCohortMetadata);
                    }
                }
                muzimaCohortMetadataService.saveMuzimaCohortMetadata(muzimaCohortMetadataList);
            }
        }
    }
}
