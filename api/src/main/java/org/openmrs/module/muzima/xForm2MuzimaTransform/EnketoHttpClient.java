package org.openmrs.module.muzima.xForm2MuzimaTransform;


import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.dom4j.DocumentException;
import org.openmrs.module.muzima.model.EnketoResult;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EnketoHttpClient extends XForm2Html5Transformer {
    String enketoServiceUri;
    HttpClient httpClient;

    public EnketoHttpClient(String enketoServiceUri, HttpClient defaultHttpClient) {
        this.enketoServiceUri = enketoServiceUri;
        this.httpClient = defaultHttpClient;
    }

    public EnketoResult transform(String xformXml) throws IOException, ParserConfigurationException, DocumentException {
        File file = createTempFile(xformXml);
        HttpPost post = createPost(file);
        InputStream response = httpClient.execute(post).getEntity().getContent();
        file.delete();
        return new EnketoResult(IOUtils.toString(response));
    }

    private HttpPost createPost(File file) {
        HttpPost post = new HttpPost(enketoServiceUri);
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("xml_file", new FileBody(file));
        post.setEntity(entity);
        return post;
    }

}
