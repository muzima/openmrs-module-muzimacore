package org.openmrs.module.muzima.xForm2MuzimaTransform;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.openmrs.module.muzima.model.EnketoResult;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public abstract class XForm2Html5Transformer {
    public abstract EnketoResult transform(String xformXml) throws IOException, TransformerException, ParserConfigurationException, DocumentException;

    protected File createTempFile(String xformXml) throws IOException {
        UUID uuid = UUID.randomUUID();

        File file = new File(System.getProperty("catalina.base") + "/webapps/" + uuid + ".xml");
        FileUtils.writeStringToFile(file, xformXml, "UTF-8");
        return file;
    }
}
