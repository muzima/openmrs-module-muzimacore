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
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.xforms.XformsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.module.muzima.MuzimaFormBuilder.muzimaform;
import static org.openmrs.module.muzima.MuzimaFormTagBuilder.tag;

public class MuzimaFormServiceImplTest extends BaseModuleContextSensitiveTest {

    private MuzimaFormService service;
    private XformsService xformsService;
    private MuzimaFormDAO dao;
    private Date syncDate;

    @Before
    public void setUp() throws Exception {
        dao = mock(MuzimaFormDAO.class);
        xformsService = mock(XformsService.class);
        executeDataSet("xformTestData.xml");
        syncDate = new Date();
        service = new MuzimaFormServiceImpl(dao);
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

    @Ignore
    @Test(expected = DocumentException.class)
    public void shouldNotCreateHTMLFormIfFormNameAlreadyExists() throws Exception {
        List<MuzimaForm> muzimaForms = asList(getMuzimaFormWithName("Something like name"),
                getMuzimaFormWithName("name"));
        when(dao.getFormByName("name", syncDate)).thenReturn(muzimaForms);
        service.createHTMLForm("html", "c0c579b0-8e59-401d-8a4a-976a0b183519",  "discriminator");
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

    private MuzimaForm getMuzimaFormWithName(String name) {
        MuzimaForm form1 = new MuzimaForm();
        //form1.setName(name);
        return form1;
    }
}