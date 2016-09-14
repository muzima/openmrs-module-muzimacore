package org.openmrs.module.muzima.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class JavaRosaFormUploadControllerTest {

    private JavaRosaFormUploadController controller;
    private MockMultipartHttpServletRequest request;
    private MuzimaFormService service;

    @Before
    public void setUp() throws Exception {
        request = new MockMultipartHttpServletRequest();
        controller = new JavaRosaFormUploadController();

        service = mock(MuzimaFormService.class);
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(MuzimaFormService.class)).thenReturn(service);

    }

    @Test
    public void shouldConvertJavaRosaFormToHTMLAndSaveIt() throws Exception {
        request.addFile(multipartFile("file", "sampleUploadForm.xml"));

        controller.uploadJavaRosa(request,  "form", "discriminator");

        verify(service).create(readStream(request.getFile("file").getInputStream()),  "form",  "discriminator");
    }

    @Test
    public void shouldConvertODKFormToHTMLAndSaveIt() throws Exception {
        request.addFile(multipartFile("file", "sampleUploadForm.xml"));

        controller.uploadODK(request,  "form",  "discriminator");

        verify(service).importODK(readStream(request.getFile("file").getInputStream()),"form",  "discriminator");
    }

    @Test
    public void validate_shouldValidateAJavaRosaXMLUsingTheService() throws Exception {
        request.addFile(multipartFile("file", "sampleUploadForm.xml"));

        controller.validateJavaRosa(request);

        verify(service).validateJavaRosa(readStream(request.getFile("file").getInputStream()));

    }

    @Test
    public void shouldCreateHTMLFormWithGivenNameAndDescription() throws Exception {
        String form = "form";
        String formName = "name";
        String formDescription = "description";
        String formDiscriminator = "discriminator";
        String version = "1.0";
        request.addFile(multipartFile("file", "sampleUploadForm.xml"));
        controller.uploadHTMLForm(request, form, formDiscriminator);
        verify(service).createHTMLForm(anyString(), eq(form),  eq(formDiscriminator));
    }

    @Test
    public void shouldUpdateHTMLFormWithGivenNameAndDescription() throws Exception {
        String formName = "UpdateForm";
        String formId = "123";
        request.addFile(multipartFile("file", "sampleUploadForm.xml"));
        controller.updateHTMLForm(request,formId);
        verify(service).updateHTMLForm(anyString(), eq(formId));

    }

    private MockMultipartFile multipartFile(String name, String fileName) throws IOException {
        return new MockMultipartFile(name, getClass().getClassLoader().getResourceAsStream(fileName));
    }

    private String readStream(InputStream stream) throws IOException {
        return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }

}
