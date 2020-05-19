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
import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CoreService;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
import org.openmrs.module.muzima.web.resource.wrapper.FakeCohortMember;
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
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/member",
        supportedClass = FakeCohortMember.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.*"})
public class CohortMemberResource extends DelegatingCrudResource<FakeCohortMember> {

    @Override
    public PageableResult doSearch(final RequestContext context) throws ResponseException {
        HttpServletRequest request = context.getRequest();
        String uuidParameter = request.getParameter("uuid");
        String membersRemovedOption = request.getParameter("members_removed");
        String syncDateParameter = request.getParameter("syncDate");
        String defaultLocation = request.getParameter("defaultLocation");
        String providerId = request.getParameter("providerId");
        List<FakeCohortMember> members = new ArrayList<FakeCohortMember>();
        if (uuidParameter != null) {
            Date syncDate = ResourceUtils.parseDate(syncDateParameter);
            CoreService coreService = Context.getService(CoreService.class);
            final List<Patient> patients = new ArrayList<Patient>();

            if(StringUtils.isNotEmpty(membersRemovedOption)){
                List<Patient> removedMembers = coreService.getPatientsRemovedFromCohort(uuidParameter, syncDate, defaultLocation, providerId);
                patients.addAll(removedMembers);
            } else {
                List<Patient> addedMembers = coreService.getPatients(uuidParameter, syncDate,
                        context.getStartIndex(), context.getLimit(), defaultLocation, providerId);
                patients.addAll(addedMembers);
            }

            final Cohort cohort = Context.getCohortService().getCohortByUuid(uuidParameter);
            for (Patient cohortMember : patients) {
                members.add(new FakeCohortMember(cohortMember, cohort));
            }

            return new NeedsPaging<FakeCohortMember>(members, context);
        } else {
            return new NeedsPaging<FakeCohortMember>(members, context);
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
    public FakeCohortMember getByUniqueId(final String uniqueId) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(Object, String, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    protected void delete(final FakeCohortMember delegate, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeCohortMember newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeCohortMember save(final FakeCohortMember delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(Object, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     */
    @Override
    public void purge(final FakeCohortMember delegate, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("display", findMethod("getDisplayString"));
            description.addSelfLink();
            return description;
        } else if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("patient");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("patient");
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
    protected String getUniqueId(final FakeCohortMember delegate) {
        return delegate.getCohort().getUuid();
    }

    /**
     * @param fakeCohortMember the patient
     * @return string that contains cohort member's identifier and full name
     */
    public String getDisplayString(FakeCohortMember fakeCohortMember) {

        if (fakeCohortMember.getPatient().getPatientIdentifier() == null) {
            return "";
        }

        return fakeCohortMember.getPatient().getPatientIdentifier().getIdentifier() + " - "
                + fakeCohortMember.getPatient().getPersonName().getFullName();
    }

    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(FakeCohortMember delegate) throws ConversionException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("display", findMethod("getDisplayString"));
        description.addSelfLink();
        return convertDelegateToRepresentation(delegate, description);
    }

    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep(FakeCohortMember delegate) throws Exception {
        SimpleObject ret = new SimpleObject();
        ret.put("display", delegate.toString());
        ret.put("links", "[ All Data resources need to define their representations ]");
        return ret;
    }
}
