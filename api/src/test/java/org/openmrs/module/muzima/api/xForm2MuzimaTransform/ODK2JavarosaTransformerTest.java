package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.muzima.xForm2MuzimaTransform.*;

import javax.xml.transform.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ODK2JavarosaTransformerTest extends ResourceTest {

    private EnketoXslTransformer transformer;

    @Before
    public void setUp() throws Exception {
        transformer = new EnketoXslTransformer(TransformerFactory.newInstance(), XslTransformPipeline.ODK2Javarosa());
    }

    @Test
    public void convertXfXFormsToHHTML() throws Exception {
        String result = transformer.transform(getText("/odk/with-xf-xforms-root-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/html-root-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertXFormsToHHTML() throws Exception {
        String result = transformer.transform(getText("/odk/with-xforms-root-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/html-root-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertHeadToHHead() throws Exception {
        String result = transformer.transform(getText("/odk/with-head-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/head-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertXfHeadToHHead() throws Exception {
        String result = transformer.transform(getText("/odk/with-xf-head-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/head-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertTitleToHTitle() throws Exception {
        String result = transformer.transform(getText("/odk/with-title-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/title-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertXfTitleToHTitle() throws Exception {
        String result = transformer.transform(getText("/odk/with-xf-title-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/title-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }


    @Test
    public void convertModelToHModel() throws Exception {
        String result = transformer.transform(getText("/odk/with-model-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/model-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertXfModelToHModel() throws Exception {
        String result = transformer.transform(getText("/odk/with-xf-model-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/model-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertInstanceTagAndRemoveInstanceId() throws Exception {
        String result = transformer.transform(getText("/odk/with-instance-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/with-instance-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertFormTagAndRemoveAllAttributes() throws Exception {
        String result = transformer.transform(getText("/odk/with-form-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/with-form-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertAndRemoveHintTag() throws Exception {
        String result = transformer.transform(getText("/odk/with-hint-tag.xml")).getResult();
        assertThat(result, not(containsString("<hint>")));
    }

    @Test
    public void convertBodyToHBody() throws Exception {
        String result = transformer.transform(getText("/odk/with-body-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/body-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertXfBodyToHBody() throws Exception {
        String result = transformer.transform(getText("/odk/with-xf-body-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/body-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    public void convertMultipleAttributeToTemplate() throws Exception {
        String result = transformer.transform(getText("/odk/with-multiple-attribute.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/with-multiple-attribute-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    @Ignore
    public void convertBindAttributeToRef() throws Exception {
        String result = transformer.transform(getText("/odk/with-bind-attribute.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/with-bind-attribute-result.xml"));
        assertThat(diff.similar(), is(true));
    }

    @Test
    @Ignore
    public void ignoreBindAttributeOnRepeatTag() throws Exception {
        String result = transformer.transform(getText("/odk/with-repeat-tag.xml")).getResult();
        Diff diff = new Diff(result, getText("/odk/with-repeat-tag-result.xml"));
        assertThat(diff.similar(), is(true));
    }
}
