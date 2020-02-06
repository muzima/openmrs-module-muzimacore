package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Htmlform2MuzimaTransform {
	
	private HtmlGenerator htmlGenerator;
	
	private String htmlformXml;
	
	private String muzimaHtml;
	
	public Htmlform2MuzimaTransform() {
	}
	
	/**
	 * Creates the HTML for a mUzima Form given the xml of an HtmlForm. This method uses the
	 * HtmlFormGenerator to process any HTML Form Entry-specific tags and returns pure HTML as used
	 * in mUzima forms
	 *
	 * @param xml the xml string of the htmlform we want to convert
	 * @return mUzima formatted html5 form
	 * @throws Exception
	 */
	private String createForm(String xml) throws Exception {
		
		xml = htmlGenerator.substituteCharacterCodesWithAsciiCodes(this.htmlformXml);
		xml = htmlGenerator.stripComments(xml);
		xml = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(xml);
		xml = htmlGenerator.applyRepeats(xml);
		xml = htmlGenerator.applyTranslations(xml);
		xml = htmlGenerator.applyTags(xml);
		xml = htmlGenerator.wrapInDiv(xml);
		return xml;
	}
	
	public void transformHtmlToMuzima() {
		try {
			this.muzimaHtml = createForm(this.htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("Error during conversion");
		}
	}
	
	public String getMuzimaHtml() {
		return this.muzimaHtml;
	}
	
	public void setHtmlformXml(String htmlformXmlString) {
		this.htmlformXml = htmlformXmlString;
	}
}
