package org.openmrs.module.muzima.resource;

import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.model.MuzimaForm;
import org.openmrs.module.muzima.api.service.MuzimaFormService;
import org.openmrs.module.muzima.web.resource.muzima.MuzimaFormResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MuzimaFormResourceTest {
    private MuzimaFormService service;
    MuzimaFormResource controller;

    @Before
    public void setUp() throws Exception {

        MuzimaForm form = getForm("foo");
        service = mock(MuzimaFormService.class);
        when(service.findByUniqueId("foo")).thenReturn(form);
        controller = new MuzimaFormResource();
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(MuzimaFormService.class)).thenReturn(service);

    }

    private MuzimaForm getForm(String uuid) {
        MuzimaForm muzimaForm = new MuzimaForm();
        muzimaForm.setId(uuid.hashCode());
        muzimaForm.setUuid(uuid);
        return muzimaForm;
    }

    @Test
    public void retrieve_shouldGetMuzimaFormByUUID() {
        Representation representation = mock(CustomRepresentation.class);
        when(representation.getRepresentation()).thenReturn("(uuid:uuid,id:id)");
        RequestContext context = mock(RequestContext.class);
        when(context.getRepresentation()).thenReturn(representation);

        Object foo = controller.retrieve("foo", context);
        verify(service, times(1)).findByUniqueId("foo");
    }

    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void delete_notSupported() {
        controller.delete(getForm(""), "", null);
    }

    @Test
    public void save_shouldDelegateToService() throws SAXException, DocumentException, TransformerException, IOException, XPathExpressionException, ParserConfigurationException {
        MuzimaForm form = getForm("");
        controller.save(form);
        try {
            verify(service, times(1)).save(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAll_shouldGetAllForms() {
        Representation representation = mock(CustomRepresentation.class);
        RequestContext context = mock(RequestContext.class);

        RestService restService = mock(RestService.class);
        when(restService.getResourceBySupportedClass(MuzimaFormResource.class)).thenReturn(null);
        PowerMockito.when(Context.getService(RestService.class)).thenReturn(restService);


        when(representation.getRepresentation()).thenReturn("(uuid:uuid,id:id)");
        when(context.getRepresentation()).thenReturn(representation);
        when(context.getStartIndex()).thenReturn(0);
        when(context.getLimit()).thenReturn(10);
        when(service.getAll()).thenReturn(getMuzimaForms());

        SimpleObject response = controller.getAll(context);
        assertThat(response.containsKey("results"), is(true));
        List forms = (List) response.get("results");
        assertThat(forms.size(), is(3));
        verify(service, times(1)).getAll();
    }

    private ArrayList<MuzimaForm> getMuzimaForms() {
        ArrayList<MuzimaForm> muzimaForms = new ArrayList<MuzimaForm>();
        muzimaForms.add(getForm("foo"));
        muzimaForms.add(getForm("bar"));
        muzimaForms.add(getForm("baz"));
        return muzimaForms;
    }

    @Test
    public void getRepresentationDescription_shouldAddDefaultProperties() {
        Representation representation = mock(RefRepresentation.class);
        Set<String> keys = controller.getRepresentationDescription(representation).getProperties().keySet();
        assertThat(keys.contains("id"), is(true));
        assertThat(keys.contains("uuid"), is(true));
        assertThat(keys.contains("name"), is(true));
        assertThat(keys.contains("description"), is(true));
        assertThat(keys.contains("tags"), is(true));

        representation = mock(DefaultRepresentation.class);
        keys = controller.getRepresentationDescription(representation).getProperties().keySet();
        assertThat(keys.contains("id"), is(true));
        assertThat(keys.contains("uuid"), is(true));
        assertThat(keys.contains("name"), is(true));
        assertThat(keys.contains("description"), is(true));
        assertThat(keys.contains("tags"), is(true));
    }

}
