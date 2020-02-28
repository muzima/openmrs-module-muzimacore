package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler.HtmlGenerator;

public class Htmlform2MuzimaTransform {
	
	private HtmlGenerator htmlGenerator = new HtmlGenerator();
	
	public Htmlform2MuzimaTransform() {
	}
	
	/**
	 * Transforms the xml of an HtmlForm to the appropriate mUzima form html5 string. This method
	 * uses the HtmlFormGenerator to process any HTML Form Entry-specific tags and returns pure HTML
	 * as used in mUzima forms.
	 */
	public String convertHtml2muzima(String htmlformXml) {
		
		String xml = htmlformXml;
		try {
			htmlformXml = htmlGenerator.applyMacros(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during applyMacros ", e);
			
		}
		try {
			htmlformXml = htmlGenerator.applyRepeats(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during applyRepeats() ", e);
		}
		try {
			htmlformXml = htmlGenerator.applyTranslations(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during applyTranslations() ", e);
		}
		try {
			htmlformXml = htmlGenerator.stripComments(htmlformXml);
		}
		catch (Exception e) {
			
			throw new RuntimeException("exception during stripComments()", e);
		}
		try {
			htmlformXml = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during convertSpecialCharactersWithinLogicAndVelocityTests()");
		}
		try {
			htmlformXml = htmlGenerator.substituteAsciiCodesWithCharacterCodes(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during substituteAsciiCodesWithCharacterCodes()");
		}
		
		try {
			htmlformXml = htmlGenerator.applyTags(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during applyTags()", e);
		}
		try {
			htmlformXml = htmlGenerator.cleanHtml(htmlformXml);
		}
		catch (Exception e) {
			throw new RuntimeException("exception during cleaning()", e);
		}
		return htmlformXml;
		
	}

}
