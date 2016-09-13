package org.openmrs.module.muzima.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.model.MuzimaFormTag;
import org.openmrs.module.muzima.api.service.MuzimaTagService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class TagControllerTest {
    private TagController controller;
    private MuzimaTagService service;

    @Before
    public void setUp() throws Exception {
        service = mock(MuzimaTagService.class);
        controller = new TagController();
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(MuzimaTagService.class)).thenReturn(service);
    }

    @Test
    public void tags_shouldReturnTags() throws IOException {
        MuzimaFormTag tag1 = new MuzimaFormTag() {{
            setId(1);
        }};
        MuzimaFormTag tag2 = new MuzimaFormTag() {{
            setId(2);
        }};

        when(service.getAll()).thenReturn(asList(tag1, tag2));

        assertThat(controller.tags().size(), is(2));
        assertThat(controller.tags(), hasItems(tag1, tag2));
    }
}
