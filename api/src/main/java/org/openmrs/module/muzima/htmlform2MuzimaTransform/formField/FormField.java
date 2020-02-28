package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

/*
 * This represents a field on a form such as text, textarea, number, select, radio etc.
 *  
 */
public interface FormField {
	
	/**
	 * called to generate html5 code corresponding to an muzima tag
	 * 
	 * @return html code as String
	 */
	public String generateHtml();
	
	public void setDefaultValue(Object defaultValue);
	
	public String getJs();
	
	public void setRequired(boolean required);
	
	public boolean isRequired();
	
}
