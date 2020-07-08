package org.openmrs.module.muzima.htmlform2MuzimaTransform.formField;

/*
* Represents a toggle attribute
*/

public class ToggleField {
	
	private String targetId;
	private String targetClass;
	private String style;
	
	public ToggleField() {
	}
	
	public ToggleField(String targetId, String targetClass, String style) {
		super();
		this.targetId = targetId;
		this.targetClass = targetClass;
		this.style = style;
	}
	
	public ToggleField(String rawAttr) {
		if (rawAttr != null && rawAttr.trim().length() > 0) {
			rawAttr = rawAttr.trim();
			
			if (isComplexToggleField(rawAttr)) {
				parseComplesToggleField(rawAttr);
			} else {
				// This is not considered a list of attributes
				targetId = sanitizeStringForHtmlAttribute(rawAttr);
			}
		}
	}
	
	private void parseComplesToggleField(String rawAttr) {
		rawAttr = rawAttr.replaceAll("\\{", "").replaceAll("\\}", "");
		String[] attrs = rawAttr.split(",");
		for (String attrPair : attrs) {
			if (attrPair.trim().length() > 0 && attrPair.indexOf(":") > 0) {
				String[] nameValue = attrPair.split(":");
				if (nameValue.length == 2) {
					String name = nameValue[0].trim();
					String value = nameValue[1].trim();
					if (name.equalsIgnoreCase("id")) {
						targetId = sanitizeStringForHtmlAttribute(value);
					} else if (name.equalsIgnoreCase("class")) {
						targetClass = sanitizeStringForHtmlAttribute(value);
					} else if (name.equalsIgnoreCase("style")) {
						style = sanitizeStringForHtmlAttribute(value);
					}
				}
			}
		}
	}
	
	private boolean isComplexToggleField(String rawAttr) {
		return rawAttr != null && rawAttr.trim().startsWith("{") && rawAttr.trim().endsWith("}") ? true : false;
	}
	
	public boolean isToggleDim() {
		return style != null && style.trim().equalsIgnoreCase("dim") ? true : false;
	}
	
	private String sanitizeStringForHtmlAttribute(String value) {
		return value.replaceAll("\"", "").replaceAll("'", "").trim();
	}
	
	public String getTargetId() {
		return targetId;
	}
	
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public String getTargetClass() {
		return targetClass;
	}
	
	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
}
