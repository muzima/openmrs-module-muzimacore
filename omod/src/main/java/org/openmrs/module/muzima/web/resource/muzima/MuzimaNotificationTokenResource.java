package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.NotificationTokenService;
import org.openmrs.module.muzima.model.NotificationToken;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Date;
import java.util.UUID;

@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/notificationtoken",
        supportedClass = NotificationToken.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.*"})
public class MuzimaNotificationTokenResource extends DataDelegatingCrudResource<NotificationToken> {
    @Override
    public NotificationToken getByUniqueId(String id) {
        NotificationTokenService notificationTokenService = Context.getService(NotificationTokenService.class);
        return notificationTokenService.getNotificationTokenById(id);
    }

    @Override
    protected void delete(NotificationToken notificationToken, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public NotificationToken newDelegate() {
        return new NotificationToken();
    }

    @Override
    public NotificationToken save(NotificationToken notificationToken) {
        NotificationTokenService notificationTokenService = Context.getService(NotificationTokenService.class);
        return notificationTokenService.saveNotificationToken(notificationToken);
    }

    @Override
    public void purge(NotificationToken notificationToken, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        return null;
    }
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.Creatable#create(org.openmrs.module.webservices.rest.SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public Object create(final SimpleObject propertiesToCreate, final RequestContext context) throws ResponseException {
        Object systemUserIdObject = propertiesToCreate.get("userSystemId");
        Object tokenObject = propertiesToCreate.get("token");
        User user = Context.getUserService().getUserByUsername(systemUserIdObject.toString());

        NotificationToken notificationToken = new NotificationToken();
        notificationToken.setUserId(user.getUserId());
        notificationToken.setToken(tokenObject.toString());
        notificationToken.setCreator(user);
        notificationToken.setDateCreated(new Date());
        notificationToken.setVoided(false);
        notificationToken.setUuid(UUID.randomUUID().toString());
        save(notificationToken);
        return true;
    }
}
