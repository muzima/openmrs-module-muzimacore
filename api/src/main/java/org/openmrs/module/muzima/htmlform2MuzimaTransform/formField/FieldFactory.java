package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;

/**
 * Contains shortcut methods to instantiate fields and utility methods.
 */
public class FieldFactory {
	
	/**
	 * Formats a value for display as HTML.
	 * 
	 * @param value to display
	 * @return the HTML to display the value
	 */
	public static String displayValue(String value) {
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		value = value.replace("\n", "<br/>");
		return "<span class=\"value\">" + value + "</span>";
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
			
			//l = concept.getName(locale, false).getName() + " " + units;
			l = concept.getName(locale, false).getName();
			
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
	 * Utility method to escape strings to be used in javascript
	 */
	public static String escapeJs(String s) {
		s = s.replaceAll("\n", "\\\\n");
		s = s.replaceAll("'", "\\\\'");
		s = s.replaceAll("\"", "\\\\\"");
		s = s.replaceAll("[.]", "\\\\\\\\.");
		
		return s;
	}
}
