package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.openmrs.Concept;

public class TimeField implements FormField {
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private Date defaultValue;
	
	private boolean hidden;
	
	private boolean hideSeconds = false;
	
	private String js = null;
	
	public TimeField(Concept concept, Locale locale, String label, Date defaultTime) {
		this.setName(FieldFactory.createNameAttributeFromConcept(concept, locale));
		this.setDataConcept(FieldFactory.createDataConceptAttributeFromConcept(concept, locale));
		this.setFieldLabel(label);
		this.defaultValue = defaultTime;
		
	};
	
	@Override
	public String generateHtml() {
		Calendar valAsCal = null;
		if (defaultValue != null) {
			valAsCal = Calendar.getInstance();
			valAsCal.setTime(defaultValue);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group\">\r\n" + "    <label for=\"timeField\">" + this.fieldLabel);
		if (required) {
			sb.append("<span class=\"required\">*</span>");
		}
		sb.append("</label>\r\n"
		        + "<div name=\"timeField\" class=form-control onblur=\"timeChange()\" onmouseout=\"timeChange()\" onmouseup=\"timeChange()\" onchange=\"timeChange()\">\r\n");
		
		if (hidden) {
			sb.append("<input type=\"hidden\" name=\"").append(this.name).append("hours")
			        .append("\" value=\"" + new SimpleDateFormat("HH").format(defaultValue) + "\"/>\r\n");
			sb.append("<input type=\"hidden\" name=\"").append(this.name).append("minutes")
			        .append("\" value=\"" + new SimpleDateFormat("mm").format(defaultValue) + "\"/>\r\n");
			if (!hideSeconds) {
				sb.append("<input type=\"hidden\" name=\"").append(this.name).append("seconds")
				        .append("\" value=\"" + new SimpleDateFormat("ss").format(defaultValue) + "\"/>\r\n");
			}
		} else {
			sb.append("<select  id=\"").append(this.name).append(".hours").append("\">\r\n");
			for (int i = 0; i <= 23; ++i) {
				String label = "" + i;
				if (label.length() == 1)
					label = "0" + label;
				sb.append("<option value=\"" + i + "\"");
				if (valAsCal != null) {
					if (valAsCal.get(Calendar.HOUR_OF_DAY) == i)
						sb.append(" selected=\"true\"");
				}
				sb.append(">" + label + "</option>\r\n");
			}
			sb.append("</select>\r\n");
			sb.append(":");
			sb.append("<select  id=\"").append(this.name).append(".minutes").append("\">\r\n");
			for (int i = 0; i <= 59; ++i) {
				String label = "" + i;
				if (label.length() == 1)
					label = "0" + label;
				sb.append("<option value=\"" + i + "\"");
				if (valAsCal != null) {
					if (valAsCal.get(Calendar.MINUTE) == i)
						sb.append(" selected=\"true\"");
				}
				sb.append(">" + label + "</option>\r\n");
			}
			sb.append("</select>\r\n");
			if (!hideSeconds) {
				sb.append("<select  id=\"").append(this.name).append(".seconds").append("\">\r\n");
				for (int i = 0; i <= 59; ++i) {
					String label = "" + i;
					if (label.length() == 1)
						label = "0" + label;
					sb.append("<option value=\"" + i + "\"");
					if (valAsCal != null) {
						if (valAsCal.get(Calendar.SECOND) == i)
							sb.append(" selected=\"true\"");
					}
					sb.append(">" + label + "</option>\r\n");
				}
				sb.append("</select>\r\n");
			}
		}
		sb.append("<input hidden type=\"text\" id=\"" + this.name + "\" name=\"" + this.name + "\"/>\r\n");
		sb.append("</div>\r\n</div>\r\n");
		setJs();
		return sb.toString();
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Date) defaultValue;
		
	}
	
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	@Override
	public boolean isRequired() {
		return required;
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
	
	public Date getDefaultValue() {
		return defaultValue;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isHideSeconds() {
		return hideSeconds;
	}
	
	public void setHideSeconds(boolean hideSeconds) {
		this.hideSeconds = hideSeconds;
	}
	
	@Override
	public String getJs() {
		if (this.js != null) {
			return this.js;
		}
		return "";
	}
	
	private void setJs() {
		StringBuilder sb = new StringBuilder();
		sb.append(
		    "    \r\n" + "    let hour = document.getElementById(\"" + FieldFactory.escapeJs(this.name) + ".hours\");\r\n"
		            + "    let min = document.getElementById(\"" + FieldFactory.escapeJs(this.name) + ".minutes\");\r\n");
		if (!hideSeconds) {
			sb.append("    let sec = document.getElementById(\"" + FieldFactory.escapeJs(this.name) + ".seconds\");\r\n");
		}
		sb.append("    let times = document.getElementById(\"" + FieldFactory.escapeJs(this.name) + "\");\r\n" + "    \r\n"
		        + "    let timeChange = function() {\r\n" + "        times.value = hour.value+\":\"+min.value");
		if (!hideSeconds) {
			sb.append("+\":\"+sec.value; \r\n");
		}
		sb.append("    }\r\n");
		this.js = sb.toString();
	}
}
