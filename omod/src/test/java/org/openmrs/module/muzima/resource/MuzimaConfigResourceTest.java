package org.openmrs.module.muzima.resource;

import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.api.service.MuzimaConfigService;
import org.openmrs.module.muzima.model.MuzimaConfig;
import org.openmrs.module.muzima.web.resource.muzima.MuzimaConfigResource;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class MuzimaConfigResourceTest {
    private MuzimaConfigService service;
    private MuzimaConfigResource configResource;

    @Before
    public void setUp() throws Exception {

        MuzimaConfig config = createConfig("asdfasdfasdf");
        service = mock(MuzimaConfigService.class);
        when(service.getConfigByUuid("asdfasdfasdf")).thenReturn(config);
        configResource = new MuzimaConfigResource();
        mockStatic(Context.class);
        PowerMockito.when(Context.getService(MuzimaConfigService.class)).thenReturn(service);
    }

    private MuzimaConfig createConfig(String uuid) {
        MuzimaConfig config = new MuzimaConfig();
        config.setId(uuid.hashCode());
        config.setUuid(uuid);
        config.setName("foo");
        config.setDescription("Testing");
        return config;
    }

    @Test
    public void retrieve_shouldGetMuzimaConfigByUUID() {
        Representation representation = mock(CustomRepresentation.class);
        when(representation.getRepresentation()).thenReturn("(uuid:uuid,id:id)");
        RequestContext context = mock(RequestContext.class);
        when(context.getRepresentation()).thenReturn(representation);

        configResource.retrieve("asdfasdfasdf", context);
        verify(service, times(1)).getConfigByUuid("asdfasdfasdf");
    }

    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void delete_notSupported() {
        configResource.delete(createConfig(""), "", null);
    }

    @Test
    public void save_shouldDelegateToService() throws SAXException, DocumentException, TransformerException, IOException, XPathExpressionException, ParserConfigurationException {
        MuzimaConfig config = createConfig("");
        configResource.save(config);
        try {
            verify(service, times(1)).save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAll_shouldGetAllConfigs() {
        Representation representation = mock(CustomRepresentation.class);
        RequestContext context = mock(RequestContext.class);

        RestService restService = mock(RestService.class);
        when(restService.getResourceBySupportedClass(MuzimaConfigResource.class)).thenReturn(null);
        PowerMockito.when(Context.getService(RestService.class)).thenReturn(restService);

        when(representation.getRepresentation()).thenReturn("(uuid:uuid,id:id)");
        when(context.getRepresentation()).thenReturn(representation);
        when(context.getStartIndex()).thenReturn(0);
        when(context.getLimit()).thenReturn(10);
        when(service.getAll()).thenReturn(getMuzimaConfigs());

        SimpleObject response = configResource.getAll(context);
        assertThat(response.containsKey("results"), is(true));
        List configs = (List) response.get("results");
        assertThat(configs.size(), is(5));
        verify(service, times(1)).getAll();
    }

    private ArrayList<MuzimaConfig> getMuzimaConfigs() {
        ArrayList<MuzimaConfig> muzimaConfigs = new ArrayList<MuzimaConfig>();
        muzimaConfigs.add(createConfig("1234asd"));
        muzimaConfigs.add(createConfig("2345asd"));
        muzimaConfigs.add(createConfig("3456asd"));
        muzimaConfigs.add(createConfig("4567asd"));
        muzimaConfigs.add(createConfig("5678asd"));
        return muzimaConfigs;
    }

    @Test
    public void getRepresentationDescription_shouldAddDefaultProperties() {
        Representation representation = mock(RefRepresentation.class);
        Set<String> keys = configResource.getRepresentationDescription(representation).getProperties().keySet();
        assertThat(keys.contains("id"), is(true));
        assertThat(keys.contains("uuid"), is(true));
        assertThat(keys.contains("name"), is(true));
        assertThat(keys.contains("description"), is(true));
        assertThat(keys.contains("configJson"), is(true));

        representation = mock(DefaultRepresentation.class);
        keys = configResource.getRepresentationDescription(representation).getProperties().keySet();
        assertThat(keys.contains("id"), is(true));
        assertThat(keys.contains("uuid"), is(true));
        assertThat(keys.contains("name"), is(true));
        assertThat(keys.contains("description"), is(true));
        assertThat(keys.contains("configJson"), is(true));
    }
}