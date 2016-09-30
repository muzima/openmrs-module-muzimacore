package org.openmrs.module.muzima.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.db.MuzimaFormDAO;
import org.openmrs.module.muzima.api.service.MuzimaTagService;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.model.MuzimaFormTag;
import org.openmrs.module.muzima.model.MuzimaXForm;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openmrs.module.muzima.MuzimaFormBuilder.muzimaform;
import static org.openmrs.module.muzima.MuzimaFormTagBuilder.tag;

public class MuzimaFormDAOTest extends BaseModuleContextSensitiveTest {

    private MuzimaFormDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = (MuzimaFormDAO) applicationContext.getBean("muzimaFormDAO");
        executeDataSet("tagTestData.xml");
        executeDataSet("xformTestData.xml");
    }

    @Test
    public void getAll_shouldGetAll() {
        List<MuzimaForm> list = dao.getAll();
        assertThat(list.size(), is(3));
        assertThat(list, hasItem(muzimaform()
                .withId(1)
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withDiscriminator("a")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance()));
        assertThat(list, hasItem(muzimaform()
                .withId(2)
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183520")
                .withDiscriminator("b")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(3).withName("Encounter"))
                .with(tag().withId(4).withName("HIV"))
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183520"))
                .instance()));
        assertThat(list, hasItem(muzimaform()
                .withId(3)
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183521")
                .withDiscriminator("c")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183521"))
                .instance()));
    }

    @Test
    public void getXForms_shouldGetXForms() {
        List<MuzimaXForm> all = dao.getXForms();
        assertThat(all.size(), is(3));
    }

    @Test
    public void findById_shouldFindById() {
        MuzimaForm form = dao.getFormById(1);
        assertThat(form, is(muzimaform()
                .withId(1)
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withDiscriminator("a")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance()));
    }

    @Test
    public void findByUUID_shouldFindByUUID() {
        MuzimaForm form = dao.getFormByUuid("foo");
        assertThat(form, is(muzimaform()
                .withId(1)
                .withUuid("foo")
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withDiscriminator("a")
                .with(tag().withId(1).withName("Registration"))
                .with(tag().withId(2).withName("Patient"))
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance()));
    }

    @Test
    public void saveForm_shouldSaveForm() {
        dao.saveForm(muzimaform()
                .withId(1)
                .with(tag().withId(1).withName("Registration"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183522"))
                .instance());
        List<MuzimaForm> list = dao.getAll();
        assertThat(list, hasItem(muzimaform().withId(1).with(tag().withId(1).withName("Registration"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183522")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183522")).instance()));
    }

    @Test
    public void saveForm_shouldAssignAnExistingTag() throws IOException {
        dao.saveForm(muzimaform().withId(1).with(tag().withId(1).withName("Registration"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance());
        List<MuzimaForm> list = dao.getAll();
        assertThat(list, hasItem(muzimaform().withId(1).with(tag().withId(1).withName("Registration"))
                .withForm("c0c579b0-8e59-401d-8a4a-976a0b183519")
                .withFormDefinition(Context.getFormService().getFormByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519"))
                .instance()));
    }

    @Test
    public void saveForm_shouldAssignANewTag() throws IOException {
        MuzimaForm form = muzimaform().withId(1).with(tag().withName("New Tag")).with(tag().withName("Another Tag")).instance();
        dao.saveForm(form);
        Set<MuzimaFormTag> formTags = form.getTags();
        assertThat(formTags.size(), is(2));
        MuzimaFormTag newTag = (MuzimaFormTag) formTags.toArray()[0];
        assertThat(newTag.getId(), notNullValue());
        MuzimaTagService tagService = Context.getService(MuzimaTagService.class);
        List<MuzimaFormTag> tags = tagService.getAll();
        assertThat(tags, hasItem(newTag));
    }
}