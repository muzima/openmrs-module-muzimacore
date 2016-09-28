package org.openmrs.module.muzima.api.impl;

import net.sf.saxon.TransformerFactoryImpl;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.api.service.impl.MuzimaFormServiceImpl;
import org.openmrs.module.muzima.xForm2MuzimaTransform.EnketoXslTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ModelXml2JsonTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2HTML5Transformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2JavarosaTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.XslTransformPipeline;
import org.openmrs.module.xforms.Xform;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EnketoResultTest {
    @Test(expected = NullPointerException.class)
    public void getForm_shouldEmpty() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, DocumentException {
        EnketoResult enketoResult = new EnketoResult("<root><form><ul><li/><li/></ul></form></root>");
        assertThat(enketoResult.getModel(), isEmptyOrNullString());

        enketoResult = new EnketoResult("<root><model><x/><y/></model></root>");
        assertThat(enketoResult.getForm(), isEmptyOrNullString());
    }

    @Test
    public void getForm_shouldGetForm() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, DocumentException {
        String result = "<root><model><x/><y/></model><form><ul><li/><li/></ul></form></root>";
        EnketoResult enketoResult = new EnketoResult(result);
        assertThat(enketoResult.getForm(), is("<form><ul><li/><li/></ul></form>"));
    }

    @Test
    public void getForm_shouldGetModel() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, DocumentException {
        String result = "<root><model><x/><y/></model><form><ul><li/><li/></ul></form></root>";
        EnketoResult enketoResult = new EnketoResult(result);
        assertThat(enketoResult.getForm(), is("<form><ul><li/><li/></ul></form>"));
    }

    @Test
    public void test() throws IOException, DocumentException, TransformerException, ParserConfigurationException {
        MuzimaFormDAO muzimaFormDAO = mock(MuzimaFormDAO.class);
        TransformerFactory transformerFactory = new TransformerFactoryImpl();
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(transformerFactory, XslTransformPipeline.xform2HTML5Pipeline());
        ModelXml2JsonTransformer modelXml2JsonTransformer = new ModelXml2JsonTransformer(transformerFactory, XslTransformPipeline.modelXml2JsonXSLPipeline());
        ODK2JavarosaTransformer odk2JavarosaTransformer = new ODK2JavarosaTransformer(transformerFactory, XslTransformPipeline.modelXml2JsonXSLPipeline());
        ODK2HTML5Transformer odk2HTML5Transformer = new ODK2HTML5Transformer(transformerFactory, XslTransformPipeline.ODK2HTML5());
        MuzimaFormServiceImpl muzimaFormService = new MuzimaFormServiceImpl(muzimaFormDAO, enketoXslTransformer, modelXml2JsonTransformer, odk2JavarosaTransformer, odk2HTML5Transformer);
        Xform xform = new Xform();
        xform.setXformXml(xformXml);
        when(muzimaFormDAO.getXform(1)).thenReturn(xform);

        try {
            muzimaFormService.importExisting(1, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String xformXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xf:xforms xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:jr=\"http://openrosa.org/javarosa\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <xf:model id=\"openmrs_model\">\n" +
            "    <xf:instance id=\"openmrs_model_instance\">\n" +
            "      <form xmlns:xd=\"http://schemas.microsoft.com/office/infopath/2003\" xmlns:openmrs=\"/moduleServlet/formentry/forms/schema/20-1\" id=\"50\" name=\"Pediatric Return Visit Form 2\" uuid=\"1eff723d-58ed-427c-b770-17c5d6cd1566\" version=\"0.1\">\n" +
            "  <header>\n" +
            "    <enterer/>\n" +
            "    <date_entered/>\n" +
            "    <session/>\n" +
            "    <uid/>\n" +
            "  </header>\n" +
            "  <patient>\n" +
            "    <patient.birthdate openmrs_attribute=\"birthdate\" openmrs_table=\"patient\"/>\n" +
            "    <patient.birthdate_estimated openmrs_attribute=\"birthdate_estimated\" openmrs_table=\"patient\"/>\n" +
            "    <patient.family_name openmrs_attribute=\"family_name\" openmrs_table=\"patient_name\"/>\n" +
            "    <patient.given_name openmrs_attribute=\"given_name\" openmrs_table=\"patient_name\"/>\n" +
            "    <patient.medical_record_number openmrs_attribute=\"identifier\" openmrs_table=\"patient_identifier\"/>\n" +
            "    <patient.middle_name openmrs_attribute=\"middle_name\" openmrs_table=\"patient_name\"/>\n" +
            "    <patient.patient_id openmrs_attribute=\"patient_id\" openmrs_table=\"patient\"/>\n" +
            "    <patient.sex openmrs_attribute=\"gender\" openmrs_table=\"patient\"/>\n" +
            "    <patient.tribe openmrs_attribute=\"tribe\" openmrs_table=\"patient\"/>\n" +
            "    <patient_address.address1 openmrs_attribute=\"address1\" openmrs_table=\"patient_address\"/>\n" +
            "    <patient_address.address2 openmrs_attribute=\"address2\" openmrs_table=\"patient_address\"/>\n" +
            "  </patient>\n" +
            "  <encounter>\n" +
            "    <encounter.encounter_datetime openmrs_attribute=\"encounter_datetime\" openmrs_table=\"encounter\">'today()'</encounter.encounter_datetime>\n" +
            "    <encounter.location_id openmrs_attribute=\"location_id\" openmrs_table=\"encounter\"/>\n" +
            "    <encounter.provider_id openmrs_attribute=\"provider_id\" openmrs_table=\"encounter\" provider_id_type=\"PROVIDER.ID\"/>\n" +
            "  </encounter>\n" +
            "  <obs>\n" +
            "    <weight_kg openmrs_concept=\"5089^WEIGHT (KG)^99DCT\" openmrs_datatype=\"NM\">\n" +
            "        <date xsi:nil=\"true\"/>\n" +
            "        <time xsi:nil=\"true\"/>\n" +
            "        <value xsi:nil=\"true\"/>\n" +
            "    </weight_kg>\n" +
            "  </obs>\n" +
            "  <problem_list openmrs_concept=\"1284^PROBLEM LIST^99DCT\" openmrs_datatype=\"ZZ\">\n" +
            "    <problem_added multiple=\"0\" openmrs_concept=\"6042^PROBLEM ADDED^99DCT\" openmrs_datatype=\"CWE\">\n" +
            "        <date xsi:nil=\"true\"/>\n" +
            "        <time xsi:nil=\"true\"/>\n" +
            "        <value xsi:nil=\"true\"/>\n" +
            "    </problem_added>\n" +
            "    <problem_resolved multiple=\"0\" openmrs_concept=\"6097^PROBLEM RESOLVED^99DCT\" openmrs_datatype=\"CWE\">\n" +
            "        <date xsi:nil=\"true\"/>\n" +
            "        <time xsi:nil=\"true\"/>\n" +
            "        <value xsi:nil=\"true\"/>\n" +
            "    </problem_resolved>\n" +
            "  </problem_list>\n" +
            "  <other/></form>\n" +
            "    </xf:instance>\n" +
            "    <xf:bind id=\"patient.birthdate\" jr:preload=\"patient\" jr:preloadParams=\"birthDate\" locked=\"true()\" nodeset=\"/form/patient/patient.birthdate\" type=\"xsd:date\"/>\n" +
            "    <xf:bind id=\"patient.birthdate_estimated\" locked=\"true()\" nodeset=\"/form/patient/patient.birthdate_estimated\" type=\"xsd:boolean\"/>\n" +
            "    <xf:bind id=\"patient.family_name\" jr:preload=\"patient\" jr:preloadParams=\"familyName\" locked=\"true()\" nodeset=\"/form/patient/patient.family_name\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient.given_name\" jr:preload=\"patient\" jr:preloadParams=\"givenName\" locked=\"true()\" nodeset=\"/form/patient/patient.given_name\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient.medical_record_number\" jr:preload=\"patient\" jr:preloadParams=\"patientIdentifier\" locked=\"true()\" nodeset=\"/form/patient/patient.medical_record_number\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient.middle_name\" jr:preload=\"patient\" jr:preloadParams=\"middleName\" locked=\"true()\" nodeset=\"/form/patient/patient.middle_name\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient.patient_id\" jr:preload=\"patient\" jr:preloadParams=\"patientId\" nodeset=\"/form/patient/patient.patient_id\" required=\"true()\" type=\"xsd:int\" visible=\"false()\"/>\n" +
            "    <xf:bind id=\"patient.sex\" jr:preload=\"patient\" jr:preloadParams=\"sex\" locked=\"true()\" nodeset=\"/form/patient/patient.sex\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient.tribe\" locked=\"true()\" nodeset=\"/form/patient/patient.tribe\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient_address.address1\" locked=\"true()\" nodeset=\"/form/patient/patient_address.address1\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"patient_address.address2\" locked=\"true()\" nodeset=\"/form/patient/patient_address.address2\" type=\"xsd:string\"/>\n" +
            "    <xf:bind constraint=\". &lt;= today()\" id=\"encounter.encounter_datetime\" message=\"Encounter date cannot be after today\" nodeset=\"/form/encounter/encounter.encounter_datetime\" required=\"true()\" type=\"xsd:date\"/>\n" +
            "    <xf:bind id=\"encounter.location_id\" nodeset=\"/form/encounter/encounter.location_id\" required=\"true()\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"encounter.provider_id\" nodeset=\"/form/encounter/encounter.provider_id\" required=\"true()\" type=\"xsd:string\"/>\n" +
            "    <xf:bind constraint=\". &gt;= 0.0 and . &lt;= 250.0\" id=\"weight_kg\" message=\"value should be between 0.0 and 250.0 inclusive\" nodeset=\"/form/obs/weight_kg/value\" type=\"xsd:decimal\"/>\n" +
            "    <xf:bind id=\"problem_added\" nodeset=\"/form/problem_list/problem_added\"/>\n" +
            "    <xf:bind id=\"problem_resolved\" nodeset=\"/form/problem_list/problem_resolved\"/>\n" +
            "    <xf:bind id=\"problem_list_problem_added_value\" nodeset=\"/form/problem_list/problem_added/value\" type=\"xsd:string\"/>\n" +
            "    <xf:bind id=\"problem_list_problem_resolved_value\" nodeset=\"/form/problem_list/problem_resolved/value\" type=\"xsd:string\"/>\n" +
            "  </xf:model>\n" +
            "  <xf:group id=\"1\">\n" +
            "    <xf:label>Page1</xf:label>\n" +
            "    <xf:input bind=\"patient.birthdate\">\n" +
            "      <xf:label>BIRTHDATE</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.birthdate_estimated\">\n" +
            "      <xf:label>BIRTHDATE ESTIMATED</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.family_name\">\n" +
            "      <xf:label>FAMILY NAME</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.given_name\">\n" +
            "      <xf:label>GIVEN NAME</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.medical_record_number\">\n" +
            "      <xf:label>MEDICAL RECORD NUMBER</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.middle_name\">\n" +
            "      <xf:label>MIDDLE NAME</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.patient_id\">\n" +
            "      <xf:label>PATIENT ID</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.sex\">\n" +
            "      <xf:label>SEX</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient.tribe\">\n" +
            "      <xf:label>TRIBE</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient_address.address1\">\n" +
            "      <xf:label>ADDRESS1</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"patient_address.address2\">\n" +
            "      <xf:label>ADDRESS2</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:input bind=\"encounter.encounter_datetime\">\n" +
            "      <xf:label>ENCOUNTER DATETIME</xf:label>\n" +
            "    </xf:input>\n" +
            "    <xf:select1 bind=\"encounter.location_id\">\n" +
            "      <xf:label>LOCATION ID</xf:label>\n" +
            "      <xf:item id=\"4\">\n" +
            "        <xf:label>Chulaimbo [4]</xf:label>\n" +
            "        <xf:value>4</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"3\">\n" +
            "        <xf:label>Mosoriot Hospital [3]</xf:label>\n" +
            "        <xf:value>3</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"1\">\n" +
            "        <xf:label>Unknown Location [1]</xf:label>\n" +
            "        <xf:value>1</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"11\">\n" +
            "        <xf:label>Unknown Location 10 [11]</xf:label>\n" +
            "        <xf:value>11</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"12\">\n" +
            "        <xf:label>Unknown Location 11 [12]</xf:label>\n" +
            "        <xf:value>12</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"13\">\n" +
            "        <xf:label>Unknown Location 12 [13]</xf:label>\n" +
            "        <xf:value>13</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"14\">\n" +
            "        <xf:label>Unknown Location 13 [14]</xf:label>\n" +
            "        <xf:value>14</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"15\">\n" +
            "        <xf:label>Unknown Location 14 [15]</xf:label>\n" +
            "        <xf:value>15</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"16\">\n" +
            "        <xf:label>Unknown Location 15 [16]</xf:label>\n" +
            "        <xf:value>16</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"17\">\n" +
            "        <xf:label>Unknown Location 16 [17]</xf:label>\n" +
            "        <xf:value>17</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"18\">\n" +
            "        <xf:label>Unknown Location 17 [18]</xf:label>\n" +
            "        <xf:value>18</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"19\">\n" +
            "        <xf:label>Unknown Location 18 [19]</xf:label>\n" +
            "        <xf:value>19</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"5\">\n" +
            "        <xf:label>Unknown Location 4 [5]</xf:label>\n" +
            "        <xf:value>5</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"6\">\n" +
            "        <xf:label>Unknown Location 5 [6]</xf:label>\n" +
            "        <xf:value>6</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"7\">\n" +
            "        <xf:label>Unknown Location 6 [7]</xf:label>\n" +
            "        <xf:value>7</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"8\">\n" +
            "        <xf:label>Unknown Location 7 [8]</xf:label>\n" +
            "        <xf:value>8</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"9\">\n" +
            "        <xf:label>Unknown Location 8 [9]</xf:label>\n" +
            "        <xf:value>9</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"10\">\n" +
            "        <xf:label>Unknown Location 9  [10]</xf:label>\n" +
            "        <xf:value>10</xf:value>\n" +
            "      </xf:item>\n" +
            "      <xf:item id=\"2\">\n" +
            "        <xf:label>Wishard Hospital [2]</xf:label>\n" +
            "        <xf:value>2</xf:value>\n" +
            "      </xf:item>\n" +
            "    </xf:select1>\n" +
            "    <xf:select1 bind=\"encounter.provider_id\">\n" +
            "      <xf:label>PROVIDER ID</xf:label>\n" +
            "      <xf:item id=\"1\">\n" +
            "        <xf:label>Super User [admin]</xf:label>\n" +
            "        <xf:value>1</xf:value>\n" +
            "      </xf:item>\n" +
            "    </xf:select1>\n" +
            "    <xf:group id=\"problem_list/problem_added\">\n" +
            "      <xf:label>PROBLEM ADDED</xf:label>\n" +
            "      <xf:hint>Diagnosis or problem noted on a patient encounter.</xf:hint>\n" +
            "      <xf:repeat bind=\"problem_added\">\n" +
            "        <xf:input bind=\"problem_list_problem_added_value\">\n" +
            "          <xf:label>problem_added value</xf:label>\n" +
            "          <xf:hint>Diagnosis or problem noted on a patient encounter.</xf:hint>\n" +
            "        </xf:input>\n" +
            "      </xf:repeat>\n" +
            "    </xf:group>\n" +
            "    <xf:group id=\"problem_list/problem_resolved\">\n" +
            "      <xf:label>PROBLEM RESOLVED</xf:label>\n" +
            "      <xf:hint>Diagnosis or problem noted on a patient encounter as being resolved.</xf:hint>\n" +
            "      <xf:repeat bind=\"problem_resolved\">\n" +
            "        <xf:input bind=\"problem_list_problem_resolved_value\">\n" +
            "          <xf:label>problem_resolved value</xf:label>\n" +
            "          <xf:hint>Diagnosis or problem noted on a patient encounter as being resolved.</xf:hint>\n" +
            "        </xf:input>\n" +
            "      </xf:repeat>\n" +
            "    </xf:group>\n" +
            "    <xf:input bind=\"weight_kg\">\n" +
            "      <xf:label>WEIGHT (KG)</xf:label>\n" +
            "      <xf:hint>Patient's weight in kilograms.</xf:hint>\n" +
            "    </xf:input>\n" +
            "  </xf:group>\n" +
            "</xf:xforms>";

}
