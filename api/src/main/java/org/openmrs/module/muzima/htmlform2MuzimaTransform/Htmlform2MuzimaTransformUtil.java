package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.MatchMode;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterTypeEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.PatientEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.propertyeditor.UserEditor;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * utility methods used in tranforming Htmlforms to Muzima
 */
public class Htmlform2MuzimaTransformUtil {
	
	public static Log log = LogFactory.getLog(Htmlform2MuzimaTransformUtil.class);
	
	/**
	 * Converts a string to specified type
	 *
	 * @param val the string to convert
	 * @param clazz the type to convert to
	 * @return an instance of the specified type, with it's value set to val
	 */
	public static Object convertToType(String val, Class<?> clazz) {
		if (val == null)
			return null;
		if ("".equals(val) && !String.class.equals(clazz))
			return null;
		if (Location.class.isAssignableFrom(clazz)) {
			LocationEditor ed = new LocationEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (User.class.isAssignableFrom(clazz)) {
			UserEditor ed = new UserEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (Date.class.isAssignableFrom(clazz)) {
			// all HTML Form Entry dates should be submitted as yyyy-mm-dd
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setLenient(false);
				return df.parse(val);
			}
			catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else if (Double.class.isAssignableFrom(clazz)) {
			return Double.valueOf(val);
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return Integer.valueOf(val);
		} else if (Concept.class.isAssignableFrom(clazz)) {
			ConceptEditor ed = new ConceptEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (Drug.class.isAssignableFrom(clazz)) {
			DrugEditor ed = new DrugEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (Patient.class.isAssignableFrom(clazz)) {
			PatientEditor ed = new PatientEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (Person.class.isAssignableFrom(clazz)) {
			PersonEditor ed = new PersonEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else if (EncounterType.class.isAssignableFrom(clazz)) {
			EncounterTypeEditor ed = new EncounterTypeEditor();
			ed.setAsText(val);
			return ed.getValue();
		} else {
			return val;
		}
	}
	
	/**
	 * Loads a W3C XML document from a file.
	 * 
	 * @param filename The name of the file to be loaded
	 * @return a document object model object representing the XML file
	 * @throws Exception
	 */
	public static Document loadXML(String filename) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			return builder.parse(new File(filename));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts an xml string to a Document object
	 *
	 * @param xml the xml string to convert
	 * @return the resulting Document object
	 * @throws Exception
	 */
	public static Document stringToDocument(String xml) throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Disable XXE: security measure to prevent DOS, arbitrary-file-read, and possibly RCE
			dbf.setExpandEntityReferences(false);
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(xml)));
			return document;
		}
		catch (Exception e) {
			log.error("Error converting String to Document:\n" + xml);
			throw e;
		}
	}
	
	/**
	 * Converts a Document object to an xml string
	 *
	 * @param document the Document instance to convert
	 * @return the resulting xml string
	 * @throws Exception
	 */
	public static String documentToString(Document document) throws Exception {
		//set up a transformer
		Transformer trans = null;
		TransformerFactory transfac = TransformerFactory.newInstance();
		
		try {
			trans = transfac.newTransformer();
		}
		catch (TransformerException te) {
			System.out.println(Htmlform2MuzimaTransformConstants.ERROR_TRANSFORMER_1 + te);
		}
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, Htmlform2MuzimaTransformConstants.CONSTANT_YES);
		trans.setOutputProperty(OutputKeys.INDENT, Htmlform2MuzimaTransformConstants.CONSTANT_YES);
		trans.setOutputProperty(OutputKeys.METHOD, Htmlform2MuzimaTransformConstants.CONSTANT_XML);
		trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(document);
		try {
			trans.transform(source, result);
		}
		catch (TransformerException te) {
			System.out.println(Htmlform2MuzimaTransformConstants.ERROR_TRANSFORMER_2 + te);
		}
		String xmlString = sw.toString();
		
		return xmlString;
	}
	
	/**
	 * Retrieves a child Node by name
	 *
	 * @param content the parent Node
	 * @param name the name of the child Node
	 * @return the child Node with the specified name
	 */
	public static Node findChild(Node content, String name) {
		if (content == null)
			return null;
		NodeList children = content.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node node = children.item(i);
			if (name.equals(node.getNodeName()))
				return node;
		}
		return null;
	}
	
