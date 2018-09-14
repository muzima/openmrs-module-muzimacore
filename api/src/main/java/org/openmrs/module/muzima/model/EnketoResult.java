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
package org.openmrs.module.muzima.model;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class EnketoResult {
    private String transform;
    DocumentBuilder documentBuilder;
    XPathFactory xPathFactory;

    public EnketoResult(String transform) throws ParserConfigurationException {
        this.transform = transform;
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xPathFactory = XPathFactory.newInstance();
    }

    public String getForm() throws DocumentException {
        if (!hasResult()) return "";
        org.dom4j.Document document = new SAXReader().read(new StringReader(transform));
        return document.getRootElement().element("form").asXML();
    }

    public String getModel() throws DocumentException {
        if (!hasResult()) return "";
        org.dom4j.Document document = new SAXReader().read(new StringReader(transform));
        return document.getRootElement().element("model").asXML();
    }

    public String getResult() throws DocumentException {
        return transform;
    }

    public boolean hasResult() {
        return transform != null && !transform.isEmpty();
    }

    public String getModelAsJson() throws DocumentException {
        return getModel();
    }
}
