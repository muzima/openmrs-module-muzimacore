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
package org.openmrs.module.muzima.web.resource.openmrs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CoreService;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
import org.openmrs.module.muzima.web.resource.wrapper.FakeEncounter;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.resource.impl.ServiceSearcher;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.muzima.utils.Constants.MuzimaSettings.MAXIMUM_ENCOUNTERS_DOWNLOAD_SETTING_PROPERTY;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/encounter",
        supportedClass = FakeEncounter.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
public class EncounterResource extends DataDelegatingCrudResource<FakeEncounter> {

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected PageableResult doSearch(final RequestContext context) {

        MuzimaSettingService muzimaSettingService = Context.getService(MuzimaSettingService.class);
        MuzimaSetting muzimaSetting = muzimaSettingService.getMuzimaSettingByProperty(MAXIMUM_ENCOUNTERS_DOWNLOAD_SETTING_PROPERTY);
        int maxEncounterResultsPerPatient = muzimaSetting!= null && StringUtils.isNumeric(muzimaSetting.getValueString()) ?
                Integer.parseInt(muzimaSetting.getValueString()) : 3; //Setting 3 as default results size
        if(maxEncounterResultsPerPatient == 0){
            return new AlreadyPaged<FakeEncounter>(context, new ArrayList<FakeEncounter>(), false);
        }

        HttpServletRequest request = context.getRequest();
        String patientParameter = request.getParameter("patient");
        String syncDateParameter = request.getParameter("syncDate");
        if (patientParameter != null) {
            CoreService coreService = Context.getService(CoreService.class);
            String[] patientUuids = StringUtils.split(patientParameter, ",");
            Date syncDate = ResourceUtils.parseDate(syncDateParameter);
            List<Encounter> encounters = coreService.getEncounters(Arrays.asList(patientUuids), maxEncounterResultsPerPatient, syncDate);

            List<FakeEncounter> fakeEncounters = new ArrayList<FakeEncounter>();
            for (Encounter encounter : encounters) {
                fakeEncounters.add(FakeEncounter.copyEncounter(encounter));
            }

            return new NeedsPaging<FakeEncounter>(fakeEncounters, context);
        } else {

            AlreadyPaged<Encounter> pagedEncounter =
                    new ServiceSearcher<Encounter>(EncounterService.class, "getEncounters", "getCountOfEncounters")
                            .search(context.getParameter("q"), context);

            List<FakeEncounter> fakeEncounters = new ArrayList<FakeEncounter>();
            for (Encounter encounter : pagedEncounter.getPageOfResults()) {
                fakeEncounters.add(FakeEncounter.copyEncounter(encounter));
            }
            return new AlreadyPaged<FakeEncounter>(context, fakeEncounters, pagedEncounter.hasMoreResults());

        }
    }

    /**
     * Gets the delegate object with the given unique id. Implementations may decide whether
     * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
     * human-readable property.
     *
     * @param uniqueId
     * @return the delegate for the given uniqueId
     */
    @Override
    public FakeEncounter getByUniqueId(String uniqueId) {
        Encounter encounter = Context.getEncounterService().getEncounterByUuid(uniqueId);
        return FakeEncounter.copyEncounter(encounter);
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(String, String, RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws ResponseException
     */
    @Override
    protected void delete(FakeEncounter delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeEncounter newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeEncounter save(FakeEncounter delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(String, RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws ResponseException
     */
    @Override
    public void purge(FakeEncounter delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Gets the {@link DelegatingResourceDescription} for the given representation for this
     * resource, if it exists
     *
     * @param rep
     * @return
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("id");
            description.addProperty("encounterDatetime");
            description.addProperty("patient", Representation.REF);
            description.addProperty("location", Representation.REF);
            description.addProperty("form", Representation.REF);
            description.addProperty("encounterType", Representation.REF);
            description.addProperty("provider", Representation.REF);
            description.addProperty("voided");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("id");
            description.addProperty("encounterDatetime");
            description.addProperty("patient", Representation.REF);
            description.addProperty("location");
            description.addProperty("form");
            description.addProperty("encounterType");
            description.addProperty("provider");
            description.addProperty("voided");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }
}
