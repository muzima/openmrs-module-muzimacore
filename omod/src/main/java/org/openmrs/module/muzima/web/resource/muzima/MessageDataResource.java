package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.Data;
import org.openmrs.module.muzima.model.MessageData;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Date;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/messagedata",
        supportedClass = MessageData.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"})
public class MessageDataResource extends DataDelegatingCrudResource<MessageData> {


    @Override
    public MessageData getByUniqueId(String uuid) {
        DataService dataService = Context.getService(DataService.class);
        return dataService.getMessageDataByUuid(uuid);
    }

    @Override
    protected void delete(MessageData messageData, String uuid, RequestContext requestContext) throws ResponseException {
        DataService dataService = Context.getService(DataService.class);
        dataService.voidMessageData(uuid,messageData, new Date(),"");
    }

    @Override
    public MessageData newDelegate() {
        return null;
    }

    @Override
    public MessageData save(MessageData messageData) {
        DataService dataService = Context.getService(DataService.class);
        dataService.saveMessageData(messageData);
        return messageData;
    }

    @Override
    public void purge(MessageData messageData, RequestContext requestContext) throws ResponseException {
        DataService dataService = Context.getService(DataService.class);
        dataService.purgeMessageData(messageData);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("subject");
            description.addProperty("body");
            description.addProperty("source");
            description.addProperty("sendDate");
            description.addProperty("receiver", Representation.DEFAULT);
            description.addProperty("sender", Representation.DEFAULT);
            description.addSelfLink();
            return description;
        } else {
            return null;
        }
    }
}
