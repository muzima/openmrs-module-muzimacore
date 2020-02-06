package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.openmrs.Concept;

/**
 * A field that allows the selection of a specific day, month, and year. To handle both a date and
 * time, see {@see DateTimeField}.
 */

public class DateField implements FormField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private String defaultValue;
	
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private boolean allowFutureDates = false;
	
	private String js = null;
	
	public DateField(Concept concept, Locale locale, String label, boolean allowFutureDate, Date defaultDate) {
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.fieldLabel = label;
		this.setAllowFutureDates(allowFutureDate);
		this.defaultValue = dateFormat.format(defaultDate);
		
	}
	
	public DateField(Concept concept, Locale locale, String label) {
		this(concept, locale, label, false, null);
	}
	
	public DateField(Concept concept, Locale locale, String label, boolean allowFutureDate) {
		this(concept, locale, label, allowFutureDate, null);
	}
	
	public DateField(Concept concept, Locale locale, String label, Date defaultDate) {
		this(concept, locale, label, false, defaultDate);
	}
	
	@Override
	public String generateHtml() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"form-group\">\r\n" + "    <label for=\"" + this.name + "\">" + this.fieldLabel);
		if (required) {
			sb.append("<span class=\"required\">*</span>");
		}
		sb.append("</label>\r\n" + "    <input class=\"form-control datepicker");
		if (allowFutureDates) {
			sb.append(" future-date");
		} else {
			sb.append(" past-date");
		}
		sb.append("\" id=\"" + this.name + "\" name=\"" + this.name + "\" type=\"text\" readonly=\"readonly\"");
		if (defaultValue != null) {
			sb.append(" value=\"" + this.defaultValue + "\"");
		}
		if (required) {
			sb.append("required=\"required\"");
		}
		sb.append(">\r\n" + "</div>\r\n");
		return sb.toString();
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = dateFormat.format(defaultValue);
		
	}
	
	@Override
	public String getJs() {
		if (this.js != null) {
			return this.js;
		}
		return "";
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
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public String getDataConcept() {
		return dataConcept;
	}
	
	public void setDataConcept(String dataConcept) {
		this.dataConcept = dataConcept;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	public boolean isAllowFutureDates() {
		return allowFutureDates;
	}
	
	public void setAllowFutureDates(boolean allowFutureDates) {
		this.allowFutureDates = allowFutureDates;
	}
	
}
