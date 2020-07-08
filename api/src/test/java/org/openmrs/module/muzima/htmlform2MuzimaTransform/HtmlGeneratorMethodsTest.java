package org.openmrs.module.muzima.htmlform2MuzimaTransform;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.muzima.htmlform2MuzimaTransform.taghandler.HtmlGenerator;

public class HtmlGeneratorMethodsTest {
	
	private HtmlGenerator htmlGenerator;
	
	@Before
	public void initialise() {
		this.htmlGenerator = new HtmlGenerator();
	}
	
	public String removeWhiteSpaces(String input) {
		return input.replaceAll("\\s+", "");
	}
	
	@Test
	public void applyMacrosTest() throws Exception {
		String htmlForm = "<htmlform>\r\n" + "    <macros>\r\n" + "        lightgrey=#e0e0e0\r\n"
		        + "        lightblue=#e0e0ff\r\n" + "        darkblue=#4444ff\r\n" + "    </macros>\r\n"
		        + "    <div style=\"background-color: $lightblue\">This is a pleasant light blue color</div>\r\n"
		        + "</htmlform>";
		htmlForm = htmlGenerator.applyMacros(htmlForm);
		String expected = "<htmlform>\r\n"
		        + "  <div style=\"background-color: #e0e0ff\">This is a pleasant light blue color</div>\r\n" + "</htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void applyRepeatsTest1() throws Exception {
		String htmlForm = "<htmlform><repeat>\r\n" + "    <template>\r\n"
		        + "        <obsgroup groupingConceptId=\"1295\">\r\n" + "            <tr>\r\n"
		        + "                <td><obs conceptId=\"1297\" answerConceptId=\"{concept}\" answerLabel=\"{effect}\" labelText=\"\"/></td>\r\n"
		        + "                <td><obs conceptId=\"3063\"/></td>\r\n" + "            </tr>\r\n"
		        + "        </obsgroup>\r\n" + "    </template>\r\n"
		        + "    <render concept=\"6355\" effect=\"Nausées/vomissements\"/>\r\n"
		        + "    <render concept=\"16\" effect=\"Diarrhée\"/>\r\n" + "</repeat></htmlform>";
		htmlForm = htmlGenerator.applyRepeats(htmlForm);
		
		String expected = "<htmlform><obsgroup groupingConceptId=\"1295\">\r\n" + "            <tr>\r\n"
		        + "                <td>\r\n"
		        + "                  <obs conceptId=\"1297\" answerConceptId=\"6355\" answerLabel=\"Nausées/vomissements\" \r\n"
		        + "                       labelText=\"\"/>\r\n" + "               </td>\r\n" + "                <td>\r\n"
		        + "                  <obs conceptId=\"3063\"/>\r\n" + "               </td>\r\n" + "            </tr>\r\n"
		        + "        </obsgroup>\r\n" + "      \r\n" + "		       <obsgroup groupingConceptId=\"1295\">\r\n"
		        + "            <tr>\r\n" + "                <td>\r\n"
		        + "                  <obs conceptId=\"1297\" answerConceptId=\"16\" answerLabel=\"Diarrhée\" \r\n"
		        + "                       labelText=\"\"/>\r\n" + "               </td>\r\n" + "                <td>\r\n"
		        + "                  <obs conceptId=\"3063\"/>\r\n" + "               </td>\r\n" + "            </tr>\r\n"
		        + "        </obsgroup></htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void applyRepeatTest2() throws Exception {
		String htmlForm = "<htmlform><repeat with=\"['664','No Complaints'],['832','Weight Loss'],['777','Nausea']\">\r\n"
		        + "<obs conceptId=\"1069\" answerConceptId=\"{0}\" answerLabel=\"{1}\" style=\"checkbox\" /><br/>\r\n"
		        + "</repeat></htmlform>";
		htmlForm = htmlGenerator.applyRepeats(htmlForm);
		
		String expected = "<htmlform><obs conceptId=\"1069\" answerConceptId=\"664\" answerLabel=\"No Complaints\" style=\"checkbox\" /><br/>\r\n"
		        + "<obs conceptId=\"1069\" answerConceptId=\"832\" answerLabel=\"Weight Loss\" style=\"checkbox\" /><br/>\r\n"
		        + "<obs conceptId=\"1069\" answerConceptId=\"777\" answerLabel=\"Nausea\" style=\"checkbox\" /><br/></htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void applyTranslationsWithDefaultLocaleTest() throws Exception {
		String htmlForm = "<htmlform><translations defaultLocale=\"fr\">\r\n" + "    <code name=\"night_sweats\">\r\n"
		        + "        <variant locale=\"en\" value=\"night sweats\"/>\r\n"
		        + "        <variant locale=\"fr\" value=\"sueurs nocturnes\"/>\r\n" + "    </code>\r\n"
		        + "</translations>\r\n" + "<obs conceptId=\"1234\" labelCode=\"night_sweats\"/>\r\n" + "or\r\n"
		        + "<lookup expression=\"fn.message('night_sweats')\"/></htmlform>";
		htmlForm = htmlGenerator.applyTranslations(htmlForm);
		String expected = "<htmlform><obs conceptId=\"1234\" labelCode=\"sueurs nocturnes\"/>\r\n" + "or\r\n"
		        + "<lookup expression=\"fn.message('sueurs nocturnes')\"/></htmlform>";
		;
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void applyTranslationsWithNoDefaultLocaleTest() throws Exception {
		String htmlForm = "<htmlform><translations> \r\n" + "     	<code name=\"night_sweats\">   \r\n"
		        + "     	   <variant locale=\"en\" value=\"night sweats\"/> \r\n"
		        + "     	   <variant locale=\"fr\" value=\"sueurs nocturnes\"/>\r\n" + "     	</code>\r\n"
		        + "     	<code name=\"fivr\">   \r\n" + "     	   <variant locale=\"en\" value=\"fever\"/> \r\n"
		        + "     	   <variant locale=\"fr\" value=\"feeevee\"/>\r\n" + "     	</code>\r\n" + " 	</translations> "
		        + "<obs conceptId=\"1234\" labelCode=\"night_sweats\"/></htmlform>";
		htmlForm = htmlGenerator.applyTranslations(htmlForm);
		
		String expected = "<htmlform><obs conceptId=\"1234\" labelCode=\"night sweats\" /></htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void StripCommentsTest() throws Exception {
		String htmlForm = "<htmlform><!--this is a comment it should not be showing after formatiing--></htmlform>";
		htmlForm = htmlGenerator.stripComments(htmlForm);
		String expected = "<htmlform></htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void convertSpecialCharactersWithinLogicAndVelocityTests() throws Exception {
		String htmlForm = "<htmlform><includeIf velocityTest=\"!(($patient.gender == 'F') || ($patient.gender == 'M')) && (! $patient.age)\">\r\n"
		        + "  <b><font color='darkorange' size='+2'>ERROR: Unknown gender or date of birth.  Please update patient demographics before filling out this form.</font></b>\r\n"
		        + "</includeIf></htmlform>";
		htmlForm = htmlGenerator.convertSpecialCharactersWithinLogicAndVelocityTests(htmlForm);
		String expected = "<htmlform><includeIf velocityTest=\"!(($patient.gender == 'F') || ($patient.gender == 'M')) &amp;&amp;(! $patient.age)\">\r\n"
		        + "  <b><font color='darkorange' size='+2'>ERROR: Unknown gender or date of birth.  Please update patient demographics before filling out this form.</font></b>\r\n"
		        + "</includeIf></htmlform>";
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
	
	@Test
	public void substituteCharacterCodesWithAsciiCodesTest() throws Exception {
		String htmlForm = "<htmlform>this contains non&#160;breaking&#160;space</htmlform>";
		String expected = "<htmlform>this contains non&nbsp;breaking&nbsp;space</htmlform>";
		htmlForm = htmlGenerator.substituteAsciiCodesWithCharacterCodes(htmlForm);
		assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(htmlForm));
	}
}
