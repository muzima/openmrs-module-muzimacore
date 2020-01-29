package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * A Togglecheckbox field, like {@code <input type="checkbox"/>} for generating html for <obs ...
 * style="checkbox" toggle="...">
 */
public class ToggleCheckbox implements FormField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private Concept answerConcept;
	
	private String answerLabel;
	
	private Object defaultValue;
	
	private String value;
	
	private boolean toggleDimInd = false;
	
	private String toggleTarget;
	
	private boolean disabled = false;
	
	private String js;
	
	//	public ToggleCheckbox() {
	//	}
	//	
	//	public ToggleCheckbox(String value) {
	//		this.value = value;
	//	}
	//	
	//	public ToggleCheckbox(String label, String value) {
	//		this.fieldLabel = label;
	//		this.value = value;
	//	}
	
	public ToggleCheckbox(Concept concept, Concept ansConcept, Locale locale, String fieldLabel, String ansLabel,
	    String toggleTarget) {
		this.fieldLabel = fieldLabel;
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.toggleTarget = toggleTarget;
		this.answerLabel = ansLabel;
		this.value = FieldFactory.createDataConceptAttributeFromConcept(ansConcept, locale);
	}
	
	public ToggleCheckbox(Concept concept, Concept ansConcept, Locale locale, String fieldLabel, String ansLabel,
	    String toggleTarget, boolean toggleDimInd) {
		this.fieldLabel = fieldLabel;
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.toggleTarget = toggleTarget;
		this.answerLabel = ansLabel;
		this.value = FieldFactory.createDataConceptAttributeFromConcept(ansConcept, locale);
		this.toggleTarget = toggleTarget;
		this.toggleDimInd = toggleDimInd;
	}
	
	public ToggleCheckbox(Concept concept, Concept ansConcept, Locale locale, String fieldLabel, String ansLabel,
	    String toggleTarget, boolean toggleDimInd, boolean disabled) {
		this.fieldLabel = fieldLabel;
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.toggleTarget = toggleTarget;
		this.answerLabel = ansLabel;
		this.value = FieldFactory.createDataConceptAttributeFromConcept(ansConcept, locale);
		this.toggleTarget = toggleTarget;
		this.toggleDimInd = toggleDimInd;
		this.disabled = disabled;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append(
		    "<div class=\"form-group\">\r\n" + "    <label for=\"" + this.name + "\">" + this.fieldLabel + "</label>\r\n"
		            + "    <input class=\"form-control\" id=\"" + this.name + "\" name=\"" + this.name + "\"\r\n"
		            + "           type=\"checkbox\" data-concept=\"" + this.dataConcept + "\">\r\n" + "			value=\"");
		
		if (defaultValue != null && !"".equals(defaultValue))
			sb.append(" checked=\"true\"");
		if (toggleTarget != null && toggleTarget.trim().length() > 0)
			sb.append(" toggle" + (toggleDimInd ? "Dim" : "Hide") + "=\"" + toggleTarget + "\"");
		if (disabled) {
			sb.append(" disabled=\"disabled\"");
		}
		sb.append("/>\r\n" + "</div>");
		setJs();
		return sb.toString();
	}
	
	/**
	 * Gets the value attribute for the checkbox.
	 * 
	 * @return value of the field
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value attribute for the checkbox.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the text label to display before the checkbox.
	 * 
	 * @return the label
	 */
	public String getFieldLabel() {
		return fieldLabel;
	}
	
	/**
	 * Sets the text label to display after the checkbox.
	 * 
	 * @param label
	 */
	public void setFieldLabel(String label) {
		this.fieldLabel = label;
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
		
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public String getToggleTarget() {
		return toggleTarget;
	}
	
	public void setToggleTarget(String toggleTarget) {
		this.toggleTarget = toggleTarget;
	}
	
	public boolean isToggleDimInd() {
		return toggleDimInd;
	}
	
	public void setToggleDimInd(boolean toggleDimInd) {
		this.toggleDimInd = toggleDimInd;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public String getJs() {
		setJs();
		return this.js;
	}
	
	public void setJs() {
		this.js = "function() {\r\n" + "  var target = $(this).attr(\"toggleHide\");\r\n"
		        + "  if ($(this).is(\":checked\")) {\r\n" + "    $(\"#\" + target + \", .\" + target).fadeIn();\r\n"
		        + "  } else {\r\n" + "    $(\"#\" + target + \", .\" + target).hide();\r\n"
		        + "    clearContainerInputs($(\"#\" + target + \", .\" + target));\r\n" + "  }\r\n" + "}";
		
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
	
	public Concept getAnswerConcept() {
		return answerConcept;
	}
	
	public void setAnswerConcept(Concept answerConcept) {
		this.answerConcept = answerConcept;
	}
	
	public String getAnswerLabel() {
		return answerLabel;
	}
	
	public void setAnswerLabel(String answerLabel) {
		this.answerLabel = answerLabel;
	}
	
}
