package org.openmrs.module.muzima.web.utils;

import com.jayway.jsonpath.JsonPath;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Map;


/**
 * TODO: Write brief description about the class here.
 */
public class XmlJsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(XmlJsonUtil.class.getSimpleName());



    public static Map<String, Object> createPatientValuesFromPayload(Map<String, Object> map, final String payload) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new InputSource(new ByteArrayInputStream(payload.getBytes("utf-8"))));
            Element element = document.getDocumentElement();
            element.normalize();
            String other_identifier_type = "";
            Node patientNode = document.getElementsByTagName("patient").item(0);
            NodeList patientElementNodes = patientNode.getChildNodes();
            for (int i = 0; i < patientElementNodes.getLength(); i++) {
                Node patientElementNode = patientElementNodes.item(i);
                if (patientElementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element patientElement = (Element) patientElementNode;
                    if (patientElement.getTagName().equals("patient.middle_name")) {
                        map.put("middle_name", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.given_name")) {
                        map.put("given_name", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.family_name")) {
                        map.put("family_name", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient_identifier.identifier_type_id")) {
                        int identifierTypeId = Integer.parseInt(patientElement.getTextContent());
                        map.put("identifier_type_id", "" + identifierTypeId);
                    } else if (patientElement.getTagName().equals("patient.medical_record_number")) {
                        map.put("medical_record_number", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.sex")) {
                        map.put("sex", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.birthdate")) {
                        map.put("birth_date", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.birthdate_estimated")) {
                        map.put("birthdate_estimated", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.phone_number")) {
                        map.put("phone_number", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("person_attribute4")) {
                        map.put("person_attribute4", patientElement.getTextContent());
                    } else if (patientElement.getTagName().equals("patient.uuid")) {
                        map.put("patient_uuid", patientElement.getTextContent());
                    }
                }
            }

            Node otherIdentityNode = document.getElementsByTagName("other_identifier_type_group").item(0);
            NodeList otherIdentityElementNodes = otherIdentityNode.getChildNodes();
            for (int i = 0; i < otherIdentityElementNodes.getLength(); i++) {
                Node elementNode = otherIdentityElementNodes.item(i);
                if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elements = (Element) elementNode;
                    if (elements.getTagName().equals("other_identifier_type")) {
                        other_identifier_type = elements.getTextContent();
                        map.put("other_identifier_type", elements.getTextContent());
                        map.put("other_identifier_type_name", other_identifier_type);
                    }
                }
            }

            Node otherIdentityTypeNode = document.getElementsByTagName(other_identifier_type + "_identifier_type").item(0);
            NodeList otherIdentityTypeElementNodes = otherIdentityTypeNode.getChildNodes();
            for (int i = 0; i < otherIdentityTypeElementNodes.getLength(); i++) {
                Node elementNode = otherIdentityTypeElementNodes.item(i);
                if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elements = (Element) elementNode;
                    if (elements.getTagName().equals(other_identifier_type + "_value")) {
                        map.put("other_identifier_type_value", elements.getTextContent());

                    }
                }
            }

            Node encounterNode = document.getElementsByTagName("encounter").item(0);
            NodeList encounterElementNodes = encounterNode.getChildNodes();

            for (int i = 0; i < encounterElementNodes.getLength(); i++) {
                Node encounterElementNode = encounterElementNodes.item(i);
                if (encounterElementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element encounterElement = (Element) encounterElementNode;
                    if (encounterElement.getTagName().equals("encounter.location_id")) {

                        map.put("location_id", encounterElement.getTextContent());
                    } else if (encounterElement.getTagName().equals("encounter.provider_id")) {

                        map.put("provider_id", encounterElement.getTextContent());

                    } else if (encounterElement.getTagName().equals("encounter.encounter_datetime")) {

                        map.put("encounter_datetime", encounterElement.getTextContent());
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            logger.error("ParserConfigurationException" + map.toString());
            throw new QueueProcessorException(e);
        } catch (SAXException e) {
            logger.error("SAXException" + map.toString());
            throw new QueueProcessorException(e);
        } catch (Exception e) {
            logger.error("QueueProcessorException" + map.toString());
            throw new QueueProcessorException(e);
        }
        return map;
    }

    public static String createXmlFromJson(String jsonPayload) throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = createElement("form", doc);
        doc.appendChild(rootElement);
        Element patient = createElement("patient", doc);
        rootElement.appendChild(patient);
        processPatient(jsonPayload, patient, doc);
        Element encounter = createElement("encounter", doc);
        rootElement.appendChild(encounter);
        processEncounter(jsonPayload, encounter, doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }


    private static Element processPatient(String patientPayload, Element rootElement, Document doc) throws QueueProcessorException {


        String uuid = readAsString(patientPayload, "$['patient_uuid']");
        rootElement = setElementValues("patient.uuid", uuid, rootElement, doc);
        String medical_record_number = readAsString(patientPayload, "$['medical_record_number']");
        rootElement = setElementValues("patient.medical_record_number", medical_record_number, rootElement, doc);
        String family_name = readAsString(patientPayload, "$['family_name']");
        rootElement = setElementValues("patient.family_name", family_name, rootElement, doc);
        String middle_name = readAsString(patientPayload, "$['middle_name']");
        rootElement = setElementValues("patient.middle_name", middle_name, rootElement, doc);
        String given_name = readAsString(patientPayload, "$['given_name']");
        rootElement = setElementValues("patient.given_name", given_name, rootElement, doc);
        String sex = readAsString(patientPayload, "$['sex']");
        rootElement = setElementValues("patient.sex", sex, rootElement, doc);
        String identifier_type_id = readAsString(patientPayload, "$['identifier_type_id']");
        rootElement = setElementValues("patient_identifier.identifier_type_id", identifier_type_id, rootElement, doc);
        String birth_date = readAsString(patientPayload, "$['birth_date']");
        rootElement = setElementValues("patient.birthdate", birth_date, rootElement, doc);
        String birthdate_estimated = readAsString(patientPayload, "$['birthdate_estimated']");
        rootElement = setElementValues("patient.birthdate_estimated", birthdate_estimated, rootElement, doc);
        String person_attribute4 = readAsString(patientPayload, "$['person_attribute4']");
        rootElement = setElementValues("person_attribute4", person_attribute4, rootElement, doc);
        String phone_number = readAsString(patientPayload, "$['phone_number']");
        rootElement = setElementValues("patient.phone_number", phone_number, rootElement, doc);
        Element otherIdentiier = createElement("other_identifier_type_group", doc);
        rootElement.appendChild(otherIdentiier);
        processOtherIdentifier(patientPayload, otherIdentiier, doc);
        String other_identifier_type_name = readAsString(patientPayload, "$['other_identifier_type_name']");
        Element otherIdentiierType = createElement(other_identifier_type_name + "_identifier_type", doc);
        rootElement.appendChild(otherIdentiierType);
        processOtherIdentifierType(patientPayload, otherIdentiierType, doc);
        return rootElement;
    }

    private static Element processEncounter(String patientPayload, Element rootElement, Document doc) throws QueueProcessorException {

        String location_id = readAsString(patientPayload, "$['location_id']");
        rootElement = setElementValues("encounter.location_id", location_id, rootElement, doc);
        String provider_id = readAsString(patientPayload, "$['provider_id']");
        rootElement = setElementValues("encounter.provider_id", provider_id, rootElement, doc);
        String encounter_datetime = readAsString(patientPayload, "$['encounter_datetime']");
        rootElement = setElementValues("encounter.encounter_datetime", encounter_datetime, rootElement, doc);
        return rootElement;
    }

    private static Element processOtherIdentifier(String patientPayload, Element rootElement, Document doc) throws QueueProcessorException {
        String location_id = readAsString(patientPayload, "$['other_identifier_type']");
        rootElement = setElementValues("other_identifier_type", location_id, rootElement, doc);
        return rootElement;
    }

    private static Element processOtherIdentifierType(String patientPayload, Element rootElement, Document doc) throws QueueProcessorException {
        String name = readAsString(patientPayload, "$['other_identifier_type_name']");
        String location_id = readAsString(patientPayload, "$['other_identifier_type_value']");
        rootElement = setElementValues(name + "_value", location_id, rootElement, doc);
        return rootElement;
    }

    public static String readAsString(final String jsonObject, final String path) {
        String returnedString = null;
        try
        {
            returnedString = JsonPath.read(jsonObject, path);
        } catch (Exception e) {
            logger.error("Unable to read string value with path: " + path);
        }
        return returnedString;
    }

    public static Element createElement(String element, Document doc) {
        Element rootElement = doc.createElement(element);
        return rootElement;
    }

    public static Element setAttributeToElement(String attributeId, String attributeValue, Element rootElement, Document doc) {
        Attr attr = doc.createAttribute(attributeId);
        attr.setValue(attributeValue);
        rootElement.setAttributeNode(attr);
        return rootElement;
    }

    public static Element setElementValues(String elementName, String elementValue, Element rootElement, Document doc) {
        Element docElement = doc.createElement(elementName);
        docElement.appendChild(doc.createTextNode(elementValue));
        rootElement.appendChild(docElement);
        return rootElement;
    }
}
