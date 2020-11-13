package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.annotation.Handler;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.muzima.model.MinimumSupportedAppVersion;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.sql.Timestamp;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/minimumsupportedappversion",
        supportedClass = MinimumSupportedAppVersion.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.*"})
@Handler(supports = MinimumSupportedAppVersion.class)
public class MinimumSupportedAppVersionResource extends MetadataDelegatingCrudResource<MinimumSupportedAppVersion> {
    @Override
    public MinimumSupportedAppVersion getByUniqueId(final String uuid){
        MinimumSupportedAppVersion minimumSupportedAppVersion = new MinimumSupportedAppVersion();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //this is the version code of the minimum compatible APK.
        minimumSupportedAppVersion.setVersion(17);
        minimumSupportedAppVersion.setUuid("5c75e184-afd7-4d05-9823-ea777268051b");
        minimumSupportedAppVersion.setTimestamp(timestamp.getTime());
        return minimumSupportedAppVersion;
    }

    @Override
    public void delete(final MinimumSupportedAppVersion minimumSupportedAppVersion, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public void purge(final MinimumSupportedAppVersion minimumSupportedAppVersion, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
    public MinimumSupportedAppVersion newDelegate() {
        return new MinimumSupportedAppVersion();
    }

    @Override
    public MinimumSupportedAppVersion save(final MinimumSupportedAppVersion delegate) {
        return delegate;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("version");
        description.addProperty("uuid");
        description.addProperty("timestamp");
        return description;
    }
}
