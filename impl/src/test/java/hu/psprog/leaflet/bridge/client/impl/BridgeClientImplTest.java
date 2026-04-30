package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link BridgeClientImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class BridgeClientImplTest {

    private static final RESTRequest REST_REQUEST = RESTRequest.getBuilder().build();
    private static final String HOST_URL = "http://localhost:8080";
    private static final TypeReference<WrapperBodyDataModel<EntryDataModel>> WRAPPED_TYPE_REFERENCE = new TypeReference<>() {};
    private static final EntryDataModel NON_WRAPPED_RESULT = EntryDataModel.getBuilder()
            .withId(1L)
            .build();
    private static final WrapperBodyDataModel<EntryDataModel> WRAPPED_RESULT = WrapperBodyDataModel.<EntryDataModel>getBuilder()
            .withBody(NON_WRAPPED_RESULT)
            .build();

    @Mock
    private HttpClient httpClient;

    @Mock
    private ClassicHttpRequest classicHttpRequest;

    @Mock
    private ClassicHttpResponse classicHttpResponse;

    @Mock
    private BridgeSettings bridgeSettings;

    @Mock
    private InvocationFactoryImpl invocationFactory;

    @Mock
    private ResponseReaderImpl responseReader;

    @Captor
    private ArgumentCaptor<HttpClientResponseHandler<WrapperBodyDataModel<EntryDataModel>>> wrappedResponseCaptor;

    @Captor
    private ArgumentCaptor<HttpClientResponseHandler<EntryDataModel>> nonWrappedResponseCaptor;

    @Captor
    private ArgumentCaptor<HttpClientResponseHandler<Object>> nullResponseCaptor;

    @Captor
    private ArgumentCaptor<TypeReference<EntryDataModel>> typeReferenceCaptor;

    private BridgeClientImpl bridgeClient;

    @BeforeEach
    public void setup() {
        prepareBridgeClient(false);
    }

    @Test
    public void shouldCallForWrappedResponseWithTrailingSlashInBaseURL() throws CommunicationFailureException, IOException, HttpException {

        // given
        prepareBridgeClient(true);

        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        given(httpClient.execute(eq(classicHttpRequest), wrappedResponseCaptor.capture())).willReturn(WRAPPED_RESULT);
        given(responseReader.read(classicHttpResponse, WRAPPED_TYPE_REFERENCE)).willReturn(WRAPPED_RESULT);

        // when
        var result = bridgeClient.call(REST_REQUEST, WRAPPED_TYPE_REFERENCE);

        // then
        wrappedResponseCaptor.getValue().handleResponse(classicHttpResponse);
        assertThat(result, equalTo(WRAPPED_RESULT));
    }

    @Test
    public void shouldCallForWrappedResponseWithoutTrailingSlashInBaseURL() throws CommunicationFailureException, IOException, HttpException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        given(httpClient.execute(eq(classicHttpRequest), wrappedResponseCaptor.capture())).willReturn(WRAPPED_RESULT);
        given(responseReader.read(classicHttpResponse, WRAPPED_TYPE_REFERENCE)).willReturn(WRAPPED_RESULT);

        // when
        var result = bridgeClient.call(REST_REQUEST, WRAPPED_TYPE_REFERENCE);

        // then
        wrappedResponseCaptor.getValue().handleResponse(classicHttpResponse);
        assertThat(result, equalTo(WRAPPED_RESULT));
    }

    @Test
    public void shouldCallForNonWrappedResponse() throws CommunicationFailureException, IOException, HttpException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        given(httpClient.execute(eq(classicHttpRequest), nonWrappedResponseCaptor.capture())).willReturn(NON_WRAPPED_RESULT);
        given(responseReader.read(eq(classicHttpResponse), typeReferenceCaptor.capture())).willReturn(NON_WRAPPED_RESULT);

        // when
        var result = bridgeClient.call(REST_REQUEST, EntryDataModel.class);

        // then
        nonWrappedResponseCaptor.getValue().handleResponse(classicHttpResponse);
        assertThat(result, equalTo(NON_WRAPPED_RESULT));
        assertThat(typeReferenceCaptor.getValue().getType(), equalTo(EntryDataModel.class));
    }

    @Test
    public void shouldCallForEmptyResponse() throws CommunicationFailureException, IOException, HttpException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        given(httpClient.execute(eq(classicHttpRequest), nullResponseCaptor.capture())).willReturn(null);

        // when
        bridgeClient.call(REST_REQUEST);

        // then
        nullResponseCaptor.getValue().handleResponse(classicHttpResponse);
        verify(responseReader).read(classicHttpResponse);
    }

    @Test
    public void shouldThrowCommunicationFailureExceptionOnCallForWrappedResponse() {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willThrow(RuntimeException.class);

        // when
        assertThrows(CommunicationFailureException.class, () -> bridgeClient.call(REST_REQUEST, WRAPPED_TYPE_REFERENCE));

        // then
        // expected exception
    }

    @Test
    public void shouldThrowCommunicationFailureExceptionOnCallForNonWrappedResponse() throws IOException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        given(httpClient.execute(eq(classicHttpRequest), nonWrappedResponseCaptor.capture())).willThrow(IOException.class);

        // when
        assertThrows(CommunicationFailureException.class, () -> bridgeClient.call(REST_REQUEST, EntryDataModel.class));

        // then
        // expected exception
    }

    @Test
    public void shouldThrowCommunicationFailureExceptionOnCallForEmptyResponse() throws IOException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        doThrow(RuntimeException.class).when(responseReader).read(classicHttpResponse);
        doAnswer(invocation -> {
            var handler = invocation.getArgument(1, HttpClientResponseHandler.class);
            handler.handleResponse(classicHttpResponse);
            return null;
        }).when(httpClient).execute(eq(classicHttpRequest), nullResponseCaptor.capture());

        // when
        assertThrows(CommunicationFailureException.class, () -> {
            bridgeClient.call(REST_REQUEST);
            nullResponseCaptor.getValue().handleResponse(classicHttpResponse);
        });

        // then
        // expected exception
    }

    @Test
    public void shouldThrowKnownExceptionOnCallForWrappedResponse() throws IOException {

        // given
        given(invocationFactory.getInvocationFor(HOST_URL, REST_REQUEST)).willReturn(classicHttpRequest);
        doThrow(ResourceNotFoundException.class).when(responseReader).read(classicHttpResponse);
        doAnswer(invocation -> {
            var handler = invocation.getArgument(1, HttpClientResponseHandler.class);
            handler.handleResponse(classicHttpResponse);
            return null;
        }).when(httpClient).execute(eq(classicHttpRequest), nullResponseCaptor.capture());

        // when
        assertThrows(ResourceNotFoundException.class, () -> {
            bridgeClient.call(REST_REQUEST);
            nullResponseCaptor.getValue().handleResponse(classicHttpResponse);
        });

        // then
        // expected exception
    }

    private void prepareBridgeClient(boolean withTrailingSlash) {

        given(bridgeSettings.getHostUrl()).willReturn(withTrailingSlash
                ?  HOST_URL + "/"
                : HOST_URL);

        bridgeClient = new BridgeClientImpl(httpClient, bridgeSettings, invocationFactory, responseReader);
    }
}
