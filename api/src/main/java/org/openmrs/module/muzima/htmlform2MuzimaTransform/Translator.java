package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Translator {
	
	private String defaultLocaleStr = "en";
	
	private Map<String, String> translationsMap = new HashMap<String, String>();
	
	public Translator() {
	}
	
	public Map<String, String> getTranslationsMap() {
		return translationsMap;
	}
	
	public void setDefaultLocale(String locale) {
		this.defaultLocaleStr = locale;
	}
	
	public String getDefaultLocale() {
		return this.defaultLocaleStr;
	}
	
	public void addTranslations(String code, String translationValue) {
		if (translationsMap == null) {
			translationsMap = new HashMap<String, String>();
		}
		
		translationsMap.put(code, translationValue);
		
	}
	
	public String translate(String xmlInput) {
		for (Map.Entry<String, String> transMap : translationsMap.entrySet()) {
			xmlInput = xmlInput.replace(transMap.getKey(), transMap.getValue());
		}
		return xmlInput;
	}
}
