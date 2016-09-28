package org.openmrs.module.muzima.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MuzimaFormControllerTest {
    private MuzimaFormController html5FormController;
    private MuzimaFormService service;
    private MuzimaForm form;

    @Before
    public void setUp() throws Exception {
        service = mock(MuzimaFormService.class);
        html5FormController = new MuzimaFormController();
        mockStatic(Context.class);
    }

    @Test
    public void save_shouldSaveAForm() throws Exception {
        when(Context.getService(MuzimaFormService.class)).thenReturn(service);
        form = new MuzimaForm() {{
            setId(1);
        }};

        html5FormController.save(form);

        verify(service).save(form);
    }

    @Test
    public void retire_shouldRetireAFormAndSetRetiredByAndDate() throws Exception {
        User admin = new User(23);
        form = new MuzimaForm();
        form.setId(1);
        when(Context.getService(MuzimaFormService.class)).thenReturn(service);
        when(Context.getAuthenticatedUser()).thenReturn(admin);
        when(service.getFormById(1)).thenReturn(form);

        html5FormController.retire(form.getId(), "The form is 60 years old!");

        assertTrue(form.getRetired());
        assertEquals(admin, form.getRetiredBy());
        assertEquals("The form is 60 years old!", form.getRetireReason());
        assertEquals(toSimpleDate(new Date()), toSimpleDate(form.getDateRetired()));
        verify(service).save(form);
    }

    private String toSimpleDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
