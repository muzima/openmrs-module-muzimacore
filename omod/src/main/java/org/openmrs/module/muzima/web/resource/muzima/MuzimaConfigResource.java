package org.openmrs.module.muzima.web.resource.muzima;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/config",
        supportedClass = MuzimaConfig.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
@Handler(supports = MuzimaConfig.class)
public class MuzimaConfigResource extends MetadataDelegatingCrudResource<MuzimaConfig> {
    private static final Log log = LogFactory.getLog(MuzimaConfigResource.class);

    @Override
    protected NeedsPaging<MuzimaConfig> doGetAll(RequestContext context) throws ResponseException {
        MuzimaConfigService service = Context.getService(MuzimaConfigService.class);
        List<MuzimaConfig> all = service.getAll();
        return new NeedsPaging<MuzimaConfig>(all, context);
    }

    @Override
    protected PageableResult doSearch(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        Integer startIndex = context.getStartIndex();;
        Integer limit =  context.getLimit();;

        String nameParameter = request.getParameter("q");
        List<MuzimaConfig> muzimaConfigs = new ArrayList<MuzimaConfig>();

        if (nameParameter != null) {
            muzimaConfigs = Context.getService(MuzimaConfigService.class).getPagedConfigs(nameParameter, startIndex, limit);
        }
        return new NeedsPaging<MuzimaConfig>(muzimaConfigs, context);
    }

    @Override
    public MuzimaConfig getByUniqueId(String uuid) {
        MuzimaConfigService service = Context.getService(MuzimaConfigService.class);
        return service.getConfigByUuid(uuid);
    }

    @Override
    public Object retrieve(String uuid, RequestContext context) throws ResponseException {
        MuzimaConfigService service = Context.getService(MuzimaConfigService.class);
        return asRepresentation(service.getConfigByUuid(uuid), context.getRepresentation());
    }

    @Override
    public void delete(MuzimaConfig muzimaConfig, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public MuzimaConfig newDelegate() {
        return new MuzimaConfig();
    }

    @Override
    public MuzimaConfig save(MuzimaConfig muzimaConfig) {
        MuzimaConfigService service = Context.getService(MuzimaConfigService.class);
        try {
            return service.save(muzimaConfig);
        } catch (Exception e) {
            log.error(e);
        }
        return muzimaConfig;
    }

    @Override
    public void purge(MuzimaConfig muzimaConfig, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = null;

        if (rep instanceof DefaultRepresentation || rep instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("id");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("configJson");
            description.addSelfLink();
        }

        return description;
    }
}
