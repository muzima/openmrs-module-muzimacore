package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.util.LocaleUtility;
//import org.openmrs.module.htmlformentry.handler.AttributeDescriptor;
//import org.openmrs.module.htmlformentry.handler.IteratingTagHandler;
//import org.openmrs.module.htmlformentry.handler.TagHandler;
//import org.openmrs.module.htmlformentry.matching.ObsGroupEntity;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides methods to take a {@code <htmlform>...</htmlform>} xml block and turns it into HTML to
 * be displayed as a form in a web browser. It can apply the {@code <macros>...</macros>} section,
 * and replace tags like {@code <obs/>}.
 */
public class HtmlGenerator {
	
	/**
	 * Takes an XML string, finds the {@code <macros></macros>} section in it, and applies those
	 * substitutions
	 * <p/>
	 * For example the following input:
	 * <p/>
	 * <pre>
	 * {@code
	 * <htmlform>
	 *     <macros>
	 *          count=1, 2, 3
	 *     </macros>
	 *     You can count like $count
	 * </htmlform>
	 * }
	 * </pre>
	 * <p/>
	 * Would produce the following output:
	 * <p/>
	 * <pre>
	 * {@code
	 * <htmlform>
	 *     You can count like 1, 2, 3
	 * </htmlform>
	 * }
	 * </pre>
	 *
	 * @param xml the xml string to process for macros
	 * @return the xml string with after macro substitution
	 * @throws Exception
	 */
	public String applyMacros(String xml) throws Exception {
		Document doc = Htmlform2MuzimaTransformUtil.stringToDocument(xml);
		Node content = Htmlform2MuzimaTransformUtil.findChild(doc, "htmlform");
		Node macrosNode = Htmlform2MuzimaTransformUtil.findChild(content, "macros");
		
		// if there are no macros defined, we just return the original xml unchanged
		if (macrosNode == null) {
			return xml;
		}
		
		// One way to define macros is simply as the text content of the macros node.  This is left for backwards compatibility
		Properties macros = new Properties();
		String macrosText = macrosNode.getTextContent();
		if (macrosText != null) {
			macros.load(new ByteArrayInputStream(macrosText.getBytes()));
		}
		
		// Another way to define macros is as child tags to the macros node.
		NodeList children = macrosNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if ("macro".equals(node.getNodeName())) {
				String key = Htmlform2MuzimaTransformUtil.getNodeAttribute(node, "key", "");
				if (StringUtils.isBlank(key)) {
					throw new IllegalArgumentException("Macros must define a 'key' attribute");
				}
				String value = Htmlform2MuzimaTransformUtil.getNodeAttribute(node, "value", "");
				//below is used to handle velocity expressions, TODO decide on handling of velocity expressions
				//				if (StringUtils.isBlank(value)) {
				//					String expression = Htmlform2MuzimaTransformUtil.getNodeAttribute(node, "expression", "");
				//					if (StringUtils.isBlank(expression)) {
				//						throw new IllegalArgumentException("Macros must define either a 'value' or 'expression' attribute");
				//					}
				//					if (session != null) {
				//						value = session.evaluateVelocityExpression("$!{" + expression + "}");
				//					} else {
				//						value = expression;
				//					}
				//				}
				macros.put(key, value);
			}
		}
		
		// now remove the macros node
		content.removeChild(macrosNode);
		
		// switch back to String mode from the document so we can use string utilities to substitute
		xml = Htmlform2MuzimaTransformUtil.documentToString(doc);
		
		// substitute any macros we found
		for (Object temp : macros.keySet()) {
			String key = (String) temp;
			String value = macros.getProperty(key, "");
			xml = xml.replace("$" + key, value);
		}
		
		return xml;
	}
	
	/**
	 * Takes an XML string, finds the {@code <translations></translations>} section in it, and
	 * applies those substitutions
	 * <p/>
	 * <pre>
	 * {@code
	 * <htmlform>
	 * 		
	 *     <translations defaultLocale="en">
	 *       <code name="night_sweats">
	 *         <variant locale="en" value="night sweats"/>
	 *         <variant locale="fr" value="sueurs nocturnes"/>
	 * 		  </code>
	 *     </translations>
	 * </htmlform>
	 * } </pre> NOTE: it gets the defaultLocale specified in the form, if defaulLocale is not
	 * specified in the form, 'en' is made the defaultLocale
	 *
	 * @param xml the xml string to process for translations
	 * @return the xml string after translation substitutions have been made
	 * @throws Exception
	 */
	//	TODO handle the thrown exceptions
	public String applyTranslations(String xml) throws Exception {
		Translator translator = new Translator();
		Document doc = Htmlform2MuzimaTransformUtil.stringToDocument(xml);
		Node content = Htmlform2MuzimaTransformUtil.findChild(doc, "htmlform");
		Node transNode = Htmlform2MuzimaTransformUtil.findChild(content, "translations");
		
		//set the default locale of the Translator to the openmrs instance's default locale
		translator.setDefaultLocaleStr(LocaleUtility.getDefaultLocale().toString());
		
		// if there are no translations defined, we just return the original xml unchanged
		if (transNode == null) {
			return xml;
		}
		
		String defaultLocaleStr = Htmlform2MuzimaTransformUtil.getNodeAttribute(transNode, "defaultLocale", "en");
		
		NodeList codeNodeList = transNode.getChildNodes();
		for (int i = 0; i < codeNodeList.getLength(); i++) {
			Node codeNode = codeNodeList.item(i);
			if (codeNode.getNodeName().equalsIgnoreCase("code")) {
				String codeName = Htmlform2MuzimaTransformUtil.getNodeAttribute(codeNode, "name", null);
				if (codeName == null) {
					throw new IllegalArgumentException("All translation elements must contain a valid code name");
				}
				NodeList variantNodeList = codeNode.getChildNodes();
				for (int j = 0; j < variantNodeList.getLength(); ++j) {
					Node variantNode = variantNodeList.item(j);
					if (variantNode.getNodeName().equalsIgnoreCase("variant")) {
						String localeStr = Htmlform2MuzimaTransformUtil.getNodeAttribute(variantNode, "locale",
						    defaultLocaleStr);
						String valueStr = Htmlform2MuzimaTransformUtil.getNodeAttribute(variantNode, "value", null);
						if (valueStr == null) {
							throw new IllegalArgumentException("All variants must specify a value");
						}
						translator.addTranslation(localeStr, codeName, valueStr);
					}
				}
			}
		}
		
		// now remove the trans node
		content.removeChild(transNode);
		
		// switch back to String mode from the document so we can use string utilities to substitute
		xml = Htmlform2MuzimaTransformUtil.documentToString(doc);
		return xml;
	}
	
}
