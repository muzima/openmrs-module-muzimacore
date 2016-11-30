package org.openmrs.module.muzima.api;

import org.dom4j.DocumentException;
import org.javarosa.xform.parse.ValidationMessages;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.api.service.impl.MuzimaFormServiceImpl;
import org.openmrs.module.muzima.model.CompositeEnketoResult;
import org.openmrs.module.muzima.model.EnketoResult;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ModelXml2JsonTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2HTML5Transformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.ODK2JavarosaTransformer;
import org.openmrs.module.muzima.xForm2MuzimaTransform.XForm2Html5Transformer;
import org.openmrs.module.xforms.XformsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.muzima.MuzimaFormBuilder.muzimaform;
import static org.openmrs.module.muzima.MuzimaFormTagBuilder.tag;
import static org.openmrs.module.muzima.XFormBuilder.xForm;

public class MuzimaFormServiceImplTest extends BaseModuleContextSensitiveTest {

    private MuzimaFormService service;
    private XformsService xformsService;
    private MuzimaFormDAO dao;
    private XForm2Html5Transformer transformer;
    private ModelXml2JsonTransformer modelTransformer;
    private ODK2JavarosaTransformer odk2JavarosaTransformer;
    private Date syncDate;
    private ODK2HTML5Transformer odk2HTML5Transformer;

    @Before
    public void setUp() throws Exception {
        dao = mock(MuzimaFormDAO.class);
        xformsService = mock(XformsService.class);
        executeDataSet("xformTestData.xml");
        transformer = mock(XForm2Html5Transformer.class);
        modelTransformer = mock(ModelXml2JsonTransformer.class);
        odk2JavarosaTransformer = mock(ODK2JavarosaTransformer.class);
        odk2HTML5Transformer = mock(ODK2HTML5Transformer.class);
        syncDate = new Date();
        service = new MuzimaFormServiceImpl(dao, transformer, modelTransformer, odk2JavarosaTransformer, odk2HTML5Transformer);
    }

    void setUpDao() {
        List<MuzimaForm> muzimaForms = new ArrayList<MuzimaForm>();
        muzimaForms.add(
                muzimaform().withId(1)//.withName("Registration Form").withDescription("Form for registration")
                        .with(tag().withId(1).withName("Registration"))
                        .with(tag().withId(2).withName("Patient"))
                        .instance()
        );
        muzimaForms.add(muzimaform().withId(2)//.withName("PMTCT Form").withDescription("Form for PMTCT")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(3).withName("Encounter"))
                .with(tag().withId(4).withName("HIV"))
                .instance());

        muzimaForms.add(muzimaform().withId(3)//.withName("Ante-Natal Form").withDescription("Form for ante-natal care")
                .instance());

        when(dao.getAll()).thenReturn(muzimaForms);
    }

    @Test
    public void getAll_shouldGetAllForms() throws Exception {
        setUpDao();
        List<MuzimaForm> list = service.getAll();
        assertThat(list.size(), is(3));
        verify(dao, times(1)).getAll();
    }

    @Test
    public void getXform_shouldLoadXForm() throws Exception {
        service.getXForms();
        verify(dao, times(1)).getXForms();
    }

    @Test
    public void shouldNotInteractWithAnyTransformersWhileUploadingHTML() throws Exception {
        service.createHTMLForm("html", "c0c579b0-8e59-401d-8a4a-976a0b183519", "discriminator");
        verifyZeroInteractions(transformer, modelTransformer, odk2JavarosaTransformer, odk2HTML5Transformer);
        verify(dao).saveForm(any(MuzimaForm.class));
    }

    @Ignore
    @Test(expected = DocumentException.class)
    public void shouldNotCreateHTMLFormIfFormNameAlreadyExists() throws Exception {
        List<MuzimaForm> muzimaForms = asList(getMuzimaFormWithName("Something like name"),
                getMuzimaFormWithName("name"));
        when(dao.getFormByName("name", syncDate)).thenReturn(muzimaForms);
        service.createHTMLForm("html", "c0c579b0-8e59-401d-8a4a-976a0b183519",  "discriminator");
        verifyZeroInteractions(transformer, modelTransformer, odk2JavarosaTransformer, odk2HTML5Transformer);
        verify(dao, never()).saveForm(any(MuzimaForm.class));
    }

