package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.annotation.Handler;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.muzima.model.MuzimaModuleVersion;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.sql.Timestamp;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/muzimacoreversion",
        supportedClass = MuzimaModuleVersion.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
@Handler(supports = MuzimaModuleVersion.class)
public class MuzimaModuleVersionResource extends MetadataDelegatingCrudResource<MuzimaModuleVersion> {
    @Override
    public MuzimaModuleVersion getByUniqueId(final String uuid){
        MuzimaModuleVersion muzimaModuleVersion = new MuzimaModuleVersion();
        Module module = ModuleFactory.getModuleById("muzimacore");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        muzimaModuleVersion.setVersion(module.getVersion());
        muzimaModuleVersion.setTimestamp(timestamp.getTime());
        return muzimaModuleVersion;
    }

    @Override
    public void delete(final MuzimaModuleVersion muzimaModuleVersion, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public void purge(final MuzimaModuleVersion muzimaModuleVersion, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
    public MuzimaModuleVersion newDelegate() {
        return new MuzimaModuleVersion();
    }

    @Override
    public MuzimaModuleVersion save(final MuzimaModuleVersion delegate) {
        return delegate;
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("version");
        description.addProperty("timestamp");
        return description;
    }
}
