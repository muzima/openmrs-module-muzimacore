package org.openmrs.module.muzima.web.resource.muzima;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaPatientReportService;
import org.openmrs.module.muzima.model.MuzimaPatientReport;
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

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/patientreport",
        supportedClass = MuzimaPatientReport.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
@Handler(supports = MuzimaPatientReport.class)
public class MuzimaPatientReportResource extends MetadataDelegatingCrudResource<MuzimaPatientReport> {
    private static final Log log = LogFactory.getLog(MuzimaPatientReportResource.class);

    @Override
    protected NeedsPaging<MuzimaPatientReport> doGetAll(RequestContext context) throws ResponseException {
        MuzimaPatientReportService service = Context.getService(MuzimaPatientReportService.class);
        List<MuzimaPatientReport> all = service.getAllMuzimaPatientReports();
        return new NeedsPaging<MuzimaPatientReport>(all, context);
    }

    @Override
    protected PageableResult doSearch(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        Integer startIndex = context.getStartIndex();
        Integer limit =  context.getLimit();;

        String uuid = request.getParameter("patientUuid");
        List<MuzimaPatientReport> muzimaPatientReports = new ArrayList<MuzimaPatientReport>();

        if (uuid != null) {
            PatientService patientService = Context.getService(PatientService.class);
            Patient patient = patientService.getPatientByUuid(uuid);
            MuzimaPatientReportService service = Context.getService(MuzimaPatientReportService.class);
            muzimaPatientReports = service.getPagedMuzimaPatientReports(patient.getId(), startIndex, limit);
        }
        return new NeedsPaging<MuzimaPatientReport>(muzimaPatientReports, context);
    }

    @Override
    public MuzimaPatientReport getByUniqueId(String uuid) {
        PatientService patientService = Context.getService(PatientService.class);
        Patient patient = patientService.getPatientByUuid(uuid);
        MuzimaPatientReportService service = Context.getService(MuzimaPatientReportService.class);
        return service.getLatestPatientReportByPatientId(patient.getId());
    }

    @Override
    public Object retrieve(String uuid, RequestContext context) throws ResponseException {
        MuzimaPatientReportService service = Context.getService(MuzimaPatientReportService.class);
        return asRepresentation(service.getMuzimaPatientReportByUuid(uuid), context.getRepresentation());
    }
    
    @Override
    public void delete(MuzimaPatientReport muzimaPatientReport, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }
    
    //ToDo 
    @Override
    public void purge(MuzimaPatientReport muzimaPatientReport, RequestContext requestContext) throws ResponseException {
    }
    
    public MuzimaPatientReport newDelegate() {
        return new MuzimaPatientReport();
    }

    @Override
    public MuzimaPatientReport save(MuzimaPatientReport muzimaPatientReport) {
        MuzimaPatientReportService service = Context.getService(MuzimaPatientReportService.class);
        try {
            return service.saveMuzimaPatientReport(muzimaPatientReport);
        } catch (Exception e) {
            log.error(e);
        }
        return muzimaPatientReport;
    }
    
    public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
        DelegatingResourceDescription description = null;

        if (rep instanceof DefaultRepresentation || rep instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("patientUuid");
            description.addProperty("reportJson", findMethod("getReportJsonAsString"));
            description.addSelfLink();
        }
        return description;
    }

    public String getReportJsonAsString(MuzimaPatientReport report) {
        return report.getReportJsonAsString();
    }
}
