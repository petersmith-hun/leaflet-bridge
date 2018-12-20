package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.adapter.RequestBodyAdapter;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.JerseyInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link PostCallStrategy}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class PostCallStrategyTest {

    private static final String REQUEST_BODY = "request body";
    private static final String TRANSFORMED_REQUEST_BODY = "transformed request body";
    private static final String METHOD_POST = "POST";

    @Mock
    private RequestBodyAdapter mockedAdapter;

    @InjectMocks
    private PostCallStrategy postCallStrategy;

    private Invocation.Builder invocationBuilder;

    @Before
    public void setup() {
        invocationBuilder = ClientBuilder
                .newClient()
                .target("http://localhost")
                .request();
        given(mockedAdapter.adapt(any())).willReturn(TRANSFORMED_REQUEST_BODY);
    }

    @Test
    public void shouldPreparePOSTInvocationWithStandardBody() throws JsonProcessingException, IllegalAccessException {

        // given
        RESTRequest request = prepareRestRequest(true, false, false);

        // when
        Invocation result = postCallStrategy.prepareInvocation(invocationBuilder, request);

        // then
        assertThat(result, notNullValue());
        ClientRequestContext requestContext = extractRequestContext(result);
        assertThat(requestContext.getMethod(), equalTo(METHOD_POST));
        assertThat(requestContext.getEntity(), equalTo(REQUEST_BODY));
        assertThat(requestContext.getMediaType(), equalTo(MediaType.APPLICATION_JSON_TYPE));
        verifyZeroInteractions(mockedAdapter);
    }

    @Test
    public void shouldPreparePOSTInvocationWithEmptyBody() throws JsonProcessingException, IllegalAccessException {

        // given
        RESTRequest request = prepareRestRequest(false, false, false);

        // when
        Invocation result = postCallStrategy.prepareInvocation(invocationBuilder, request);

        // then
        assertThat(result, notNullValue());
        ClientRequestContext requestContext = extractRequestContext(result);
        assertThat(requestContext.getMethod(), equalTo(METHOD_POST));
        assertThat(requestContext.getEntity(), equalTo(StringUtils.EMPTY));
        assertThat(requestContext.getMediaType(), equalTo(MediaType.APPLICATION_JSON_TYPE));
        verifyZeroInteractions(mockedAdapter);
    }

    @Test
    public void shouldPreparePOSTInvocationWithStandardBodyAndAdapter() throws JsonProcessingException, IllegalAccessException {

        // given
        RESTRequest request = prepareRestRequest(true, false, true);

        // when
        Invocation result = postCallStrategy.prepareInvocation(invocationBuilder, request);

        // then
        assertThat(result, notNullValue());
        ClientRequestContext requestContext = extractRequestContext(result);
        assertThat(requestContext.getMethod(), equalTo(METHOD_POST));
        assertThat(requestContext.getEntity(), equalTo(TRANSFORMED_REQUEST_BODY));
        assertThat(requestContext.getMediaType(), equalTo(MediaType.APPLICATION_JSON_TYPE));
        verify(mockedAdapter).adapt(REQUEST_BODY);
    }

    @Test
    public void shouldPreparePOSTInvocationWithMultipartBodyAndAdapter() throws JsonProcessingException, IllegalAccessException {

        // given
        RESTRequest request = prepareRestRequest(true, true, true);

        // when
        Invocation result = postCallStrategy.prepareInvocation(invocationBuilder, request);

        // then
        assertThat(result, notNullValue());
        ClientRequestContext requestContext = extractRequestContext(result);
        assertThat(requestContext.getMethod(), equalTo(METHOD_POST));
        assertThat(requestContext.getEntity(), equalTo(TRANSFORMED_REQUEST_BODY));
        assertThat(requestContext.getMediaType(), equalTo(MediaType.MULTIPART_FORM_DATA_TYPE));
        verify(mockedAdapter).adapt(REQUEST_BODY);
    }

    @Test
    public void shouldPreparePOSTInvocationWithMultipartBodyWithoutAdapter() throws JsonProcessingException, IllegalAccessException {

        // given
        RESTRequest request = prepareRestRequest(true, true, false);

        // when
        Invocation result = postCallStrategy.prepareInvocation(invocationBuilder, request);

        // then
        assertThat(result, notNullValue());
        ClientRequestContext requestContext = extractRequestContext(result);
        assertThat(requestContext.getMethod(), equalTo(METHOD_POST));
        assertThat(requestContext.getEntity(), equalTo(REQUEST_BODY));
        assertThat(requestContext.getMediaType(), equalTo(MediaType.MULTIPART_FORM_DATA_TYPE));
        verifyZeroInteractions(mockedAdapter);
    }

    private ClientRequestContext extractRequestContext(Invocation invocation) throws IllegalAccessException {

        Field requestContextField = ReflectionUtils.findField(JerseyInvocation.class, "requestContext");
        requestContextField.setAccessible(true);

        return (ClientRequestContext) requestContextField.get(invocation);
    }

    private RESTRequest prepareRestRequest(boolean withBody, boolean multipart, boolean withAdapter) {

        RESTRequest.RESTRequestBuilder builder = RESTRequest.getBuilder()
                .requestBody(withBody ? REQUEST_BODY : null);

        if (multipart) {
            builder.multipart();
        }

        if (withAdapter) {
            builder.adapter(mockedAdapter);
        }

        return builder.build();
    }
}