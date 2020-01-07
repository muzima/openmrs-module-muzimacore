package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class HtmlGeneratorTest {
	
	private Document htmlForm;
	
	HtmlGenerator htmlGenerator;
	
	String expectedXml;
	
	public String removeWhiteSpaces(String input) {
		return input.replaceAll("\\s+", "");
	}
	
	@Before
	public void initialise() throws Exception {
		this.expectedXml = Htmlform2MuzimaTransformUtil.documentToString(
		    Htmlform2MuzimaTransformUtil.loadXML("src\\test\\resources\\htmlForm\\ExpectedOutputForm.xml"));
		
		this.htmlForm = Htmlform2MuzimaTransformUtil.loadXML("src\\test\\resources\\htmlForm\\sampleForm.xml");
		this.htmlGenerator = new HtmlGenerator();
	}
	
	@Test
	public void applyAllFormatingMethodsTest() throws Exception {
		String xml = Htmlform2MuzimaTransformUtil.documentToString(htmlForm);
		xml = htmlGenerator.applyMacros(xml);
		xml = htmlGenerator.applyRepeats(xml);
		xml = htmlGenerator.applyTranslations(xml);
		xml = htmlGenerator.stripComments(xml);
		xml = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(xml);
		xml = htmlGenerator.substituteCharacterCodesWithAsciiCodes(xml);
		
		assertEquals(removeWhiteSpaces(expectedXml), removeWhiteSpaces(xml));
		
	}
	
}
