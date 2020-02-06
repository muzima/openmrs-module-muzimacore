package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.openmrs.Concept;

public class TimeFieldMain {
	
	private String fieldLabel = "observation time";
	
	private String name = "sureshtime";
	
	private String dataConcept = "1915^DATE OF OBS^99DCT";
	
	private boolean required = true;
	
	protected final SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");
	
	//private String initialValue = timeFormat.format(new Date());
	
	private Date defaultValue = new Date();
	
	private boolean hidden;
	
	private boolean hideSeconds = true;
	
	protected String js = setJs();
	
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
		sb.append("<script>");
		sb.append(this.js);
		sb.append("</script>");
		
		return sb.toString();
		
	}
	
	protected String setJs() {
		StringBuilder sb = new StringBuilder();
		sb.append("    \r\n" + "    let hour = document.getElementById(\"" + this.name + ".hours\");\r\n"
		        + "    let min = document.getElementById(\"" + this.name + ".minutes\");\r\n");
		if (!hideSeconds) {
			sb.append("    let sec = document.getElementById(\"" + this.name + ".seconds\");\r\n");
		}
		sb.append("    let times = document.getElementById(\"" + this.name + "\");\r\n" + "    \r\n"
		        + "    let timeChange = function() {\r\n" + "        times.value = hour.value+\":\"+min.value");
		if (!hideSeconds) {
			sb.append("+\":\"+sec.value; \r\n");
		}
		sb.append("    }\r\n");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		TimeFieldMain tf = new TimeFieldMain();
		System.out.print(tf.generateHtml());
	}
}
