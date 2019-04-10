package org.openmrs.module.muzima.api.xForm2MuzimaTransform;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ResourceTest {
    private ApplicationContext context = new ClassPathXmlApplicationContext();

    protected String getText(String path) throws IOException {
        return IOUtils.toString(getResource(path).getInputStream());
    }

    protected File getFile(String path) throws IOException {
        return getResource(path).getFile();
    }

    protected Resource getResource(String path) {
        return context.getResource(path);
    }
}
