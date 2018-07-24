package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link BridgeClientFactory}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class BridgeClientFactoryTest {

    private static final RESTRequest REST_REQUEST = RESTRequest.getBuilder().method(RequestMethod.GET).build();
    private static final EntryDataModel ENTRY_DATA_MODEL = EntryDataModel.getBuilder().withId(1L).build();
    private static final BridgeSettings BRIDGE_SETTINGS = BridgeSettings.getBuilder()
            .withHostUrl("http://localhost:9999/svc")
            .build();

    @Mock
    private Client client;

    @Mock
    private InvocationFactoryImpl invocationFactory;

    @Mock
    private ResponseReaderImpl responseReader;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation invocation;

    @Mock
    private Response response;

    @InjectMocks
    private BridgeClientFactory bridgeClientFactory;

    @Test
    public void shouldCreationBridgeClient() throws CommunicationFailureException, JsonProcessingException {

        // given
        given(client.target(BRIDGE_SETTINGS.getHostUrl())).willReturn(webTarget);
        given(invocationFactory.getInvocationFor(webTarget, REST_REQUEST)).willReturn(invocation);
        given(invocation.invoke()).willReturn(response);
        given(responseReader.read(response, new GenericType<>(EntryDataModel.class))).willReturn(ENTRY_DATA_MODEL);

        // when
        BridgeClient result = bridgeClientFactory.createBridgeClient(BRIDGE_SETTINGS);

        // then
        assertThat(result, notNullValue());
        EntryDataModel callResult = result.call(REST_REQUEST, EntryDataModel.class);
        assertThat(callResult, equalTo(ENTRY_DATA_MODEL));
    }
}