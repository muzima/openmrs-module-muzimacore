package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.xForm2MuzimaTransform.*;

import javax.xml.transform.TransformerFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JavaRosaModelToJSONTest extends ResourceTest {

    private JsonNode form;


    @Before
    public void setUp() throws Exception {
        ModelXml2JsonTransformer jsonTransformer = new ModelXml2JsonTransformer(TransformerFactory.newInstance(), XslTransformPipeline.modelXml2JsonXSLPipeline());
        EnketoResult result = jsonTransformer.transform(getText("test-model-to-json-multiple.xml"));
        JsonNode node = new ObjectMapper().readTree(result.getModelAsJson());
        //System.out.println(node.toString());
        form = node.get("form");
    }

    @Test
    public void shouldOnlyHaveTwoElementInTheFieldSection() throws Exception {
        assertThat(form.get("fields").size(), is(3));
    }

    @Test
    public void shouldHaveTheTodayFieldInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(0);
        assertThat(jsonNode.get("name").toString(), is("\"today\""));
    }

    @Test
    public void shouldHaveTheTodayFieldBindingInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(0);
        assertThat(jsonNode.get("bind").toString(), is("\"/model/instance/EC_Registration_EngKan/today\""));
    }

    @Test
    public void shouldHaveTheTimeFieldInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(1);
        assertThat(jsonNode.get("name").toString(), is("\"time\""));
    }

    @Test
    public void shouldHaveTheTimeFieldBindingInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(1);
        assertThat(jsonNode.get("bind").toString(), is("\"/model/instance/EC_Registration_EngKan/today/time\""));
    }

    @Test
    public void shouldHaveTheDateFieldInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(2);
        assertThat(jsonNode.get("name").toString(), is("\"date\""));
    }

    @Test
    public void shouldHaveTheDateFieldBindingInTheJSON() throws Exception {
        JsonNode jsonNode = form.get("fields").get(2);
        assertThat(jsonNode.get("bind").toString(), is("\"/model/instance/EC_Registration_EngKan/date\""));
    }

    @Test
    public void shouldContainSectionSubFormsInTheJSON() throws Exception {
        assertThat(form.get("sub_forms"), notNullValue());
    }

    @Test
    public void shouldContainASubformWithNameAddress() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("name").toString(), is("\"address\""));
    }

    @Test
    public void shouldContainASubformWithBindTypeChild() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("bind_type").toString(), is("\"child\""));
    }

    @Test
    public void shouldHaveFieldsInSubForms() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("fields"), notNullValue());
    }

    @Test
    public void shouldOnlyOneFieldInTheSubForm() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("fields").size(), is(1));
    }

    @Test
    public void shouldHaveTheStreetFieldInTheSubForm() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("fields").get(0).get("name").toString(), is("\"street\""));
    }

    @Test
    public void shouldHaveTheStreetFieldBindingInTheSubForm() throws Exception {
        assertThat(form.get("sub_forms").get(0).get("fields").get(0).get("bind").toString(), is("\"/model/instance/EC_Registration_EngKan/address/street\""));
    }

}
