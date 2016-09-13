package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.xForm2MuzimaTransform.*;

import javax.xml.transform.TransformerFactory;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.muzima.xForm2MuzimaTransform.XslTransformPipeline.xslTransformPipeline;

public class XFormToJavaRosaModelXSLTest extends ResourceTest {

    private EnketoXslTransformer transformer;

    @Before
    public void setUp() throws Exception {
        XslTransformPipeline pipeline = xslTransformPipeline().push(getFile("/xform2jr.xsl")).push(getFile("/jr2xmldata.xsl"));
        transformer = new EnketoXslTransformer(TransformerFactory.newInstance(), pipeline);
    }

    @Test
    public void shouldConvertModelTagWithMultipleAttributeToTemplateAttribute() throws Exception {
        EnketoResult result = transformer.transform(getText("xform/test-xform-model-multiple.xml"));
        Diff diff = new Diff(result.getModel(), getText("xform/test-xform-model-multiple-result-expected.xml"));
        assertThat(diff.similar(), is(true));
    }

}
