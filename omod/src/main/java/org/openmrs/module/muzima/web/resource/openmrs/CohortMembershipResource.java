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

import org.apache.commons.beanutils.ConversionException;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CoreService;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
import org.openmrs.module.muzima.web.resource.wrapper.FakeCohortMembership;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/memberships",
        supportedClass = FakeCohortMembership.class,
        supportedOpenmrsVersions = {"2.1.*", "2.2.*"})
public class CohortMembershipResource extends DelegatingCrudResource<FakeCohortMembership> {

    @Override
    public PageableResult doSearch(final RequestContext context) throws ResponseException {
        HttpServletRequest request = context.getRequest();
        String uuidParameter = request.getParameter("uuid");
        String syncDateParameter = request.getParameter("syncDate");
        List<FakeCohortMembership> memberships = new ArrayList<FakeCohortMembership>();
        if (uuidParameter != null) {
            Date syncDate = ResourceUtils.parseDate(syncDateParameter);
            CoreService coreService = Context.getService(CoreService.class);
            final int membershipCount = coreService.countCohortMemberships(uuidParameter, syncDate).intValue();
            final Cohort cohort = Context.getCohortService().getCohortByUuid(uuidParameter);
            final List<CohortMembership> cohortMemberships =
                    new ArrayList<CohortMembership>(cohort.getMemberships());
            for (CohortMembership cohortMembership : cohortMemberships) {
                FakeCohortMembership fakeMembership = new FakeCohortMembership
                        (cohortMembership.getPatientId(), cohortMembership.getStartDate());
                fakeMembership.setEndDate(cohortMembership.getEndDate());
                fakeMembership.setCohort(cohort);
                final Patient patient = Context.getPatientService().getPatient(cohortMembership.getPatientId());
                fakeMembership.setPatient(patient);
                fakeMembership.setVoided(cohortMembership.getVoided());
                fakeMembership.setUuid(cohortMembership.getUuid());
                memberships.add(fakeMembership);
            }
            boolean hasMore = membershipCount > context.getStartIndex() + memberships.size();
            return new AlreadyPaged<FakeCohortMembership>(context, memberships, hasMore);
        } else {
            return new NeedsPaging<FakeCohortMembership>(memberships, context);
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
    public FakeCohortMembership getByUniqueId(final String uniqueId) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(Object, String, RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws ResponseException
     */
    @Override
    protected void delete(final FakeCohortMembership delegate, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeCohortMembership newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeCohortMembership save(final FakeCohortMembership delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(Object, RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws ResponseException
     */
    @Override
    public void purge(final FakeCohortMembership delegate, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("cohort");
            description.addProperty("patient", Representation.REF);
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addProperty("voided");
            description.addProperty("uuid");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("cohort");
            description.addProperty("patient");
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addProperty("voided");
            description.addProperty("uuid");
            description.addProperty("auditInfo");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    /**
     * Implementations should override this method if T is not uniquely identified by a "uuid"
     * property.
     *
     * @param delegate
     * @return the uuid property of delegate
     */
    @Override
    protected String getUniqueId(final FakeCohortMembership delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @param fakeCohortMembership the patient
     * @return string that contains cohort member's identifier and full name
     */
    public String getDisplayString(FakeCohortMembership fakeCohortMembership) {
        Patient patient = Context.getPatientService().getPatient(fakeCohortMembership.getPatientId());
        if (patient.getPatientIdentifier() == null) {
            return "";
        }


        return patient.getPatientIdentifier().getIdentifier() + " - "
                + patient.getPersonName().getFullName();
    }

    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(FakeCohortMembership delegate) throws ConversionException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("display", findMethod("getDisplayString"));
        description.addSelfLink();
        return convertDelegateToRepresentation(delegate, description);
    }

    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep(FakeCohortMembership delegate) throws Exception {
        SimpleObject ret = new SimpleObject();
        ret.put("display", delegate.toString());
        ret.put("links", "[ All Data resources need to define their representations ]");
        return ret;
    }
}
