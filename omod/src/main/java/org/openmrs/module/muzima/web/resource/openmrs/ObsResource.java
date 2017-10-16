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
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CoreService;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
import org.openmrs.module.muzima.web.resource.wrapper.FakeObs;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/obs",
        supportedClass = FakeObs.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*"})
public class ObsResource extends DataDelegatingCrudResource<FakeObs> {

    public ObsResource() {
        allowedMissingProperties.add("valueCoded");
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
    public FakeObs getByUniqueId(String uniqueId) {
        Obs obs = Context.getObsService().getObsByUuid(uniqueId);
        return FakeObs.copyObs(obs);
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
    protected void delete(FakeObs delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeObs newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeObs save(FakeObs delegate) {
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
    public void purge(FakeObs delegate, RequestContext context) throws ResponseException {
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
            // TODO how to handle valueCodedName?
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("person", Representation.REF);
            description.addProperty("concept", Representation.REF);
            description.addProperty("value");
            description.addProperty("valueModifier");
            description.addProperty("obsDatetime");
            description.addProperty("accessionNumber");
            description.addProperty("obsGroup", Representation.REF);
            description.addProperty("valueCodedName", Representation.REF);
            description.addProperty("groupMembers");
            description.addProperty("comment");
            description.addProperty("location", Representation.REF);
            description.addProperty("order", Representation.REF);
            description.addProperty("encounter", Representation.REF);
            description.addProperty("voided");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            // TODO how to handle valueCodedName?
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("person", Representation.REF);
            description.addProperty("concept");
            description.addProperty("value");
            description.addProperty("obsDatetime");
            description.addProperty("obsGroup");
            description.addProperty("valueCodedName");
            description.addProperty("groupMembers", Representation.FULL);
            description.addProperty("comment");
            description.addProperty("location");
            description.addProperty("order");
            description.addProperty("encounter");
            description.addProperty("voided");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }

    /**
     * Gets obs by patient or encounter (paged according to context if necessary) only if a patient
     * or encounter parameter exists respectively in the request set on the {@link org.openmrs.module.webservices.rest.web.RequestContext}
     * otherwise searches for obs that match the specified query
     *
     * @param context
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected AlreadyPaged<FakeObs> doSearch(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        String personParameter = request.getParameter("person");
        String conceptParameter = request.getParameter("concept");
        String syncDateParameter = request.getParameter("syncDate");
        if (personParameter != null && conceptParameter != null) {
            String[] personUuids = StringUtils.split(personParameter, ",");
            String[] conceptUuids = StringUtils.split(conceptParameter, ",");
            Date syncDate = ResourceUtils.parseDate(syncDateParameter);

            CoreService coreService = Context.getService(CoreService.class);
            int obsCount = coreService.countObservations(Arrays.asList(personUuids),
                    Arrays.asList(conceptUuids), syncDate).intValue();
            List<Obs> observations = coreService.getObservations(Arrays.asList(personUuids),
                    Arrays.asList(conceptUuids), syncDate,
                    context.getStartIndex(), context.getLimit());
            boolean hasMore = obsCount > context.getStartIndex() + observations.size();

            List<FakeObs> fakeObservations = new ArrayList<FakeObs>();
            for (Obs observation : observations) {
                fakeObservations.add(FakeObs.copyObs(observation));
            }

            return new AlreadyPaged<FakeObs>(context, fakeObservations, hasMore);
        }
        return new AlreadyPaged<FakeObs>(context, Collections.<FakeObs>emptyList(), false);
    }

}
