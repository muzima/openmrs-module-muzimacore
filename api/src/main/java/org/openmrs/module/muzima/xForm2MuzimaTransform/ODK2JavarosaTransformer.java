package org.openmrs.module.muzima.xForm2MuzimaTransform;


import javax.xml.transform.TransformerFactory;

public class ODK2JavarosaTransformer extends EnketoXslTransformer {

    public ODK2JavarosaTransformer(TransformerFactory transformerFactory, XslTransformPipeline transformPipeline) {
        super(transformerFactory, transformPipeline);
    }
}
