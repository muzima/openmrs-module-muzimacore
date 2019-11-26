package org.openmrs.module.muzima.web.resource.muzima;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaSettingService;
import org.openmrs.module.muzima.model.MuzimaSetting;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.ResourceUtils;
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
import java.util.Date;
import java.util.List;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/setting",
        supportedClass = MuzimaSetting.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
@Handler(supports = MuzimaSetting.class)
public class MuzimaSettingResource extends MetadataDelegatingCrudResource<MuzimaSetting> {
    private final Log log = LogFactory.getLog(this.getClass());
    @Override
    protected NeedsPaging<MuzimaSetting> doGetAll(RequestContext context) throws ResponseException {
        MuzimaSettingService service = Context.getService(MuzimaSettingService.class);
        List<MuzimaSetting> all = service.getAllMuzimaSettings();
        return new NeedsPaging<MuzimaSetting>(all, context);
    }

    @Override
    protected PageableResult doSearch(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        Integer startIndex = context.getStartIndex();
        Integer limit =  context.getLimit();
        List<MuzimaSetting> muzimaSettings = new ArrayList<MuzimaSetting>();

        String propertyParameter = request.getParameter("property");
        if(propertyParameter != null){
            MuzimaSetting setting = Context.getService(MuzimaSettingService.class).getMuzimaSettingByProperty(propertyParameter);
            if(setting != null){
                muzimaSettings.add(setting);
            }
        } else {
            String nameParameter = request.getParameter("q");
            String syncDateParameter = request.getParameter("syncDate");
            Date syncDate = ResourceUtils.parseDate(syncDateParameter);
            if (nameParameter != null) {
                muzimaSettings = Context.getService(MuzimaSettingService.class).getPagedSettings(nameParameter, syncDate, startIndex, limit);
            } else {
                muzimaSettings = Context.getService(MuzimaSettingService.class).getPagedSettings(null, syncDate, startIndex, limit);
            }
        }
        return new NeedsPaging<MuzimaSetting>(muzimaSettings, context);
    }

    @Override
    public MuzimaSetting getByUniqueId(String uuid) {
        MuzimaSettingService service = Context.getService(MuzimaSettingService.class);
        return service.getMuzimaSettingByUuid(uuid);
    }

    @Override
    public Object retrieve(String uuid, RequestContext context) throws ResponseException {
        MuzimaSettingService service = Context.getService(MuzimaSettingService.class);
        return asRepresentation(service.getMuzimaSettingByUuid(uuid), context.getRepresentation());
    }

    @Override
    public void delete(MuzimaSetting muzimaSetting, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    public MuzimaSetting newDelegate() {
        return new MuzimaSetting();
    }

    @Override
    public MuzimaSetting save(MuzimaSetting muzimaSetting) {
        MuzimaSettingService service = Context.getService(MuzimaSettingService.class);
        try {
            return service.saveMuzimaSetting(muzimaSetting);
        } catch (Exception e) {
            log.error(e);
        }
        return muzimaSetting;
    }

    @Override
    public void purge(MuzimaSetting muzimaSetting, RequestContext requestContext) throws ResponseException {
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
            description.addProperty("property");
            description.addProperty("settingDataType");
            description.addProperty("valueBoolean");
            description.addProperty("valueString");
            description.addSelfLink();
        }

        return description;
    }

}
