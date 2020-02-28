package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * A checkbox field, like {@code <input type="checkbox"/>}
 */
public class CheckboxField extends SingleOptionField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private String js = null;
	
	public CheckboxField() {
		super();
	}
	
	public CheckboxField(Concept concept, Locale locale, String label) {
		this.setName(FieldFactory.createNameAttributeFromConcept(concept, locale));
		this.setDataConcept(FieldFactory.createDataConceptAttributeFromConcept(concept, locale));
		this.fieldLabel = label;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"section\">\r\n" + "<h4>" + this.fieldLabel);
		if (this.required) {
			sb.append(" <span class=\"required\">*</span>");
		}
		sb.append(" </h4> \r\n");
		
		for (int i = 0; i < getOptions().size(); ++i) {
			Option option = getOptions().get(i);
			boolean selected = option.isSelected();
			if (!selected)
				selected = getDefaultValue() == null ? option.getValue().equals("")
				        : getDefaultValue().equals(option.getValue());
			sb.append("<div class=\"form-group\">\r\n" + "            <label  class=\"font-normal\">\r\n"
			        + "                <input id=\"" + option.getLabel() + "_" + this.name + "\" type=\"checkbox\"\r\n"
			        + "                       data-concept=\"" + this.dataConcept + "\"\r\n"
			        + "                       value=\"" + option.getValue() + "\">\r\n" + option.getLabel() + "\r\n"
			        + "            </label>\r\n" + "        </div>");
			
		}
		
		sb.append("</div>");
		return sb.toString();
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
	
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}
	
	@Override
	public String getJs() {
		if (this.js != null) {
			return this.js;
		}
		return "";
	}
}
