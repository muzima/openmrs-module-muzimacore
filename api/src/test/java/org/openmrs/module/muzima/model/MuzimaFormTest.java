package org.openmrs.module.muzima.model;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

public class MuzimaFormTest extends BaseModuleContextSensitiveTest {

    MuzimaForm muzimaForm;

    @Before
    public void setUpTest() throws Exception {
        muzimaForm = new MuzimaForm();
        Form testForm = new Form();
        testForm.setId(1);
        testForm.setVersion("1.0");
        testForm.setEncounterType( new EncounterType("Diabetes","Diabetes Encounter Description"));
        muzimaForm.setFormDefinition(testForm);
    }

    @Test
    public void setFormDefinitionTest() {
        System.out.println(muzimaForm.getFormDefinition());
        assertNotNull(muzimaForm);
        assertNotNull(muzimaForm.getFormDefinition());
    }

    @Test
    public void toStringTest() {
    }
}