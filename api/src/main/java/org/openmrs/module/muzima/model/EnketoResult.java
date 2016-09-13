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
