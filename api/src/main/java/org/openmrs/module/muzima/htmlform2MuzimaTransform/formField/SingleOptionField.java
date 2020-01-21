package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.OpenmrsObject;

/**
 * This represents a single field on a form which presents several coded options, of which only one
 * may be selected, such as a dropdown, or a group of radio buttons.
 */
public abstract class SingleOptionField implements FormField {
	
	private String defaultValue;
	
	private List<Option> options;
	
	/**
	 * Default Constructor
	 */
	public SingleOptionField() {
	}
	
	/*
	 *    Returns the initial value set on this Field
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		if (defaultValue == null)
			this.defaultValue = null;
		else {
			if (defaultValue instanceof OpenmrsObject) {
				this.defaultValue = ((OpenmrsObject) defaultValue).getId().toString();
			} else {
				this.defaultValue = defaultValue.toString();
			}
		}
		
	}
	
	/**
	 * Adds an Option to this Widget
	 * 
	 * @param option
	 */
	public void addOption(Option option) {
		if (options == null)
			options = new ArrayList<Option>();
		options.add(option);
	}
	
	/**
	 * Returns all Options for this Widget
	 * 
	 * @return
	 */
	public List<Option> getOptions() {
		return options;
	}
	
	/**
	 * Sets all Options for this Widget
	 * 
	 * @param options
	 */
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
}