    @Test
    public void shouldUpdateTheHTMLFormOfAnExistingForm() throws Exception {
        List<MuzimaForm> muzimaForms = new ArrayList<MuzimaForm>();
        MuzimaForm registrationForm = muzimaform().withId(1)//.withName("Registration Form").withDescription("Form for registration")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183522"))
                .instance();
        MuzimaForm pmtctForm = muzimaform().withId(2)//.withName("PMTCT Form").withDescription("Form for PMTCT")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(3).withName("Encounter"))
                .with(tag().withId(4).withName("HIV"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183522"))
                .instance();
        muzimaForms.add(registrationForm);
        muzimaForms.add(pmtctForm);
        when(dao.getFormByUuid("1")).thenReturn(registrationForm);
        when(dao.getFormByUuid("2")).thenReturn(pmtctForm);
        when(dao.getFormByName("PMTCT Form", null)).thenReturn(asList(pmtctForm));
        service.updateHTMLForm("UPDATED",  "2");
        assertEquals("UPDATED",pmtctForm.getHtml());
    }

    @Test
    public void importExisting_shouldRetrieveExistingXFormAndConvertItIntoHTML5AndPersistAMuzimaForm() throws Exception {
        String xFormXml = "<xml><some/><valid/></xml>";
        String htmlForm = "<foo><form><ul><li/><li/></ul></form><model/></foo>";
        String modelJson = "{form : [{name:'', bind: ''}]}";

        when(xformsService.getXform(1)).thenReturn(xForm().withId(1).withXFormXml(xFormXml).instance());
        when(transformer.transform(xFormXml)).thenReturn(new EnketoResult(htmlForm));
        when(modelTransformer.transform(htmlForm)).thenReturn(new CompositeEnketoResult(htmlForm, modelJson));

        MuzimaForm testForm = service.create(xformsService.getXform(1).getXformXml(),  "c0c579b0-8e59-401d-8a4a-976a0b183519",  "discriminator");

        verify(dao, times(1)).saveForm(muzimaform()
                .withUuid(testForm.getUuid())
                .withDiscriminator("discriminator")
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance());
    }

    @Test
    public void importExisting_shouldSetConvertedXform() throws Exception {

        String htmlForm = "<foo><form><ul><li/><li/></ul></form><model/></foo>";
        String xFormXml = "<foo><some/><valid/></foo>";
        String modelJson = "{form : [{name:'', bind: ''}]}";

        when(transformer.transform(xFormXml)).thenReturn(new EnketoResult(htmlForm));
        when(modelTransformer.transform(htmlForm)).thenReturn(new CompositeEnketoResult(htmlForm, modelJson));
        when(xformsService.getXform(1)).thenReturn(xForm().withId(1).withXFormXml(xFormXml).instance());

        service.create(xformsService.getXform(1).getXformXml(), "c0c579b0-8e59-401d-8a4a-976a0b183522",  "discriminator");

        verify(dao, times(1)).saveForm(muzimaform()
                .withForm("form")
                .withDiscriminator("discriminator")
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183522"))
                .instance());
        verify(transformer, times(1)).transform(xFormXml);
        verify(modelTransformer, times(1)).transform(htmlForm);
    }

    @Test
    public void importODKShouldTransformUsingTheODK2HTML5Pipeline() throws Exception {
        when(odk2HTML5Transformer.transform("odk")).thenReturn(new EnketoResult("xml"));
        CompositeEnketoResult result = mock(CompositeEnketoResult.class);
        when(modelTransformer.transform("xml")).thenReturn(result);

        service.importODK("odk", "c0c579b0-8e59-401d-8a4a-976a0b183522",  "discriminator");

        verify(modelTransformer).transform("xml");
        verify(odk2HTML5Transformer).transform("odk");
        verify(dao).saveForm(any(MuzimaForm.class));
    }

    @Test
    public void save_shouldSaveExistingForm() throws Exception {
        MuzimaForm form = muzimaform().withId(1).instance();
        service.save(form);
        verify(dao).saveForm(form);
    }

    @Test
    public void findById_shouldFindFormById() {
        service.getFormById(1);
        verify(dao, times(1)).getFormById(1);
    }

    @Test
    public void findByUUID_shouldFindFormByUUID() {
        service.getFormByUuid("foo");
        verify(dao, times(1)).getFormByUuid("foo");
    }

    @Test
    public void validateJavaRosa() throws Exception {
        ValidationMessages messages = service.validateJavaRosa("xml");
        assertThat(messages.getList().get(0).getMessage(), is("Document has no root element!"));
    }

    @Test
    public void validateODK() throws Exception {
        when(odk2JavarosaTransformer.transform("odk")).thenReturn(new EnketoResult("xml"));

        ValidationMessages messages = service.validateODK("odk");

        verify(odk2JavarosaTransformer).transform("odk");
        assertThat(messages.getList().get(0).getMessage(), is("Document has no root element!"));
    }

    @Ignore
    @Test(expected = DocumentException.class)
    public void shouldNotCreateFormIfTheNameAlreadyExists() throws ParserConfigurationException, TransformerException, DocumentException, IOException {
        List<MuzimaForm> muzimaForms = asList(getMuzimaFormWithName("Something like name"),
                getMuzimaFormWithName("name"));
        when(dao.getFormByName("name", syncDate)).thenReturn(muzimaForms);
        try {
            service.create("xml",  "form",  "discriminator");
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(modelTransformer, never()).transform(anyString());
    }

    @Test
    public void shouldUpdateForm() throws ParserConfigurationException, TransformerException, DocumentException, IOException {
        MuzimaForm updateTestForm = muzimaform().withId(123).withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")//.withName("Update Test Form").withDescription("Form for Update Test")
         .instance();

        String xFormXml = "<xml><some/><valid/></xml>";
        String htmlForm = "<foo><form><ul><li/><li/></ul></form><model/></foo>";
        String modelJson = "{form : [{name:'', bind: ''}]}";

        when(xformsService.getXform(1)).thenReturn(xForm().withId(1).withXFormXml(xFormXml).instance());
        when(transformer.transform(xFormXml)).thenReturn(new EnketoResult(htmlForm));
        when(modelTransformer.transform(htmlForm)).thenReturn(new CompositeEnketoResult(htmlForm, modelJson));
        when(dao.getMuzimaFormByForm("c0c579b0-8e59-401d-8a4a-976a0b183522", true)).thenReturn(asList(updateTestForm));
        when(dao.getFormByUuid("123")).thenReturn(updateTestForm);

        try {
            service.update("<xml><some/><valid/></xml>",  "123");
        } catch (Exception e) {
            e.printStackTrace();
        }

        MuzimaForm form = service.getFormByUuid("123");
        assertEquals("<form><ul><li/><li/></ul></form>", form.getHtml());
        assertEquals("<model/>",form.getModelXml());
        assertEquals("{form : [{name:'', bind: ''}]}",form.getModelJson());
        verify(modelTransformer).transform(anyString());
    }

    @Test
    public void shouldCreateFormIfSimilarNameExistsButNotExactMatch() throws ParserConfigurationException, TransformerException, DocumentException, IOException {
        List<MuzimaForm> muzimaForms = asList(getMuzimaFormWithName("Something like name"),
                getMuzimaFormWithName("very much similar to name"));
        EnketoResult enketoResult = mock(EnketoResult.class);
        CompositeEnketoResult compositeEnketResult = mock(CompositeEnketoResult.class);

        when(dao.getFormByName("name", syncDate)).thenReturn(muzimaForms);
        when(transformer.transform(anyString())).thenReturn(enketoResult);
        when(modelTransformer.transform(anyString())).thenReturn(compositeEnketResult);

        try {
            service.create("xml","form", "discriminator");
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(modelTransformer).transform(anyString());
    }

    private MuzimaForm getMuzimaFormWithName(String name) {
        MuzimaForm form1 = new MuzimaForm();
        //form1.setName(name);
        return form1;
    }
}