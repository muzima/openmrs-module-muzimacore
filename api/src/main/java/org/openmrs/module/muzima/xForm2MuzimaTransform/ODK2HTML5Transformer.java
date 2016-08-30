package org.openmrs.module.muzima.xForm2MuzimaTransform;


import javax.xml.transform.TransformerFactory;

public class ODK2HTML5Transformer extends EnketoXslTransformer {

    public ODK2HTML5Transformer(TransformerFactory transformerFactory, XslTransformPipeline transformPipeline) {
        super(transformerFactory, transformPipeline);
    }
}
