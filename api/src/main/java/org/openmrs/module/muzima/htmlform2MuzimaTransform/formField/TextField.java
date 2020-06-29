package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Locale;

import org.openmrs.Concept;

/**
 * implements <input type="text"/> and <input type="textarea"/>
 */

public class TextField implements FormField {
	
	private Boolean textArea = false;
	
	private Integer textFieldSize;
	
	private Integer textAreaRows;
	
	private Integer textAreaColumns;
	
	private String defaultValue;
	
	private Integer textFieldMaxLength;
	
	private String placeholder;
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private String js = null;
	
	/**
	 * Default constructor implements the text field as a simple input field, like
	 * {@code <input type="text"/>}.
	 */
	public TextField(Concept concept, String label, Locale locale) {
		this(concept, locale, label, false);
		
	}
	
	/**
	 * If textArea parameter is set to True, implement this field as a {@code <textarea>}.
	 * 
	 * @param textArea
	 */
	public TextField(Concept concept, Locale locale, String label, Boolean textArea) {
		this.textArea = textArea;
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.fieldLabel = label;
		
	}
	
	/**
	 * Implements the field as a {@code <input type="text">} with the specified size.
	 * 
	 * @param size
	 */
	public TextField(Concept concept, Locale locale, String label, Integer size) {
		this(concept, locale, label, false);
		textFieldSize = size;
	}
	
	/**
	 * Implements the field as a {@code <textarea>} with the specified numbers of rows and columns.
	 *
	 * @param rows
	 * @param columns
	 */
	public TextField(Concept concept, Locale locale, String label, Integer rows, Integer columns) {
		this(concept, locale, label, true);
		textAreaRows = rows;
		textAreaColumns = columns;
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		
		if (textArea) {
			sb.append("\n<div class=\"form-group freetext\">\n" + "<label for=\"" + this.name + "\">" + this.fieldLabel);
			
			if (required) {
				sb.append("<span class=\"required error-message\">*</span>");
			}
			sb.append(" </label>\n" + "<textarea class=\"form-group\" name=\"" + this.name + "\" id=\"" + this.name + "\"");
			
			if (textAreaRows != null)
				sb.append(" rows=\"" + textAreaRows + "\"");
			if (textAreaColumns != null)
				sb.append(" cols=\"" + textAreaColumns + "\"");
			if (textFieldMaxLength != null && textFieldMaxLength.intValue() > 0) {
				sb.append(" maxlength=\"" + textFieldMaxLength.intValue() + "\"");
			}
			if (placeholder != null) {
				sb.append(" placeholder=\"" + placeholder + "\"");
			}
			if (defaultValue != null) {
				sb.append(" value=\"" + defaultValue + "\"");
			}
			if (required) {
				sb.append(" required=\"required\"");
			}
			sb.append("> </textarea>\n"
					+ "</div>\n");
		} else {
			sb.append("<div class=\"form-group freetext\">\n"
					+ "    <label for=\"" + this.name + "\">" + this.fieldLabel);
			if (required) {
				sb.append("<span class=\"required\">*</span>");
			}
			sb.append("</label>\n"
					+ "    <input class=\"form-control\" id=\"" + this.name + "\" name=\"" + this.name
			        + "\" type=\"text\" data-concept=\"" + this.dataConcept + "\"");
			if (textFieldSize != null)
				sb.append(" size=\"" + textFieldSize + "\"");
			if (textFieldMaxLength != null && textFieldMaxLength.intValue() > 0) {
				sb.append(" maxlength=\"" + textFieldMaxLength.intValue() + "\"");
			}
			if (placeholder != null) {
				sb.append(" placeholder=\"" + placeholder + "\"");
			}
			if (defaultValue != null)
				sb.append(" value=\"" + defaultValue + "\"");
			if (required) {
				sb.append(" required=\"required\"");
			}
			sb.append("\">\n"
					+ "</div>\n");
		}
		setJs();
		return sb.toString();
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (String) defaultValue;
	}
	
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}
	
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
	public String getPlaceholder() {
		return placeholder;
	}
	
	private void setJs() {
		String jsString = null;
		if (!(textFieldMaxLength == null)) {
			jsString = "\r\n$(formId).validate({\n"
					+ " 		 rules: {\n"
					+ 				FieldFactory.escapeJs(this.name) + ": {\n"
			        + "     			required: true,\n"
					+ "      			maxlength: " + this.textFieldMaxLength + "\n"
					+ "		    	}\n"
			        + "  		}\n"
					+ "		});\n";
			this.js = jsString;
		}
		
	}
	
	@Override
	public String getJs() {
		if (this.js != null) {
			return this.js;
		}
		return "";
	}
	
	public void setTextFieldMaxLength(Integer maxlength) {
		this.textFieldMaxLength = maxlength;
		
	}
	
}
