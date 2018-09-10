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

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.CoreService;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
import org.openmrs.module.muzima.web.resource.wrapper.FakeCohort;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
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
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/cohort",
        supportedClass = FakeCohort.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
public class CohortResource extends DataDelegatingCrudResource<FakeCohort> {
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<FakeCohort> doSearch(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        String nameParameter = request.getParameter("q");
        String syncDateParameter = request.getParameter("syncDate");
        CoreService coreService = Context.getService(CoreService.class);
        Date syncDate = ResourceUtils.parseDate(syncDateParameter);
        if (nameParameter != null) {
            final int cohortCount = coreService.countCohorts(nameParameter, syncDate).intValue();
            final List<Cohort> cohorts = coreService.getCohorts(nameParameter, syncDate, context.getStartIndex(), context.getLimit());

            final List<FakeCohort> fakeCohorts = new ArrayList<FakeCohort>();
            for (Cohort cohort : cohorts) {
                boolean hasCohortChanged = coreService.hasCohortChangedSinceDate(cohort.getUuid(),syncDate,context.getStartIndex(),context.getLimit());
                FakeCohort fakeCohort = FakeCohort.copyCohort(cohort);
                fakeCohort.setIsUpdated(hasCohortChanged);
                fakeCohorts.add(fakeCohort);
            }

            return new NeedsPaging<FakeCohort>(fakeCohorts, context) {
                public boolean hasMoreResults() {
                    return cohortCount > context.getStartIndex() + cohorts.size();
                }
            };

        } else {
            final List<Cohort> cohorts = Context.getCohortService().getAllCohorts();

            final List<FakeCohort> fakeCohorts = new ArrayList<FakeCohort>();
            for (Cohort cohort : cohorts) {
                boolean hasCohortChanged = coreService.hasCohortChangedSinceDate(cohort.getUuid(),syncDate,context.getStartIndex(),context.getLimit());
                FakeCohort fakeCohort = FakeCohort.copyCohort(cohort);
                fakeCohort.setIsUpdated(hasCohortChanged);
                fakeCohorts.add(fakeCohort);
            }

            return new NeedsPaging<FakeCohort>(fakeCohorts, context);
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
    public FakeCohort getByUniqueId(String uniqueId) {
        Cohort cohort = Context.getCohortService().getCohortByUuid(uniqueId);
        return FakeCohort.copyCohort(cohort);
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
    protected void delete(FakeCohort delegate, String reason, RequestContext context) throws ResponseException {
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
    public void purge(FakeCohort delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeCohort newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeCohort save(FakeCohort delegate) {
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
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("voided");
            description.addProperty("isUpdated");
            description.addProperty("memberIds", Representation.REF);
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("memberIds");
            description.addProperty("voided");
            description.addProperty("isUpdated");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }
}
