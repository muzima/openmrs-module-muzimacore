package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.apache.commons.io.IOUtils;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.dom4j.DocumentException;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.xForm2MuzimaTransform.EnketoHttpClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnketoHttpClientTest {
    @Test
    public void transform_shouldCallEnketoServer() throws IOException, ParserConfigurationException, XPathExpressionException, DocumentException, SAXException {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(getBasicHttpResponse());

        EnketoHttpClient enketoHttpClient = new EnketoHttpClient("http://10.4.33.189/transform/get_html_form", httpClient);
        EnketoResult result = enketoHttpClient.transform(getSampleXForm());
        assertThat(result.getResult(), is(IOUtils.toString(getConvertedXForm())));
    }

    @Test
    @Ignore
    public void transform_integrationTest() throws IOException, ParserConfigurationException, DocumentException {
        EnketoHttpClient enketoHttpClient = new EnketoHttpClient("http://10.4.33.189/transform/get_html_form",
                new DefaultHttpClient());
        EnketoResult result = enketoHttpClient.transform(getTestXForm());
    }

    private BasicHttpResponse getBasicHttpResponse() throws IOException {
        BasicHttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(getConvertedXForm());
        response.setEntity(entity);
        return response;
    }


    private String getSampleXForm() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext();
        return IOUtils.toString(context.getResource("/enketo/enketoSampleRequest.xml").getInputStream());
    }


    private String getTestXForm() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext();
        return IOUtils.toString(context.getResource("/xform/test-xform4webclient.xml").getInputStream());
    }

    private InputStream getConvertedXForm() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext();
        return context.getResource("/enketo/enketoSampleResult.xml").getInputStream();
    }
}
