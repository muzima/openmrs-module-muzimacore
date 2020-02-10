package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Htmlform2MuzimaTransform {
	
	private static final Logger log = LoggerFactory.getLogger(Htmlform2MuzimaTransform.class);
	
	private HtmlGenerator htmlGenerator = new HtmlGenerator();
	
	public Htmlform2MuzimaTransform() {
	}
	
	public String convertHtml2muzima(String htmlformXml) {
		
		String xml = htmlformXml;
		try {
			htmlformXml = htmlGenerator.applyMacros(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during apply macros {}", e);
			throw new RuntimeException("exception during apply macros {}", e);
			
		}
		try {
			htmlformXml = htmlGenerator.applyRepeats(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during applyRepeats(}", e);
			throw new RuntimeException("exception during applyRepeats {}", e);
		}
		try {
			htmlformXml = htmlGenerator.applyTranslations(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during applyTranslations( {}", e);
			throw new RuntimeException("exception during applyTranslations {}", e);
		}
		try {
			htmlformXml = htmlGenerator.stripComments(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during stripComments{}", e);
			throw new RuntimeException("exception during stripComments{}", e);
		}
		try {
			htmlformXml = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during convertSpecialCharactersWithinLogicAndVelocityTests{}", e);
			throw new RuntimeException("exception during convertSpecialCharactersWithinLogicAndVelocityTests");
		}
		try {
			htmlformXml = htmlGenerator.substituteCharacterCodesWithAsciiCodes(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during substituteCharacterCodesWithAsciiCodes{}", e);
			throw new RuntimeException("exception during substituteCharacterCodesWithAsciiCodes{}");
		}
		try {
			htmlformXml = htmlGenerator.applyTags(htmlformXml);
		}
		catch (Exception e) {
			log.debug("exception during applyTags", e);
			throw new RuntimeException("exception during applyTags", e);
		}
		return htmlformXml;
		
	}
	
	/**
	 * Transforms the HTML for a mUzima Form given the xml of an HtmlForm. This method uses the
	 * HtmlFormGenerator to process any HTML Form Entry-specific tags and returns pure HTML as used
	 * in mUzima forms
	 * 
	 * @sets this.muzimaHtml as mUzima formatted html5 form
	 * @throws Exception
	 */
	//	public String transformHtmlToMuzima(String htmlformXml) {
	//		try {
	//			String muzimaHtml = applyAllFormating(htmlformXml);
	//			return muzimaHtml;
	//		}
	//		catch (Exception e) {
	//			log.debug("exception during formatting {}", e);
	//			throw new RuntimeException("Error during conversion");
	//		}
	//	}

}
