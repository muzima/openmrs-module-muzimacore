package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * generates html code for radio button {@code <input type="radio"/>}
 */

public class RadioButtonsField extends SingleOptionField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private String answerSeparator = null;
	
	/**
	 * Default Constructor
	 */
	public RadioButtonsField() {
		super();
	}
	
	public RadioButtonsField(Concept concept, Locale locale, String label) {
		this.setName(FieldFactory.createNameAttributeFromConcept(concept, locale));
		this.setDataConcept(FieldFactory.createDataConceptAttributeFromConcept(concept, locale));
		this.fieldLabel = label;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group\">\r\n <div class=\"form-group\">\r\n <span><strong>" + this.fieldLabel);
		if (this.required) {
			sb.append(" <span class=\"required\">*</span>");
		}
		sb.append(" </strong></span> \r\n");
		
		for (int i = 0; i < getOptions().size(); ++i) {
			Option option = getOptions().get(i);
			boolean selected = option.isSelected();
			if (!selected)
				selected = getDefaultValue() == null ? option.getValue().equals("")
				        : getDefaultValue().equals(option.getValue());
			sb.append("<div class=\"radio\">\r\n" + "            <label>\r\n" + "                <input name=\"" + this.name
			        + "\" type=\"radio\"\r\n" + "                       data-concept=\"" + this.dataConcept + "\"\r\n"
			        + "                       value=\"" + option.getValue() + "\">\r\n" + option.getLabel() + "\r\n"
			        + "            </label>\r\n" + "        </div>");
			if (i < getOptions().size() - 1) {
				sb.append(getAnswerSeparator());
			}
		}
		
		sb.append(" </div>\r\n" + "</div>");
		return sb.toString();
	}
	
	/**
	 * @return the answerSeparator
	 */
	public String getAnswerSeparator() {
		if (answerSeparator == null)
			answerSeparator = "&#160;";
		return answerSeparator;
	}
	
	/**
	 * @param answerSeparator the answerSeparator to set
	 */
	public void setAnswerSeparator(String answerSeparator) {
		this.answerSeparator = answerSeparator;
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
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
}
