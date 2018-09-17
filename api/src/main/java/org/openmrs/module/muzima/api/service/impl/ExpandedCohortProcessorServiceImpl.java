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
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.CohortUpdateHistoryService;
import org.openmrs.module.muzima.api.ExpandedCohortProcessorService;
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.Cohort;
import org.openmrs.module.muzima.model.CohortUpdateHistory;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ExpandedCohortProcessorServiceImpl implements ExpandedCohortProcessorService {
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

        //add members
        if(cohortDefinitionData.getIsMemberAdditionEnabled() == true) {
            addedMembers = new HashSet<Integer>(newMembers);
            addedMembers.removeAll(currentMembers);
            if (!addedMembers.isEmpty()) {
                String addedMemberIdsString = "";
                for (Integer memberId : addedMembers) {
                    addedMemberIdsString += memberId + ",";
                }
                addedMemberIdsString = StringUtils.strip(addedMemberIdsString, ",");
                cohortUpdateHistory.setMembersAdded(addedMemberIdsString);
                savedCohort.getMemberIds().addAll(addedMembers);
            }
        }

        //Remove members
        if(cohortDefinitionData.getIsMemberRemovalEnabled() == true) {
            removedMembers = new HashSet<Integer>(currentMembers);
            removedMembers.removeAll(newMembers);
            if (!removedMembers.isEmpty()) {
                String removedMemberIdsString = "";
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
    }
}
