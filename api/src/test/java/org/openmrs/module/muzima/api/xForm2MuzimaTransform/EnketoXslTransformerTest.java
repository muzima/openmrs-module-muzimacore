package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.xForm2MuzimaTransform.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openmrs.module.muzima.xForm2MuzimaTransform.XslTransformPipeline.xslTransformPipeline;

public class EnketoXslTransformerTest extends ResourceTest {
    SAXTransformerFactory transformerFactory;
    TransformerHandler transformerHandler;
    Transformer transformer;

    @Before
    public void setUp() throws Exception {
        transformerHandler = mock(TransformerHandler.class);
        transformerFactory = mock(SAXTransformerFactory.class);
        transformer = mock(Transformer.class);
    }

    @Test
    public void transform_shouldTransformXform2JRFirst() throws Exception {
        ArgumentCaptor<Result> result = ArgumentCaptor.forClass(Result.class);

        when(transformerFactory.newTransformerHandler(Matchers.<Templates>anyObject())).thenReturn(transformerHandler);
        when(transformerFactory.newTransformer()).thenReturn(transformer);

        when(transformerFactory.newTemplates(Matchers.<Source>anyObject())).thenReturn(new TemplatesImpl());
        doNothing().when(transformerHandler).setResult(Matchers.<Result>anyObject());

        XslTransformPipeline transformers = xslTransformPipeline().push(getXform2JRTransformer());
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(transformerFactory, transformers);

        EnketoResult transformed = enketoXslTransformer.transform(getSampleXForm());
        verify(transformerFactory, times(1)).newTransformer();
        verify(transformerHandler, times(1)).setResult(result.capture());
    }


    @Test
    public void transform_shouldPerformSuccessiveTransforms() throws Exception {

        XslTransformPipeline transformers = xslTransformPipeline().push(getXform2JRTransformer())
                .push(getHtml5Transformer());

        ArgumentCaptor<Result> result = ArgumentCaptor.forClass(Result.class);

        when(transformerFactory.newTransformerHandler(Matchers.<Templates>anyObject())).thenReturn(transformerHandler);
        when(transformerFactory.newTransformer()).thenReturn(transformer);

        when(transformerFactory.newTemplates(Matchers.<Source>anyObject())).thenReturn(new TemplatesImpl());
        doNothing().when(transformerHandler).setResult(Matchers.<Result>anyObject());

        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(transformerFactory, transformers);
        EnketoResult transformed = enketoXslTransformer.transform(getSampleXForm());
        verify(transformerFactory, times(1)).newTransformer();
        verify(transformerHandler, times(2)).setResult(result.capture());
    }


    @Test
    public void transform_shouldTransformXFormToModel() throws Exception {

        XslTransformPipeline transformers = xslTransformPipeline().push(getXform2JRTransformer())
                .push(getHtml5Transformer());

        ArgumentCaptor<Result> result = ArgumentCaptor.forClass(Result.class);

        when(transformerFactory.newTransformerHandler(Matchers.<Templates>anyObject())).thenReturn(transformerHandler);
        when(transformerFactory.newTransformer()).thenReturn(transformer);

        when(transformerFactory.newTemplates(Matchers.<Source>anyObject())).thenReturn(new TemplatesImpl());
        doNothing().when(transformerHandler).setResult(Matchers.<Result>anyObject());

        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(transformerFactory, transformers);
        EnketoResult transformed = enketoXslTransformer.transform(getSampleXForm());
        verify(transformerFactory, times(1)).newTransformer();
        verify(transformerHandler, times(2)).setResult(result.capture());
    }

    @Test
    public void transform_shouldReturnEmptyResponseWhenNoTransformsAreAdded() throws Exception {
        ArgumentCaptor<StreamResult> result = ArgumentCaptor.forClass(StreamResult.class);

        when(transformerFactory.newTransformerHandler(Matchers.<Source>anyObject())).thenReturn(transformerHandler);
        doNothing().when(transformerHandler).setResult(Matchers.<Result>anyObject());

        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(transformerFactory, xslTransformPipeline());
        EnketoResult transformed = enketoXslTransformer.transform(getSampleXForm());
        assertThat(transformed.hasResult(), is(false));
        verify(transformerFactory, times(0)).newTransformer(Matchers.<Source>anyObject());
        verify(transformerHandler, times(0)).setResult(result.capture());
    }

    @Test
    public void transform_integrationTest() throws IOException, TransformerException, ParserConfigurationException, DocumentException {

        XslTransformPipeline transformers = xslTransformPipeline()
                .push(getXform2JRTransformer())
                .push(getHtml5Transformer());
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(TransformerFactory.newInstance(), transformers);
        EnketoResult transform = enketoXslTransformer.transform(getSampleXForm());
        assertThat(transform.hasResult(), is(true));

//        System.out.println(transform.getResult());

    }

    @Test
    public void transformShouldTransformODKRegstrationForm() throws Exception {
        XslTransformPipeline transformers = XslTransformPipeline.ODK2Javarosa();
        transformers.push(getHtml5Transformer());
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(TransformerFactory.newInstance(), transformers);
        EnketoResult result = enketoXslTransformer.transform(getSampleODKregistrationForm());
        System.out.println(result.getResult());

    }

    @Test
    public void transformShouldTransformODKForms() throws Exception {
        XslTransformPipeline transformers = XslTransformPipeline.ODK2Javarosa();
        transformers.push(getHtml5Transformer());
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(TransformerFactory.newInstance(), transformers);
        EnketoResult result = enketoXslTransformer.transform(getSampleODKForm());
        System.out.println(result.getResult());

    }

    @Ignore
    @Test
    public void transformShouldTransformDysplasiaForms() throws Exception {
        XslTransformPipeline transformers = XslTransformPipeline.ODK2Javarosa();
        transformers.push(getHtml5Transformer());
        EnketoXslTransformer enketoXslTransformer = new EnketoXslTransformer(TransformerFactory.newInstance(), transformers);
        EnketoResult result = enketoXslTransformer.transform(getSampleODKDisplasia());
        System.out.println(result.getResult());

    }

    private String getSampleXForm() throws IOException {
        return getText("/xform/test-xform.xml");
    }

    private String getSampleODKregistrationForm() throws IOException {
        return getText("/odk/registration-form.xml");
    }

    private String getSampleODKDisplasia() throws IOException {
        return getText("/odk/dysplasia-form.xml");
    }

    private String getSampleODKForm() throws IOException {
        return getText("/odk/hispatology-form.xml");
    }

    private File getXform2JRTransformer() throws IOException {
        return getFile("/xform2jr.xsl");
    }

    private File getHtml5Transformer() throws IOException {
        return getFile("/jr2html5_php5.xsl");
    }

    private File getODK2JRTransformer() throws IOException {
        return getFile("/ODK2jr.xsl");
    }


}
