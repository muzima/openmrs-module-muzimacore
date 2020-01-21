package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.compatibility.ConceptCompatibility;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.Htmlform2MuzimaTransformUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * implements <input type="number"/>
 */
public class NumberField implements FormField {
	
	private Number defaultValue;
	
	private boolean floatingPoint = true;
	
	private Double absoluteMinimum;
	
	private Double absoluteMaximum;
	
	private Integer numberFieldSize = 5;
	
	private String fieldLabel;
	
	private String name;
	
	private String dataConcept;
	
	private boolean required = false;
	
	private String js = null;
	
	/**
	 * Creates anumeric input field with certain absolute maximum and minimum values. Floating point
	 * numbers are allowed if floatingPoint=true.
	 * 
	 * @param absoluteMinimum
	 * @param absoluteMaximum
	 * @param floatingPoint
	 */
	public NumberField(Double absoluteMinimum, Double absoluteMaximum, boolean floatingPoint) {
		this.absoluteMinimum = absoluteMinimum;
		this.absoluteMaximum = absoluteMaximum;
		this.floatingPoint = floatingPoint;
	}
	
	/**
	 * Creates a numeric input field with certain absolute maximum and minimum values as defined by
	 * a specific numeric Concept
	 * 
	 * @param concept
	 * @param size, the size of the text field to render
	 */
	public NumberField(ConceptNumeric concept, Locale locale, String label, String size) {
		this(concept, locale, label, size, null, null);
	}
	
	/**
	 * Creates a numeric input field with certain absolute maximum and minimum values as defined by
	 * a specific numeric Concept, but allowing overriding
	 *
	 * @param concept
	 * @param size, the size of the text field to render
	 */
	public NumberField(ConceptNumeric concept, Locale locale, String label, String size, Double absoluteMinimum,
	    Double absoluteMaximum) {
		
		this.name = FieldFactory.createNameAttributeFromConcept(concept, locale);
		this.dataConcept = FieldFactory.createDataConceptAttributeFromConcept(concept, locale);
		this.fieldLabel = label;
		
		if (concept != null) {
			ConceptCompatibility conceptCompatibility = Context.getRegisteredComponent("htmlformentry.ConceptCompatibility",
			    ConceptCompatibility.class);
			
			setAbsoluteMaximum(absoluteMaximum != null ? absoluteMaximum : concept.getHiAbsolute());
			setAbsoluteMinimum(absoluteMinimum != null ? absoluteMinimum : concept.getLowAbsolute());
			
			setFloatingPoint(conceptCompatibility.isAllowDecimal(concept));
			if (size != null && !size.equals("")) {
				try {
					setNumberFieldSize(Integer.valueOf(size));
				}
				catch (Exception ex) {
					throw new IllegalArgumentException("Value for 'size' attribute in numeric obs must be a number.");
				}
			}
		}
	}
	
	@Override
	public String generateHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"form-group freetext\">\r\n" + "    <label for=\"" + this.name + "\">" + this.fieldLabel);
		
		if (required) {
			sb.append("<span class=\"required error-message\">*</span>");
		}
		sb.append("</label>\r\n" + "<input class=\"form-control\" id=\"" + this.name + "\" name=\"" + this.name + "\"\r\n"
		        + " type=\"number\" data-concept=\"" + this.dataConcept + "\"");
		if (defaultValue != null) {
			sb.append(" value=\"" + userFriendlyDisplay(defaultValue) + "\"");
		}
		if (absoluteMinimum != null) {
			sb.append(" min=\"" + absoluteMinimum + "\"");
		}
		
		if (absoluteMaximum != null) {
			sb.append(" max =\"" + absoluteMaximum + "\"");
		}
		if (defaultValue != null)
			sb.append(" value=\"" + defaultValue + "\"");
		if (required) {
			sb.append(" required=\"required\"");
		}
		sb.append("/>");
		//creates scripts that contain javascript to use for client side validation
		createJS();
		return sb.toString();
	}
	
	private String userFriendlyDisplay(Number number) {
		if (number == null) {
			return "";
		} else if (number.doubleValue() == number.intValue()) {
			return "" + number.intValue();
		} else {
			return "" + number.toString();
		}
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Number) defaultValue;
		
	}
	
	public Number getDefaultValue() {
		return defaultValue;
	}
	
	public boolean isFloatingPoint() {
		return floatingPoint;
	}
	
	public void setFloatingPoint(boolean floatingPoint) {
		this.floatingPoint = floatingPoint;
	}
	
	public Double getAbsoluteMinimum() {
		return absoluteMinimum;
	}
	
	public void setAbsoluteMinimum(Double absoluteMinimum) {
		this.absoluteMinimum = absoluteMinimum;
	}
	
	public Double getAbsoluteMaximum() {
		return absoluteMaximum;
	}
	
	public void setAbsoluteMaximum(Double absoluteMaximum) {
		this.absoluteMaximum = absoluteMaximum;
	}
	
	public Integer getNumberFieldSize() {
		return numberFieldSize;
	}
	
	public void setNumberFieldSize(Integer numberFieldSize) {
		this.numberFieldSize = numberFieldSize;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	//handles client side validation using jQuery validate plugin 
	private void createJS() {
		StringBuilder jsSb = new StringBuilder();
		if (absoluteMinimum != null && absoluteMaximum != null) {
			jsSb.append(" $(formId).validate({\r\n" + "        rules: {\r\n '" + name
			        + "': {\r\n number: true, \r\n range: [" + absoluteMinimum + "," + absoluteMaximum + "]");
			if (this.required) {
				jsSb.append(" \r\n required : true");
			}
			jsSb.append("\r\n  } } });");
		} else if (absoluteMaximum != null && absoluteMinimum == null) {
			jsSb.append(" $(formId).validate({\r\n" + "        rules: {\r\n '" + name + "': {\r\n number: true, \r\n max: "
			        + absoluteMaximum);
			if (this.required) {
				jsSb.append(" \r\n required : true");
			}
			jsSb.append("\r\n  } } });");
			
		} else if (absoluteMinimum != null && absoluteMaximum == null) {
			jsSb.append("$(formId).validate({\r\n" + "        rules: {\r\n '" + name + "': {\r\n number: true, \r\n min: "
			        + absoluteMinimum);
			if (this.required) {
				jsSb.append(" \r\n required : true");
			}
			jsSb.append("\r\n  } } });");
		}
		
		this.js = jsSb.toString();
	}
	
	public String getJs() {
		return js;
	}
	
}
