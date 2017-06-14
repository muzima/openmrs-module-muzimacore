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
package org.openmrs.module.muzima.web.resource.muzima;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.model.NotificationData;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/notificationdata",
        supportedClass = NotificationData.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","2.0.*",
        "2.1.*"})
public class NotificationDataResource extends DataDelegatingCrudResource<NotificationData> {

    /**
     * Gets the delegate object with the given unique id. Implementations may decide whether
     * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
     * human-readable property.
     *
     * @param uniqueId
     * @return the delegate for the given uniqueId
     */
    @Override
    public NotificationData getByUniqueId(final String uniqueId) {
        DataService dataService = Context.getService(DataService.class);
        return dataService.getNotificationDataByUuid(uniqueId);
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(String, String, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     *
     */
    @Override
    protected void delete(final NotificationData delegate, final String reason, final RequestContext context) throws ResponseException {
        DataService dataService = Context.getService(DataService.class);
        dataService.voidNotificationData(delegate, reason);
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(String, org.openmrs.module.webservices.rest.web.RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     *
     */
    @Override
    public void purge(final NotificationData delegate, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public NotificationData newDelegate() {
        return new NotificationData();
    }

    /**
     * Writes the delegate to the database
     *
     * @return the saved instance
     */
    @Override
    public NotificationData save(final NotificationData delegate) {
        DataService dataService = Context.getService(DataService.class);
        return dataService.saveNotificationData(delegate);
    }

    /**
     * Gets the {@link org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription} for the given representation for this
     * resource, if it exists
     *
     * @param rep
     * @return
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
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
            description.addProperty("status");
            description.addProperty("source");
            description.addProperty("payload");
            description.addProperty("receiver", Representation.DEFAULT);
            description.addProperty("sender", Representation.DEFAULT);
            description.addSelfLink();
            return description;
        } else {
            return null;
        }
    }

    public String getDisplayString(final NotificationData message) {
        StringBuilder builder = new StringBuilder();
        builder.append("sender: ").append(message.getSender().getUuid()).append(" - ");
        builder.append("receiver: ").append(message.getReceiver().getUuid()).append(" - ");
        builder.append("subject: ").append(message.getSubject());
        return builder.toString();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.Creatable#create(org.openmrs.module.webservices.rest.SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public Object create(final SimpleObject propertiesToCreate, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @RepHandler(FullRepresentation.class)
    public SimpleObject asFull(final NotificationData delegate) throws ConversionException {
        DelegatingResourceDescription description = getRepresentationDescription(Representation.FULL);
        return convertDelegateToRepresentation(delegate, description);
    }

    /**
     * Implementations should override this method if they are actually searchable.
     */
    @Override
    protected PageableResult doSearch(final RequestContext context) {
        String personUuid;

        DataService dataService = Context.getService(DataService.class);
        String searchString = context.getRequest().getParameter("q");
        personUuid = context.getRequest().getParameter("receiver");
        if (personUuid != null) {
            Person person = Context.getPersonService().getPersonByUuid(personUuid);
            if (person == null)
                return new EmptySearchResult();
            int encounterCount = dataService.countNotificationDataByReceiver(person, searchString, "unread").intValue();
            List<NotificationData> encounters =
                    dataService.getNotificationDataByReceiver(person, searchString, context.getStartIndex(), context.getLimit(), "unread");
            boolean hasMore = encounterCount > context.getStartIndex() + encounters.size();
            return new AlreadyPaged<NotificationData>(context, encounters, hasMore);
        }

        personUuid = context.getRequest().getParameter("sender");
        if (personUuid != null) {
            Person person = Context.getPersonService().getPersonByUuid(personUuid);
            if (person == null)
                return new EmptySearchResult();
            int encounterCount = dataService.countNotificationDataBySender(person, searchString, "unread").intValue();
            List<NotificationData> encounters =
                    dataService.getNotificationDataBySender(person, searchString, context.getStartIndex(), context.getLimit(), "unread");
            boolean hasMore = encounterCount > context.getStartIndex() + encounters.size();
            return new AlreadyPaged<NotificationData>(context, encounters, hasMore);
        }
        // TODO: in the future, this could be searching by category of the notification.
        return new NeedsPaging<NotificationData>(dataService.getAllNotificationData(), context);
    }
}
