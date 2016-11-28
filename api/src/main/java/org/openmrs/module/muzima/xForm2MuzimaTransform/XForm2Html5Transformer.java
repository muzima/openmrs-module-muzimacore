package org.openmrs.module.muzima.xForm2MuzimaTransform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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

        /* If "catalina.base" does not exist then use "java.io.tmpdir" */
        File file;
        String catalinaBaseDir = System.getProperty("catalina.base");
        if (!StringUtils.isEmpty(catalinaBaseDir) && !catalinaBaseDir.equalsIgnoreCase("null"))
            file = new File(System.getProperty("catalina.base") + "/webapps/" + uuid + ".xml");
        else
            file = new File(System.getProperty("java.io.tmpdir") + "/" + uuid + ".xml");

        FileUtils.writeStringToFile(file, xformXml, "UTF-8");
        return file;
    }
}
