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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HTMLConceptParser {
    public static final String DATA_CONCEPT_TAG = "data-concept";

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
}