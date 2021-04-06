/*
 * Copyright (c) 2014. The Trustees of Indiana University.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license with additional
 * healthcare disclaimer. If the user is an entity intending to commercialize any application
 * that uses this code in a for-profit venture, please contact the copyright holder.
 */

package org.openmrs.module.muzima.utils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.javarosa.xform.parse.ValidationMessages;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openmrs.Concept;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import javax.management.relation.RelationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HTMLConceptParser {
    public static final String DATA_CONCEPT_TAG = "data-concept";
    public ValidationMessages validationMessages = new ValidationMessages();
    public Elements allElements;


    public List<String> parse(String html) {
        Set<String> concepts = new HashSet<String>();
        Document htmlDoc = Jsoup.parse(html);
        //Select all elements containing data-concept attr and is not a div.
        Elements elements = htmlDoc.select("*:not(div)[" + DATA_CONCEPT_TAG + "]");
        for (Element element : elements) {
            concepts.add(getConceptIdenttifier(element.attr(DATA_CONCEPT_TAG)));
        }
        return new ArrayList<String>(concepts);
    }

    public List<String> parseForm(String html) {
        Set<String> concepts = new HashSet<String>();
        Document htmlDoc = Jsoup.parse(html);
        //Select all elements containing data-concept attr and is not a div.
        Elements elements = htmlDoc.select("*:not(div)[" + DATA_CONCEPT_TAG + "]");
        for (Element element : elements) {
            concepts.add(getConceptIdenttifier(element.attr(DATA_CONCEPT_TAG)));
        }

        allElements = htmlDoc.getAllElements();
        for (Element conceptAnswer : allElements) {
            if(conceptAnswer.val().split("\\^").length ==3){
                concepts.add(getConceptIdenttifier(conceptAnswer.val()));
            }
        }

        return new ArrayList<String>(concepts);
    }

    private static String getConceptIdenttifier(String rawConceptName) {
        if (rawConceptName != null && rawConceptName.trim().length() > 0 && rawConceptName.split("\\^").length > 1) {
            return rawConceptName.split("\\^")[0];
        }
        return "";
    }

    public String createConceptMetadata(List<String> conceptIdentifiers) {
        ConceptService cs = Context.getConceptService();
        JSONArray conceptsArray = new JSONArray();
        for (String uuidOrId : conceptIdentifiers) {
            Concept concept;
            if (StringUtils.isNumeric(uuidOrId)) {
                int conceptId = Integer.parseInt(uuidOrId);
                concept = Context.getConceptService().getConcept(conceptId);
            } else {
                concept = Context.getConceptService().getConceptByUuid(uuidOrId);
            }
            if (concept != null) {
                JSONObject conceptJson = new JSONObject();
                conceptJson.put("uuid", concept.getUuid());
                conceptJson.put("name", concept.getDisplayString());
                conceptsArray.add(conceptJson);
            }
        }
        JSONObject js = new JSONObject();
        js.put("concepts", conceptsArray);
        return js.toJSONString();
    }

    public ValidationMessages validateForm(String html){
        validationMessages = validateConceptsMetaData(validationMessages,html);
        validationMessages = validateAttributeTypes(validationMessages,allElements);
        validationMessages = validatePersonIdentifierTypes(validationMessages,allElements);
        validationMessages = validateRelationshipTypes(validationMessages,allElements);
        return validationMessages;
    }

    public ValidationMessages validateConceptsMetaData(ValidationMessages validationMessages, String html) {
        List<String> conceptIdentifiers = parseForm(html);
        ConceptService cs = Context.getConceptService();
        JSONArray conceptsArray = new JSONArray();
        for (String uuidOrId : conceptIdentifiers) {
            Concept concept;
            if (StringUtils.isNumeric(uuidOrId)) {
                int conceptId = Integer.parseInt(uuidOrId);
                concept = Context.getConceptService().getConcept(conceptId);
            } else {
                concept = Context.getConceptService().getConceptByUuid(uuidOrId);
            }
            if (concept == null) {
                validationMessages.addError("Concept with ID or UUID = '"+uuidOrId+"' was not found in the concept dictionary.");
            }
        }
        return validationMessages;
    }

    public ValidationMessages validateAttributeTypes(ValidationMessages validationMessages, Elements elements){
        for (Element element : elements) {
            if("attribute_type_uuid".equals(element.attr("name")) && "input".equals(element.tagName())){
                PersonService personService = Context.getPersonService();
                PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(element.val());
                if(attributeType == null){
                    validationMessages.addError("Person Attribute Type with UUID = '"+element.val()+"' was not found in the database");
                }
            }else if("attribute_type_name".equals(element.attr("name")) && "input".equals(element.tagName())){
                PersonService personService = Context.getPersonService();
                PersonAttributeType attributeType = personService.getPersonAttributeTypeByName(element.val());
                if(attributeType == null){
                    validationMessages.addError("Person Attribute Type with name = '"+element.val()+"' was not found in the database");
                }
            }else if("attribute_type_uuid".equals(element.attr("name")) && "select".equals(element.tagName())){
                Elements options = element.select("select > option");
                for( Element option : options ) {
                    if (StringUtils.isNotEmpty(option.val()) && !option.equals("...")) {
                        PersonService personService = Context.getPersonService();
                        PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(option.attr("value"));
                        if(attributeType == null){
                            validationMessages.addError("Person Attribute Type with UUID = '"+option.attr("value")+"' was not found in the database");
                        }
                    }
                }
            }else if("attribute_type_name".equals(element.attr("name")) && "select".equals(element.tagName())){
                Elements options = element.select("select > option");
                for( Element option : options ) {
                    if (StringUtils.isNotEmpty(option.val()) && !option.equals("...")) {
                        PersonService personService = Context.getPersonService();
                        PersonAttributeType attributeType = personService.getPersonAttributeTypeByUuid(option.attr("value"));
                        if(attributeType == null){
                            validationMessages.addError("Person Attribute Type with UUID = '"+option.attr("value")+"' was not found in the database");
                        }
                    }
                }
            }
        }
        return validationMessages;
    }

    public ValidationMessages validatePersonIdentifierTypes(ValidationMessages validationMessages, Elements elements){
        for (Element element : elements) {
            if("identifier_type_uuid".equals(element.attr("name")) && "input".equals(element.tagName())){
                PatientService patientService = Context.getPatientService();
                PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(element.val());
                if(patientIdentifierType == null){
                    validationMessages.addError("Patient Identifier Type with UUID = '"+element.val()+"' was not found in the database");
                }
            }else if("identifier_type_name".equals(element.attr("name")) && "input".equals(element.tagName())){
                PatientService patientService = Context.getPatientService();
                PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByName(element.val());
                if(patientIdentifierType == null){
                    validationMessages.addError("Patient Identifier Type with name = '"+element.val()+"' was not found in the database");
                }
            }else if("identifier_type_uuid".equals(element.attr("name")) && "select".equals(element.tagName())){
                Elements options = element.select("select > option");
                for( Element option : options ) {
                    if (StringUtils.isNotEmpty(option.val()) && !option.equals("...")) {
                        PatientService patientService = Context.getPatientService();
                        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByUuid(option.attr("value"));
                        if(patientIdentifierType == null){
                            validationMessages.addError("Patient Identifier Type with UUID = '"+option.attr("value")+"' was not found in the database");
                        }
                    }
                }
            }else if("identifier_type_name".equals(element.attr("name")) && "select".equals(element.tagName())){
                Elements options = element.select("select > option");
                for( Element option : options ) {
                    if (StringUtils.isNotEmpty(option.val()) && !option.equals("...")) {
                        PatientService patientService = Context.getPatientService();
                        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierTypeByName(option.attr("value"));
                        if(patientIdentifierType == null){
                            validationMessages.addError("Patient Identifier Type with name = '"+option.attr("value")+"' was not found in the database");
                        }
                    }
                }
            }
        }
        return validationMessages;
    }

    public ValidationMessages validateRelationshipTypes(ValidationMessages validationMessages, Elements elements){
        Elements lists = elements.select("[name=person.relationshipType]");
        Elements options = lists.select("select > option");
        for( Element option : options ) {
            if (StringUtils.isNotEmpty(option.val()) && !option.equals("...")) {
                PersonService personService = Context.getPersonService();
                RelationshipType relationshipType = personService.getRelationshipTypeByUuid(option.attr("value"));
                if (relationshipType == null) {
                    validationMessages.addError("Relationship Type with UUID = '" + option.attr("value") + "' was not found in the database");
                }
            }
        }
        return validationMessages;
    }
}