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
import org.openmrs.CohortMembership;
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
import org.openmrs.util.ReportingcompatibilityUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("muzima.ExpandedCohortProcessorService")
@OpenmrsProfile(openmrsPlatformVersion = "2.1")
public class ExpandedCohortProcessorServiceImplCompatibility2_1 implements ExpandedCohortProcessorService {
    public void process(CohortDefinitionData cohortDefinitionData) {
        Cohort savedCohort = Context.getCohortService().getCohort(cohortDefinitionData.getCohortId());

        ReportingCompatibilityService reportingCompatibilityService = Context.getService(ReportingCompatibilityService.class);
        Cohort newCohort = ReportingcompatibilityUtil.convert(reportingCompatibilityService.getPatientsBySqlQuery(cohortDefinitionData.getDefinition()));

        Collection<CohortMembership> newMembers = newCohort.getMemberships();

        //For cohort update history
        CohortUpdateHistory cohortUpdateHistory = new CohortUpdateHistory();
        cohortUpdateHistory.setCohortId(savedCohort.getCohortId());

        //add members
        Set<Integer> addedMembers = new HashSet<Integer>();
        String addedMemberIdsString = "";
        if (cohortDefinitionData.getIsMemberAdditionEnabled() == true) {
            for (CohortMembership newMember : newMembers) {
                boolean isAlreadyMember = false;
                for (CohortMembership activeMember : savedCohort.getActiveMemberships()) {
                    if (activeMember.getPatientId().equals(newMember.getPatientId())) {
                        isAlreadyMember = true;
                    }
                }

                if (!isAlreadyMember) {
                    addedMemberIdsString += newMember.getPatientId() + ",";
                    addedMembers.add(newMember.getPatientId());
                    savedCohort.addMembership(newMember);
                }
            }

            addedMemberIdsString = StringUtils.strip(addedMemberIdsString, ",");
            if (StringUtils.isNotEmpty(addedMemberIdsString)) {
                cohortUpdateHistory.setMembersAdded(addedMemberIdsString);
            }
        }

        //Remove members
        String removedMemberIdsString = "";
        List<Integer> removedMembersList = new ArrayList<Integer>();
        if (cohortDefinitionData.getIsMemberRemovalEnabled() == true) {
            for (CohortMembership activeMember : savedCohort.getActiveMemberships()) {

                boolean isInNewMemberList = false;
                for (CohortMembership newMember : newMembers) {
                    if (activeMember.getPatientId().equals(newMember.getPatientId())) {
                        isInNewMemberList = true;
                    }
                }

                if (!isInNewMemberList) {
                    removedMemberIdsString += activeMember.getPatientId() + ",";
                    removedMembersList.add(activeMember.getPatientId());
                    activeMember.setEndDate(new Date());
                }
            }
            removedMemberIdsString = StringUtils.strip(removedMemberIdsString, ",");
            if (StringUtils.isNotEmpty(removedMemberIdsString)) {
                cohortUpdateHistory.setMembersRemoved(removedMemberIdsString);
            }
        }

        Context.getCohortService().saveCohort(savedCohort);
        if (StringUtils.isNotEmpty(addedMemberIdsString) || StringUtils.isNotEmpty(removedMemberIdsString)) {
            cohortUpdateHistory.setDateUpdated(new Date());
            CohortUpdateHistoryService cohortUpdateHistoryService = Context.getService(CohortUpdateHistoryService.class);
            cohortUpdateHistoryService.saveCohortUpdateHistory(cohortUpdateHistory);
        }

        //Processing of post cohort membership query
        if (StringUtils.isNotEmpty(cohortDefinitionData.getFilterQuery())) {
            MuzimaCohortMetadataService muzimaCohortMetadataService = Context.getService(MuzimaCohortMetadataService.class);

            //delete records of removed members
            if (!removedMemberIdsString.isEmpty()) {
                List<MuzimaCohortMetadata> muzimaCohortMetadata = muzimaCohortMetadataService.getMuzimaCohortMetadata(removedMembersList, cohortDefinitionData.getCohortId());
                muzimaCohortMetadataService.deleteMuzimaCohortMetadata(muzimaCohortMetadata);
            }

            //add records of added members
            if (!addedMemberIdsString.isEmpty()) {
                List<Object> object = muzimaCohortMetadataService.executeFilterQuery(cohortDefinitionData.getFilterQuery());
                List<MuzimaCohortMetadata> muzimaCohortMetadataList = new ArrayList<MuzimaCohortMetadata>();
                for (int j = 0; j < object.size(); j++) {
                    MuzimaCohortMetadata muzimaCohortMetadata = new MuzimaCohortMetadata();
                    Object[] obj = (Object[]) object.get(j);
                    for (int i = 0; i < obj.length; i++) {
                        int value = Integer.valueOf(obj[i].toString());
                        if (i == 0) {
                            muzimaCohortMetadata.setCohortId(value);
                        }
                        if (i == 1) {
                            muzimaCohortMetadata.setPatientId(value);
                        }
                        if (i == 2) {
                            muzimaCohortMetadata.setLocationId(value);
                        }
                        if (i == 3) {
                            muzimaCohortMetadata.setProviderId(value);
                        }
                    }

                    if (addedMembers.contains(muzimaCohortMetadata.getPatientId())) {
                        muzimaCohortMetadataList.add(muzimaCohortMetadata);
                    }
                }
                muzimaCohortMetadataService.saveMuzimaCohortMetadata(muzimaCohortMetadataList);
            }
        }
    }
}