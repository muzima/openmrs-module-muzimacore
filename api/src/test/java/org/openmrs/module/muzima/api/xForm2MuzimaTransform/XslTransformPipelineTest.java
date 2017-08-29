package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.junit.Test;
import org.openmrs.module.muzima.xForm2MuzimaTransform.XslTransformPipeline;

import java.io.File;
import java.util.Stack;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class XslTransformPipelineTest {
    @Test
    public void push_shouldAddToTheList() {
        File mock = mock(File.class);
        XslTransformPipeline pipeline = XslTransformPipeline.xslTransformPipeline();
        Stack<File> transforms = pipeline.get();

        assertEquals(0, transforms.size());
        pipeline.push(mock);
        transforms = pipeline.get();
        assertEquals(1, transforms.size());
        assertEquals(transforms.pop(), mock);
        assertEquals(0, transforms.size());

    }

    @Test
    public void shouldHaveAXForm2JavarosaXSLInThePipeline() throws Exception {
        XslTransformPipeline pipeline = XslTransformPipeline.ODK2Javarosa();
        Stack<File> files = pipeline.get();
        File file = files.pop();
        assertThat(file.getName(), is("ODK2jr.xsl"));
    }
}
