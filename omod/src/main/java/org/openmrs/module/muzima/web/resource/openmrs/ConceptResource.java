/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.web.resource.openmrs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.web.controller.MuzimaConstants;
import org.openmrs.module.muzima.web.resource.wrapper.FakeConcept;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.LocaleUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * TODO: Write brief description about the class here.
 */
@Resource(name = MuzimaConstants.MUZIMA_NAMESPACE + "/concept",
        supportedClass = FakeConcept.class,
        supportedOpenmrsVersions = {"1.8.*", "1.9.*","1.10.*","1.11.*","1.12.*","2.*"})
public class ConceptResource extends DelegatingCrudResource<FakeConcept> {

    public ConceptResource() {
        allowedMissingProperties.add("units");
        allowedMissingProperties.add("precise");
    }

    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(FakeConcept delegate) throws ConversionException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("id");
        description.addProperty("display", "displayConcept", Representation.DEFAULT);
        if (delegate.isRetired()) {
            description.addProperty("retired");
        }
        description.addSelfLink();
        return convertDelegateToRepresentation(delegate, description);
    }

    @RepHandler(FullRepresentation.class)
    public SimpleObject asFull(FakeConcept delegate) throws ConversionException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("id");
        description.addProperty("datatype", Representation.DEFAULT);

        description.addProperty("retired");

        description.addProperty("names", Representation.DEFAULT);
        description.addProperty("descriptions", Representation.DEFAULT);

        description.addProperty("auditInfo", findMethod("getAuditInfo"));
        description.addSelfLink();
        if (delegate.isNumeric()) {
            description.addProperty("units");
            description.addProperty("precise");
        }
        return convertDelegateToRepresentation(delegate, description);
    }

    /**
     * Gets the delegate object with the given unique id. Implementations may decide whether
     * "unique id" means a uuid, or if they also want to retrieve delegates based on a unique
     * human-readable property.
     *
     * @param uniqueId
     * @return the delegate for the given uniqueId
     */
    @Override
    public Object retrieve(String uniqueId, RequestContext context) throws ResponseException {
        //TODO: this is an overkill of a very simple method that would be solved simply by ConceptService.getConceptByUuid(uuid, locales)
        //TODO: Observe for OpenMRS to implement such method
        Concept concept = Context.getConceptService().getConceptByUuid(uniqueId);
        if (concept == null && StringUtils.isNumeric(uniqueId))
            concept = Context.getConceptService().getConcept(Integer.parseInt(uniqueId));

        Concept localizedConcept = concept;
        if (concept != null) {
            String acceptedLanguages = context.getRequest().getHeader("Accept-Language");
            if (StringUtils.isNotBlank(acceptedLanguages)) {
                StringTokenizer localeTokens = new StringTokenizer(acceptedLanguages, ",");
                if (localeTokens.hasMoreElements()) {
                    String strLocale = localeTokens.nextToken();
                    StringTokenizer countrySplitter = new StringTokenizer(strLocale, "_-");
                    if (countrySplitter.hasMoreElements()) {
                        Locale parsedLocale = new Locale(countrySplitter.nextToken());
                        if (concept.getNames(parsedLocale) != null && concept.getNames(parsedLocale).size() > 0 ) {
                            localizedConcept.setNames(concept.getNames(parsedLocale));
                            if (concept.getShortNameInLocale(parsedLocale) != null)
                                localizedConcept.setShortName(concept.getShortNameInLocale(parsedLocale));
                            if (concept.getPreferredName(parsedLocale) != null)
                                localizedConcept.setPreferredName(concept.getPreferredName(parsedLocale));
                            if (concept.getFullySpecifiedName(parsedLocale) != null)
                                localizedConcept.setFullySpecifiedName(concept.getFullySpecifiedName(parsedLocale));
                            if (concept.getDescription(parsedLocale) != null) {
                                Collection<ConceptDescription> descriptions = new ArrayList<ConceptDescription>();
                                descriptions.add(concept.getDescription(parsedLocale));
                                localizedConcept.setDescriptions(descriptions);
                            } else
                                localizedConcept.setDescriptions(null);
                        } else
                            localizedConcept = null;
                    }
                }
            }
        }

        return ConversionUtil.convertToRepresentation(FakeConcept.copyConcept(localizedConcept), context.getRepresentation());
    }

    @Override
    public FakeConcept getByUniqueId(String s) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Void or retire delegate, whichever action is appropriate for the resource type. Subclasses
     * need to override this method, which is called internally by
     * {@link #delete(String, String, RequestContext)}.
     *
     * @param delegate
     * @param reason
     * @param context
     * @throws ResponseException
     */
    @Override
    protected void delete(FakeConcept delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Instantiates a new instance of the handled delegate
     *
     * @return
     */
    @Override
    public FakeConcept newDelegate() {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Writes the delegate to the database
     *
     * @param delegate
     * @return the saved instance
     */
    @Override
    public FakeConcept save(FakeConcept delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Purge delegate from persistent storage. Subclasses need to override this method, which is
     * called internally by {@link #purge(String, RequestContext)}.
     *
     * @param delegate
     * @param context
     * @throws ResponseException
     */
    @Override
    public void purge(FakeConcept delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    /**
     * Gets the {@link DelegatingResourceDescription} for the given representation for this
     * resource, if it exists
     *
     * @param rep
     * @return
     */
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("id");
            description.addProperty("names", Representation.REF);
            description.addProperty("datatype", Representation.REF);
            description.addProperty("descriptions", Representation.REF);

            description.addProperty("retired");

            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        }
        return null;
    }

    /**
     * Concept searches support the following additional query parameters:
     * <ul>
     * <li>answerTo=(uuid): restricts results to concepts that are answers to the given concept uuid
     * </li>
     * <li>memberOf=(uuid): restricts to concepts that are set members of the given concept set's
     * uuid</li>
     * </ul>
     *
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
     */
    @Override
    protected PageableResult doSearch(RequestContext context) {
        ConceptService service = Context.getConceptService();
        Integer startIndex = null;
        Integer limit = null;
        boolean canPage = true;

        // Collect information for answerTo and memberOf query parameters
        String acceptedLanguages = context.getRequest().getHeader("Accept-Language");
        String answerToUuid = context.getRequest().getParameter("answerTo");
        String memberOfUuid = context.getRequest().getParameter("memberOf");
        Concept answerTo = null;
        List<Concept> memberOfList = null;
        if (StringUtils.isNotBlank(answerToUuid)) {
            try {
                answerTo = (Concept) ConversionUtil.convert(answerToUuid, Concept.class);
            }
            catch (ConversionException ex) {
                log.error("Unexpected exception while retrieving answerTo Concept with UUID " + answerToUuid, ex);
            }
        }

        if (StringUtils.isNotBlank(memberOfUuid)) {
            Concept memberOf = service.getConceptByUuid(memberOfUuid);
            memberOfList = service.getConceptsByConceptSet(memberOf);
            canPage = false; // ConceptService does not support memberOf searches, so paging must be deferred.
        }

        // Only set startIndex and limit if we can return paged results
        if (canPage) {
            startIndex = context.getStartIndex();
            limit = context.getLimit();
        }

        List<ConceptSearchResult> searchResults;

        List<Locale> locales = null;
        if (StringUtils.isNotBlank(acceptedLanguages)) {
            StringTokenizer localeTokens = new StringTokenizer(acceptedLanguages, ",");
            if (localeTokens.hasMoreElements()){
                String strLocale = localeTokens.nextToken();
                //TODO: Agree if this is the Best approach. [If several locales pick the first one with priority 1]
                /*
                    A locale of format en-US has two parts
                    <pre>en is the language (English in this case) and US is the country</pre>

                    Since OpenMRS saves concepts names only using the language we will only take the first token

                    Here we would expect a - but also lets support _ just in case
                    e.g en-US vs en_US */
                StringTokenizer countrySplitter = new StringTokenizer(strLocale, "_-");
                if (countrySplitter.hasMoreElements()) {
                    // We have a language, create a Locale for this language
                    Locale parsedLocale = new Locale(countrySplitter.nextToken());
                    locales = new ArrayList<Locale>();
                    locales.add(parsedLocale); //

                    //TODO: ConceptService searches only on Context.getLocale();
                    Context.setLocale(parsedLocale);
                }
            }
        } else {
            // get the user's locales...and then convert that from a set to a list
            locales = new ArrayList<Locale>(LocaleUtility.getLocalesInOrder());
        }

        searchResults = service.getConcepts(context.getParameter("q"), locales, context.getIncludeAll(), null, null, null,
                null, answerTo, startIndex, limit);

        // convert search results into list of concepts
        List<FakeConcept> results = new ArrayList<FakeConcept>(searchResults.size());
        for (ConceptSearchResult csr : searchResults) {
            // apply memberOf filter
            if (memberOfList == null || memberOfList.contains(csr.getConcept()))
                results.add(FakeConcept.copyConcept(csr.getConcept()));
        }

        PageableResult result;
        if (canPage) {
            Integer count = service.getCountOfConcepts(context.getParameter("q"), locales, false, Collections
                    .<ConceptClass> emptyList(), Collections.<ConceptClass> emptyList(), Collections
                    .<ConceptDatatype> emptyList(), Collections.<ConceptDatatype> emptyList(), answerTo);
            boolean hasMore = count > startIndex + limit;
            result = new AlreadyPaged<FakeConcept>(context, results, hasMore);
        } else {
            result = new NeedsPaging<FakeConcept>(results, context);
        }

        return result;
    }

    /**
     * This does not include retired concepts
     *
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<FakeConcept> doGetAll(final RequestContext context) {
        List<Concept> allConcepts = Context.getConceptService().getAllConcepts(null, true, context.getIncludeAll());

        List<FakeConcept> results = new ArrayList<FakeConcept>(allConcepts.size());
        for (Concept concept : allConcepts) {
            results.add(FakeConcept.copyConcept(concept));
        }
        return new NeedsPaging<FakeConcept>(results, context);
    }
}
