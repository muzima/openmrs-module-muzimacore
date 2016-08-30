package org.openmrs.module.muzima.xForm2MuzimaTransform;


import org.dom4j.DocumentException;
import org.openmrs.module.muzima.api.service.impl.EnketoResult;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;

public class EnketoXslTransformer extends XForm2Html5Transformer {

    private XslTransformPipeline transformPipeline;
    private SAXTransformerFactory transformerFactory;


    public EnketoXslTransformer(TransformerFactory transformerFactory, XslTransformPipeline transformPipeline) {
        this.transformPipeline = transformPipeline;
        this.transformerFactory = (SAXTransformerFactory) transformerFactory;
        transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);
    }

    public EnketoResult transform(String xformXml) throws IOException, TransformerException, ParserConfigurationException, DocumentException {
        Stack<File> transforms = transformPipeline.get();
        if (transforms.isEmpty()) return new EnketoResult("");

        StringWriter writer = new StringWriter();
        Result streamResult = new StreamResult(writer);

        Result intermediateResult = streamResult;
        while (!transforms.isEmpty()) {
            Templates templates = transformerFactory.newTemplates(new StreamSource(transforms.pop()));
            TransformerHandler transformerHandler = transformerFactory.newTransformerHandler(templates);
            transformerHandler.setResult(intermediateResult);
            intermediateResult = new SAXResult(transformerHandler);
        }

        File inputFile = createTempFile(xformXml);
        Transformer transformer = transformerFactory.newTransformer();
        try {
            transformer.transform(new StreamSource(inputFile), intermediateResult);
        } finally {
            inputFile.delete();
        }
        return new EnketoResult(writer.getBuffer().toString());
    }
}
