package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.FormDataStatus;
import org.openmrs.module.muzima.model.NotificationData;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/formdatastatus",
        supportedClass = FormDataStatus.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.*"})

public class FormDataStatusResource  extends DataDelegatingCrudResource<FormDataStatus> {
    public FormDataStatus getByUniqueId(final String formDataUuid){
        DataService dataService = Context.getService(DataService.class);
        return dataService.getFormDataStatusByFormDataUuid(formDataUuid);
    }

    public void delete(final FormDataStatus formDataStatus, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public void purge(final FormDataStatus formDataStatus, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
    public FormDataStatus newDelegate() {
        return new FormDataStatus();
    }

    @Override
    public FormDataStatus save(final FormDataStatus delegate) {
        return delegate;
    }

    public String getDisplayString(final FormDataStatus formDataStatus) {
        StringBuilder builder = new StringBuilder();
        builder.append("formDataUuid: ").append(formDataStatus.getUuid()).append(" - ");
        builder.append("status: ").append(formDataStatus.getStatus());
        return builder.toString();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("status");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("status");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
        delegatingResourceDescription.addRequiredProperty("status");
        return delegatingResourceDescription;
    }
}