	/**
	 * Finds the first descendant of this node with the given tag name
	 * 
	 * @param node
	 * @param tagName
	 * @return
	 */
	public static Node findDescendant(Node node, String tagName) {
		if (node == null)
			return null;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (tagName.equals(node.getNodeName()))
				return node;
			Node matchingDescendant = findDescendant(child, tagName);
			if (matchingDescendant != null) {
				return matchingDescendant;
			}
		}
		return null;
	}
	
	/**
	 * Returns all the attributes associated with a Node
	 *
	 * @param node the Node to retrieve attributes from
	 * @return a Map containing all the attributes of the Node
	 */
	public static Map<String, String> getNodeAttributes(Node node) {
		Map<String, String> ret = new HashMap<String, String>();
		NamedNodeMap atts = node.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node attribute = atts.item(i);
			ret.put(attribute.getNodeName(), attribute.getNodeValue());
		}
		return ret;
	}
	
	/**
	 * Returns a specific attribute of a Node
	 *
	 * @param node the Node to retrieve the attribute from
	 * @param attributeName the name of the attribute to return
	 * @param defaultVal a default value to return if the attribute is not specified for the
	 *            selected Node
	 * @return
	 */
	public static String getNodeAttribute(Node node, String attributeName, String defaultVal) {
		String ret = getNodeAttributes(node).get(attributeName);
		return (ret == null ? defaultVal : ret);
	}
	
	/**
	 * @param node
	 * @return the contents of node as a String
	 */
	public static String getNodeContentsAsString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		}
		catch (TransformerException ex) {
			throw new RuntimeException("Error transforming node", ex);
		}
		return sw.toString();
	}
	
	/**
	 * Combines a Date object that contains only a date component (day, month, year) with a Date
	 * object that contains only a time component (hour, minute, second) into a single Date object
	 *
	 * @param date the Date object that contains date information
	 * @param time the Date object that contains time information
	 * @return a Date object with the combined date/time
	 */
	public static Date combineDateAndTime(Date date, Date time) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		if (time != null) {
			Calendar temp = Calendar.getInstance();
			temp.setTime(time);
			cal.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, temp.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, temp.get(Calendar.MILLISECOND));
		}
		return cal.getTime();
	}
	
	//TODO needed??
	/***
	 * Determines if the passed string is in valid uuid format By OpenMRS standards, a uuid must be
	 * 36 characters in length and not contain whitespace, but we do not enforce that a uuid be in
	 * the "canonical" form, with alphanumerics seperated by dashes, since the MVP dictionary does
	 * not use this format (We also are being slightly lenient and accepting uuids that are 37 or 38
	 * characters in length, since the uuid data field is 38 characters long)
	 */
	public static boolean isValidUuidFormat(String uuid) {
		if (uuid.length() < 36 || uuid.length() > 38 || uuid.contains(" ") || uuid.contains(".")) {
			return false;
		}
		
		return true;
	}
	
	//TODO needed??
	/**
	 * Evaluates the specified Java constant using reflection
	 * 
	 * @param fqn the fully qualified name of the constant
	 * @return the constant value
	 */
	protected static String evaluateStaticConstant(String fqn) {
		int lastPeriod = fqn.lastIndexOf(".");
		String clazzName = fqn.substring(0, lastPeriod);
		String constantName = fqn.substring(lastPeriod + 1);
		
		try {
			Class<?> clazz = Context.loadClass(clazzName);
			Field constantField = clazz.getField(constantName);
			Object val = constantField.get(null);
			return val != null ? String.valueOf(val) : null;
		}
		catch (Exception ex) {
			throw new IllegalArgumentException("Unable to evaluate " + fqn, ex);
		}
	}
	
	//TODO needed??
	/**
	 * Utility to return a copy of an Object. Copies all properties that are referencese by getters
	 * and setters and *are not* collection
	 * 
	 * @param source
	 * @return A copy of an object
	 * @throws Exception
	 */
	private static Object returnCopy(Object source) throws Exception {
		Class<? extends Object> clazz = source.getClass();
		Object ret = clazz.newInstance();
		Set<String> fieldNames = new HashSet<String>();
		List<Field> fields = new ArrayList<Field>();
		addSuperclassFields(fields, clazz);
		for (Field f : fields) {
			fieldNames.add(f.getName());
		}
		for (String root : fieldNames) {
			for (Method getter : clazz.getMethods()) {
				if (getter.getName().toUpperCase().equals("GET" + root.toUpperCase())
				        && getter.getParameterTypes().length == 0) {
					Method setter = getSetter(clazz, getter, "SET" + root.toUpperCase());
					//NOTE: Collection properties are not copied
					if (setter != null && methodsSupportSameArgs(getter, setter)
					        && !(getter.getReturnType().isInstance(Collection.class))) {
						Object o = getter.invoke(source, Collections.EMPTY_LIST.toArray());
						if (o != null) {
							setter.invoke(ret, o);
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * The Encounter.setProvider() contains the different overloaded methods and this filters the
	 * correct setter from those
	 * 
	 * @param clazz
	 * @param getter
	 * @param methodname
	 * @return
	 */
	private static Method getSetter(Class<? extends Object> clazz, Method getter, String methodname) {
		
		List<Method> setterMethods = getMethodCaseInsensitive(clazz, methodname);
		if (setterMethods != null && !setterMethods.isEmpty()) {
			if (setterMethods.size() == 1) {
				return setterMethods.get(0);
			} else if (setterMethods.size() > 1) {
				for (Method m : setterMethods) {
					Class<?>[] parameters = m.getParameterTypes();
					for (Class<?> parameter : parameters) {
						if (getter.getReturnType().equals(parameter)) {
							return m;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Performs a case insensitive search on a class for a method by name.
	 * 
	 * @param clazz
	 * @param methodName
	 * @return the found Method
	 */
	private static List<Method> getMethodCaseInsensitive(Class<? extends Object> clazz, String methodName) {
		
		List<Method> methodList = new ArrayList<Method>();
		for (Method m : clazz.getMethods()) {
			if (m.getName().toUpperCase().equals(methodName.toUpperCase())) {
				methodList.add(m);
				
			}
		}
		return methodList;
	}
	
	/**
	 * compares getter return types to setter parameter types
	 * 
	 * @param getter
	 * @param setter
	 * @return true if getter return types are the same as setter parameter types. Else false.
	 */
	private static boolean methodsSupportSameArgs(Method getter, Method setter) {
		if (getter != null && setter != null && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
		        && getter.getReturnType() != null && getter.getReturnType().equals(setter.getParameterTypes()[0]))
			return true;
		return false;
	}
	
	/**
	 * recurses through all superclasses of a class and adds the fields from that superclass
	 * 
	 * @param fields
	 * @param clazz
	 */
	private static void addSuperclassFields(List<Field> fields, Class<? extends Object> clazz) {
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		if (clazz.getSuperclass() != null) {
			addSuperclassFields(fields, clazz.getSuperclass());
		}
	}
	
	/**
	 * Given a Date object, returns a Date object for the same date but with the time component
	 * (hours, minutes, seconds & milliseconds) removed
	 */
	public static Date clearTimeComponent(Date date) {
		// Get Calendar object set to the date and time of the given Date object  
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		// Set time fields to zero  
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * @param s the string to conver to camelcase
	 * @return should return the passed in string in the camelcase format
	 */
	public static String toCamelCase(String s) {
		StringBuffer sb = new StringBuffer();
		String[] words = s.replaceAll("[^A-Za-z]", " ").replaceAll("\\s+", " ").trim().split(" ");
		
		for (int i = 0; i < words.length; i++) {
			if (i == 0)
				words[i] = words[i].toLowerCase();
			else
				words[i] = String.valueOf(words[i].charAt(0)).toUpperCase() + words[i].substring(1);
			
			sb.append(words[i]);
		}
		return sb.toString();
	}
	
	/**
	 * utility method that puts underscore '_' in between words in a string
	 * 
	 * @param s
	 * @return
	 */
	public static String addUnderScoreBetweenWord(String s) {
		String outString = s.toLowerCase().replaceAll("\\s", "_");
		
		return outString;
	}
	
	/**
	 * Utility method for htmlform tags converion creates muzimaform name attribute from a concept
	 * by converting the concept name to string separated with underscores.
	 * 
	 * @param concept
	 * @param locale
	 * @return
	 * @should include unit for numeric concepts.
	 */
	public static String createNameAttributeFromConcept(Concept concept, Locale locale) {
		String l;
		String name;
		
		if (concept.getDatatype().isNumeric()) {
			
			String units;
			
			if (concept instanceof ConceptNumeric) {
				units = ((ConceptNumeric) concept).getUnits();
			} else {
				ConceptNumeric asConceptNumeric = Context.getConceptService().getConceptNumeric(concept.getConceptId());
				if (asConceptNumeric == null) {
					units = null;
				}
				units = asConceptNumeric.getUnits();
			}
			
			l = concept.getName(locale, false).getName() + " " + units;
			
		} else {
			l = concept.getName(locale, false).getName();
			
		}
		name = addUnderScoreBetweenWord(l);
		return name;
		
	}
	
	/**
	 * Utility method for htmlform tags converion creates an muzimaform dataconcept attribute for a
	 * coded observation returns {conceptId}^{conceptName}^99DCT
	 * 
	 * @param concept
	 * @param locale
	 * @return
	 */
	
	public static String createDataConceptAttributeFromConcept(Concept concept, Locale locale) {
		String conceptId = concept.getId().toString();
		String conceptName;
		String dataConcept;
		
		if (concept.getDatatype().isNumeric()) {
			String units;
			if (concept instanceof ConceptNumeric) {
				units = ((ConceptNumeric) concept).getUnits();
			} else {
				ConceptNumeric asConceptNumeric = Context.getConceptService().getConceptNumeric(concept.getConceptId());
				if (asConceptNumeric == null) {
					units = null;
				}
				units = asConceptNumeric.getUnits();
			}
			
			conceptName = concept.getName(locale, false).getName() + " " + units;
			
		} else {
			conceptName = concept.getName(locale, false).getName();
			
		}
		dataConcept = conceptId + "^" + conceptName + "^99DCT";
		return dataConcept;
	}
	
	/**
	 * Find Drug by UUID
	 * 
	 * @param uuid
	 * @return
	 */
	public static Drug getDrug(String uuid) {
		Drug drug = null;
		if (StringUtils.isNotBlank(uuid)) {
			try {
				drug = Context.getConceptService().getDrugByUuid(uuid);
			}
			catch (Exception e) {
				log.error("Failed to find drug: ", e);
			}
		}
		return drug;
	}
	
	/**
	 * Get the concept by id where the id can either be: 1) an integer id like 5090 2) a mapping
	 * type id like "XYZ:HT" 3) a uuid like "a3e12268-74bf-11df-9768-17cfc9833272" 4) the fully
	 * qualified name of a Java constant that contains one of above
	 *
	 * @param id the concept identifier
	 * @return the concept if exist, else null
	 * @should find a concept by its conceptId
	 * @should find a concept by its mapping
	 * @should find a concept by its uuid
	 * @should find a concept by static constant
	 * @should return null otherwise
	 * @should find a concept by its mapping with a space in between
	 */
	public static Concept getConcept(String id) {
		return HtmlFormEntryUtil.getConcept(id);
	}
	
	/**
	 * Gets a concept by id, mapping, or uuid. (See #getConcept(String) for precise details.) If no
	 * concept is found, throws an IllegalArgumentException with the given message.
	 *
	 * @param id
	 * @param errorMessageIfNotFound
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Concept getConcept(String id, String errorMessageIfNotFound) throws IllegalArgumentException {
		
		Concept c = null;
		try {
			c = getConcept(id);
		}
		catch (Exception ex) {
			throw new IllegalArgumentException(errorMessageIfNotFound, ex);
		}
		if (c == null)
			throw new IllegalArgumentException(errorMessageIfNotFound);
		return c;
	}
	
	/**
	 * Get the location by:
	 * <ol>
	 * <li>an integer id like 5090</li>
	 * <li>a uuid like "a3e12268-74bf-11df-9768-17cfc9833272"</li>
	 * <li>a name like "Boston"</li>
	 * <li>an id/name pair like "501 - Boston" (this format is used when saving a location on a obs
	 * as a value text)</li>
	 * <li>"GlobalProperty:property.name"</li>
	 * <li>"UserProperty:propertyName"</li>
	 *
	 * @param id
	 * @param context
	 * @return the location if exist, else null
	 * @should find a location by its locationId
	 * @should find a location by name
	 * @should find a location by its uuid
	 * @should find a location by global property
	 * @should find a location by user property
	 * @should find a location by session attribute
	 * @should not fail if trying to find a location by session attribute and we have no session
	 * @should return null otherwise
	 */
	public static Location getLocation(String id) {
		return HtmlFormEntryUtil.getLocation(id);
	}
	
	/***
	 * Get the program by: 1)an integer id like 5090 or 2) uuid like
	 * "a3e12268-74bf-11df-9768-17cfc9833272" or 3) name of *associated concept* (not name of
	 * program), like "MDR-TB Program"
	 *
	 * @param id
	 * @return the program if exist, else null
	 * @should find a program by its id
	 * @should find a program by name of associated concept
	 * @should find a program by its uuid
	 * @should return null otherwise
	 */
	public static Program getProgram(String id) {
		return HtmlFormEntryUtil.getProgram(id);
	}
	
	/***
	 * Get the person by: 1)an integer id like 5090 or 2) uuid like
	 * "a3e12268-74bf-11df-9768-17cfc9833272" or 3) a username like "mgoodrich" or 4) an id/name
	 * pair like "5090 - Bob Jones" (this format is used when saving a person on a obs as a value
	 * text)
	 * 
	 * @param id
	 * @return the person if exist, else null
	 * @should find a person by its id
	 * @should find a person by its uuid
	 * @should find a person by username of corresponding user
	 * @should return null otherwise
	 */
	public static Person getPerson(String id) {
		return HtmlFormEntryUtil.getPerson(id);
	}
	
	/***
	 * Get the patient identifier type by: 1)an integer id like 5090 or 2) uuid like
	 * "a3e12268-74bf-11df-9768-17cfc9833272" or 3) a name like "Temporary Identifier"
	 * 
	 * @param id
	 * @return the identifier type if exist, else null
	 * @should find an identifier type by its id
	 * @should find an identifier type by its uuid
	 * @should find an identifier type by its name
	 * @should return null otherwise
	 */
	public static PatientIdentifierType getPatientIdentifierType(String id) {
		return HtmlFormEntryUtil.getPatientIdentifierType(id);
	}
	
	/**
	 * Translates a String into a Date.
	 * 
	 * @param value use "now" for the current timestamp, "today" for the current date with a
	 *            timestamp of 00:00, or a date string that can be parsed by SimpleDateFormat with
	 *            the format parameter.
	 * @param format the pattern SimpleDateTime will use to parse the value, if other than "now" or
	 *            "today".
	 * @return Date on success; null for an invalid value
	 * @throws IllegalArgumentException if a date string cannot be parsed with the format string you
	 *             provided
	 * @see java.text.SimpleDateFormat
	 * @should return a Date object with current date and time for "now"
	 * @shold return a Date with current date, but time of 00:00:00:00, for "today"
	 * @should return a Date object matching the value param if a format is specified
	 * @should return null for null value
	 * @should return null if format is null and value not in [ null, "now", "today" ]
	 * @should fail if date parsing fails
	 */
	public static Date translateDatetimeParam(String value, String format) {
		return HtmlFormEntryUtil.translateDatetimeParam(value, format);
	}
	
}
