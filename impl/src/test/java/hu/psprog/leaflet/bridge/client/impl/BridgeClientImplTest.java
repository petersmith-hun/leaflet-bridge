package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link BridgeClientImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class BridgeClientImplTest {

    private static final RESTRequest REST_REQUEST = RESTRequest.getBuilder().build();

    @Mock
    private InvocationFactoryImpl invocationFactory;

    @Mock
    private ResponseReaderImpl responseReader;

    @Mock
    private Response response;

    @Mock
    private Invocation invocation;

    @Mock
    private WebTarget webTarget;

    @InjectMocks
    private BridgeClientImpl bridgeClient;

    @Before
    public void setup() {
        given(invocation.invoke()).willReturn(response);
    }

    @Test
    public void shouldCallForWrappedResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        GenericType<WrapperBodyDataModel<BaseBodyDataModel>> genericType = new GenericType<>() {};
        given(invocationFactory.getInvocationFor(eq(webTarget), any(RESTRequest.class))).willReturn(invocation);

        // when
        bridgeClient.call(REST_REQUEST, genericType);

        // then
        verify(responseReader).read(eq(response), eq(genericType));
    }

    @Test(expected = CommunicationFailureException.class)
    public void shouldThrowCommunicationFailureExceptionOnCallForWrappedResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        GenericType<WrapperBodyDataModel<BaseBodyDataModel>> genericType = new GenericType<>() {};
        doThrow(JsonProcessingException.class).when(invocationFactory).getInvocationFor(eq(webTarget), any(RESTRequest.class));

        // when
        bridgeClient.call(REST_REQUEST, genericType);

        // then
        // expected exception
    }

    @Test
    public void shouldCallForNonWrappedResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        given(invocationFactory.getInvocationFor(eq(webTarget), any(RESTRequest.class))).willReturn(invocation);

        // when
        bridgeClient.call(REST_REQUEST, BaseBodyDataModel.class);

        // then
        verify(responseReader).read(eq(response), eq(new GenericType<BaseBodyDataModel>() {}));
    }

    @Test(expected = CommunicationFailureException.class)
    public void shouldThrowCommunicationFailureExceptionOnCallForNonWrappedResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        doThrow(JsonProcessingException.class).when(invocationFactory).getInvocationFor(eq(webTarget), any(RESTRequest.class));

        // when
        bridgeClient.call(REST_REQUEST, BaseBodyDataModel.class);

        // then
        // expected exception
    }

    @Test
    public void shouldCallForEmptyResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        given(invocationFactory.getInvocationFor(eq(webTarget), any(RESTRequest.class))).willReturn(invocation);

        // when
        bridgeClient.call(REST_REQUEST);

        // then
        verify(responseReader).read(eq(response));
    }

    @Test(expected = CommunicationFailureException.class)
    public void shouldThrowCommunicationFailureExceptionOnCallForEmptyResponse() throws CommunicationFailureException, JsonProcessingException {

        // given
        doThrow(JsonProcessingException.class).when(invocationFactory).getInvocationFor(eq(webTarget), any(RESTRequest.class));

        // when
        bridgeClient.call(REST_REQUEST);

        // then
        // expected exception
    }
}
