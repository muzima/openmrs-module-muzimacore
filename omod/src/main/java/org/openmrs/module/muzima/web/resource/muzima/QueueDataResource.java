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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.DataService;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.handler.ObsQueueDataHandler;
import org.openmrs.module.muzima.handler.RelationshipQueueDataHandler;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.utils.JsonUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/queuedata",
        supportedClass = QueueData.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.0.*","2.1.*"})
public class QueueDataResource extends DataDelegatingCrudResource<QueueData> {

    /**
     * Gets the delegate object with the given unique id. Implementations may decide whether
     * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
     * human-readable property.
     *
     * @param uniqueId
     * @return the delegate for the given uniqueId
     */
    @Override
    public QueueData getByUniqueId(final String uniqueId) {
        DataService dataService = Context.getService(DataService.class);
        return dataService.getQueueDataByUuid(uniqueId);
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
    protected void delete(final QueueData delegate, final String reason, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
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
    public void purge(final QueueData delegate, final RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public QueueData newDelegate() {
        return new QueueData();
    }

    /**
     * Writes the delegate to the database
     *
     * @return the saved instance
     */
    @Override
    public QueueData save(final QueueData delegate) {
        DataService dataService = Context.getService(DataService.class);
        return dataService.saveQueueData(delegate);
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
            description.addProperty("payload");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    public String getDisplayString(final QueueData message) {
        return message.getDiscriminator();
    }

    /**
     * Gets a description of resource's properties which can be set on creation.
     *
     * @return the description
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     *
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
        delegatingResourceDescription.addRequiredProperty("dataSource");
        delegatingResourceDescription.addRequiredProperty("payload");
        delegatingResourceDescription.addRequiredProperty("discriminator");
        delegatingResourceDescription.addRequiredProperty("location");
        delegatingResourceDescription.addRequiredProperty("provider");
        delegatingResourceDescription.addRequiredProperty("formName");
        delegatingResourceDescription.addRequiredProperty("patientUuid");
        delegatingResourceDescription.addProperty("formDataUuid");
        return delegatingResourceDescription;
    }

    /**
     * Gets a description of resource's properties which can be edited.
     * <p/>
     * By default delegates to {@link #getCreatableProperties()} and removes sub-resources returned
     * by {@link #getPropertiesToExposeAsSubResources()}.
     *
     * @return the description
     * @throws org.openmrs.module.webservices.rest.web.response.ResponseException
     *
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        return new DelegatingResourceDescription();
    }

    /**
     * It is empty, because we set that already in the create method.
     *
     * @param queueData the queue data object.
     * @param dataSource the uuid of the data source.
     */
    @PropertySetter("dataSource")
    public static void setDataSource(final QueueData queueData, final String dataSource) {
    }

    /**
     * It is empty, because we set that already in the create method.
     *
     * @param queueData the queue data object.
     * @param payload the payload.
     */
    @PropertySetter("payload")
    public static void setPayload(final QueueData queueData, final Object payload) {
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.Creatable#create(org.openmrs.module.webservices.rest.SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public Object create(final SimpleObject propertiesToCreate, final RequestContext context) throws ResponseException{
        Object dataSourceObject = propertiesToCreate.get("dataSource");
        if (dataSourceObject == null) {
            throw new ConversionException("The data source property is missing!");
        }

        DataService dataService = Context.getService(DataService.class);
        DataSource dataSource = dataService.getDataSourceByUuid(dataSourceObject.toString());
        if (dataSource == null) {
            List<DataSource> dataSources = dataService.getAllDataSource();
            if (CollectionUtils.isEmpty(dataSources)) {
                throw new IllegalPropertyException("Unable to find any data source object.");
            }
            dataSource = dataSources.get(0);
        }

        Object payloadObject = propertiesToCreate.get("payload");
        if (payloadObject == null) {
            throw new ConversionException("The payload property is missing!");
        }

        String payload;
        if (payloadObject instanceof Map) {
            StringWriter stringWriter = new StringWriter();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writeValue(stringWriter, payloadObject);
            } catch (IOException e) {
                throw new ConversionException("Unable to convert payload property!", e);
            }
            payload = stringWriter.toString();
        } else {
            payload = payloadObject.toString();
        }

        String discriminator = null;
        if(propertiesToCreate.get("discriminator") != null){
            discriminator = propertiesToCreate.get("discriminator").toString();
        }

        String formName;
        Location location = null;

        QueueData queueData = new QueueData();
        String patientUuid = extractPatientUuidFromPayload(payload);
        Provider provider = extractProviderFromPayload(payload);

        if(StringUtils.equals(discriminator, ObsQueueDataHandler.DISCRIMINATOR_VALUE)){
            formName = "Individual Obs";
        } else if (StringUtils.equals(discriminator, RelationshipQueueDataHandler.DISCRIMINATOR_VALUE)) {
            formName = "Relationship";
        } else {
            location = extractLocationFromPayload(payload);
            formName = extractFormNameFromPayload(payload);
        }

        queueData.setDataSource(dataSource);
        queueData.setPayload(payload);
        queueData.setFormName(formName);
        queueData.setLocation(location);
        queueData.setProvider(provider);
        queueData.setPatientUuid(patientUuid);

        Object formDataUuid = propertiesToCreate.get("formDataUuid");
        if(formDataUuid != null) queueData.setFormDataUuid(formDataUuid.toString());

        propertiesToCreate.put("location",location);
        propertiesToCreate.put("provider",provider);
        propertiesToCreate.put("formName",formName);
        propertiesToCreate.put("patientUuid", patientUuid);

        setConvertedProperties(queueData, propertiesToCreate, getCreatableProperties(), true);
        queueData = save(queueData);
        return ConversionUtil.convertToRepresentation(queueData, Representation.DEFAULT);
    }

    private Provider extractProviderFromPayload(String payload) {
        String providerString = JsonUtils.readAsString(payload, "$['encounter']['encounter.provider_id']");
        Provider provider = Context.getProviderService().getProviderByIdentifier(providerString);
        return provider;
    }

    private Location extractLocationFromPayload(String payload) {
        String locationString = JsonUtils.readAsString(payload, "$['encounter']['encounter.location_id']");
        int locationId = NumberUtils.toInt(locationString, -999);
        Location location = Context.getLocationService().getLocation(locationId);
        return location;
    }

    private String extractFormNameFromPayload(String payload) {
        String formUuid = JsonUtils.readAsString(payload, "$['encounter']['encounter.form_uuid']");
        MuzimaFormService muzimaFormService = Context.getService(MuzimaFormService.class);
        MuzimaForm muzimaForm = muzimaFormService.getFormByUuid(formUuid);
        if(muzimaForm != null) {
            return muzimaForm.getName();
        }
        return "Unknown name";
    }

    private String extractPatientUuidFromPayload(String payload){
        String patientUuid = JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
        return patientUuid;
    }
}