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
import org.openmrs.module.muzima.model.CohortDefinitionData;
import org.openmrs.Cohort;
import org.openmrs.module.muzima.model.CohortUpdateHistory;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;
import org.openmrs.util.ReportingcompatibilityUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

@Component("muzima.ExpandedCohortProcessorService")
@OpenmrsProfile(openmrsPlatformVersion = "2.0")
public class ExpandedCohortProcessorServiceImplCompatibility2_1 implements ExpandedCohortProcessorService {
    public void process(CohortDefinitionData cohortDefinitionData){
        Cohort savedCohort = Context.getCohortService().getCohort(cohortDefinitionData.getCohortId());

        ReportingCompatibilityService reportingCompatibilityService = Context.getService(ReportingCompatibilityService.class);
        Cohort newCohort = ReportingcompatibilityUtil.convert(reportingCompatibilityService.getPatientsBySqlQuery(cohortDefinitionData.getDefinition()));

        Collection<CohortMembership> newMembers = newCohort.getMemberships();

        //For cohort update history
        CohortUpdateHistory cohortUpdateHistory = new CohortUpdateHistory();
        cohortUpdateHistory.setCohortId(savedCohort.getCohortId());

        //add members
        String addedMemberIdsString = "";
        if(cohortDefinitionData.getIsMemberAdditionEnabled() == true) {
            for(CohortMembership newMember:newMembers){
                boolean isAlreadyMember = false;
                for(CohortMembership activeMember: savedCohort.getActiveMemberships()){
                    if(activeMember.getPatientId() == newMember.getPatientId()){
                        isAlreadyMember = true;
                    }
                }

                if(!isAlreadyMember){
                    addedMemberIdsString += newMember.getPatientId() + ",";
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
        if(cohortDefinitionData.getIsMemberRemovalEnabled() == true) {
            for(CohortMembership activeMember: savedCohort.getActiveMemberships()){
                boolean isInNewMemberList = false;
                for(CohortMembership newMember:newMembers){
                    if(activeMember.getPatientId() == newMember.getPatientId()){
                        isInNewMemberList = true;
                    }
                }

                if(!isInNewMemberList){
                    removedMemberIdsString += activeMember.getPatientId() + ",";
                    savedCohort.getActiveMemberships().remove(activeMember);
                }
            }
            removedMemberIdsString = StringUtils.strip(removedMemberIdsString, ",");
            if(StringUtils.isNotEmpty(removedMemberIdsString)) {
                cohortUpdateHistory.setMembersRemoved(removedMemberIdsString);
            }
        } else {
            for(CohortMembership activeMember: savedCohort.getActiveMemberships()){
                boolean isInNewMemberList = false;
                for(CohortMembership newMember:newMembers){
                    if(activeMember.getPatientId() == newMember.getPatientId()){
                        isInNewMemberList = true;
                    }
                }

                if(!isInNewMemberList){
                    activeMember.setEndDate(new Date());
                }
            }
        }

        if(StringUtils.isNotEmpty(addedMemberIdsString) || StringUtils.isNotEmpty(removedMemberIdsString)) {
            Context.getCohortService().saveCohort(savedCohort);

            cohortUpdateHistory.setDateUpdated(new Date());
            CohortUpdateHistoryService cohortUpdateHistoryService = Context.getService(CohortUpdateHistoryService.class);
            cohortUpdateHistoryService.saveCohortUpdateHistory(cohortUpdateHistory);
        }
    }
}