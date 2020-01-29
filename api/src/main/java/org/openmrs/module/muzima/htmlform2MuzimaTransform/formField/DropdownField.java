package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * Generates code for a dropdown (select) form field like
 * {@code <select name="..."><option value="...">...</option></select>}
 */
public class DropdownField extends SingleOptionField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private Integer size;
	
	private String js = null;
	
	public DropdownField() {
	}
	
	public DropdownField(Integer size) {
		this.size = size;
	}
	
	public DropdownField(Concept concept, Locale locale, String label, Integer size) {
		this.size = size;
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.fieldLabel = label;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group\">\r\n" + "            <label for=\"" + this.name + "\">" + this.fieldLabel);
		if (this.required) {
			sb.append(" <span class=\"required\">*</span>");
		}
		sb.append("</label>");
		sb.append("<select id=\"" + this.name + "\" name=\"" + this.name + "\"");
		if (this.required) {
			sb.append(" required =\"required \" ");
		}
		sb.append(" data-concept=" + this.dataConcept);
		if (size != null) {
			if (size == 999) {
				size = getOptions().size() + 1; // Add one to make sure all elements show up without scrollbar
			}
			sb.append(" size=").append("\"" + size.intValue() + "\"");
		}
		sb.append(">");
		
		for (int i = 0; i < getOptions().size(); ++i) {
			Option option = getOptions().get(i);
			boolean selected = option.isSelected();
			if (!selected)
				selected = getDefaultValue() == null ? option.getValue().equals("")
				        : getDefaultValue().equals(option.getValue());
			sb.append("<option value=\"").append(option.getValue()).append("\"");
			if (selected)
				sb.append(" selected=\"true\"");
			sb.append(">");
			sb.append(option.getLabel());
			sb.append("</option>");
		}
		sb.append("</select>");
		
		return null;
	}
	
	@Override
	public String getJs() {
		if (this.js != null) {
			return this.js;
		}
		return "";
	}
	
}
