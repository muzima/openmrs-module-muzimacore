package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.Obs;

/**
 * A field that implements a upload button to upload images/files, like
 * {@code <input type="file"/>}. The attribute conceptId mentioned in the obs tag has to be the id
 * of a concept with datatype 'complex'.
 */
//TODO handle defaultValue
public class UploadField implements FormField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private Obs defaultValue;
	
	private boolean required;
	
	public UploadField(Concept concept, Locale locale, String label) {
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.fieldLabel = label;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group\">\r\n" + "    <label for=\"" + this.name + "\">" + this.fieldLabel);
		if (required) {
			sb.append("<span class=\"required\">*</span>");
		}
		sb.append("</label>\r\n" + "    <input class=\"form-control\" id=\"" + this.name + "\" name=\"" + this.name
		        + "\"\r\n" + "           type=\"file\" data-concept=\"" + this.dataConcept + "\" />");
		return sb.toString();
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Obs) defaultValue;
	}
	
	public String getFieldLabel() {
		return fieldLabel;
	}
	
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDataConcept() {
		return dataConcept;
	}
	
	public void setDataConcept(String dataConcept) {
		this.dataConcept = dataConcept;
	}
	
	public Obs getDefaultValue() {
		return defaultValue;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
