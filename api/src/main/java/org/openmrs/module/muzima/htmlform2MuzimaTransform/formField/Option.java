package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * Represents a form field Option
 */
public class Option {
	
	private String label;
	private String value;
	private boolean selected = false;
	
	public Option() {
		value = "";
		label = "";
	}
	
	/**
	 * the constructor below is intended for use in autocomplete fields to generate 'source' to be
	 * used in jquery autocomplete ui
	 */
	public Option(Concept concept, Locale locale) {
		this.label = concept.getName(locale, false).getName();
		this.value = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
	}
	
	public Option(Concept concept, String label, Locale locale, boolean selected) {
		this.label = label;
		this.value = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
	}
	
	public Option(String label, String value, boolean selected) {
		this.label = label;
		this.value = value;
		this.selected = selected;
	}
	
	public Option(Concept concept, Locale locale, boolean selected) {
		this.label = concept.getName(locale, false).getName();
		this.value = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.selected = selected;
	}
	
	/**
	 * Gets the label to use with the Option
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the label to use with the Option
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Gets the value for the Option
	 *
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value for the Option
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns true/false whether or not the Option is selected
	 * 
	 * @return true/false is selected
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets whether or not the Option is selected
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * If the Option has a value, returns the value, otherwise returns the label
	 * 
	 * @return value or label
	 */
	public String getValueOrLabel() {
		if (value != null)
			return value;
		else
			return label;
	}
}
