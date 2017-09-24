package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.DeleteCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.GetCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PostCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PutCallStrategy;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.JerseyInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static hu.psprog.leaflet.bridge.config.BridgeConfiguration.DEVICE_ID_HEADER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link InvocationFactory}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class InvocationFactoryTest {

    private static final String PAGE = "page";
    private static final String LIMIT = "limit";
    private static final String ORDER_BY = "orderBy";
    private static final String ORDER_DIRECTION = "orderDirection";
    private static final String ID = "id";
    private static final String BEARER_TOKEN = "Bearer token";
    private static final String AUTHORIZATION = "Authorization";
    private static final String QUERY_STRING = "limit=10&orderBy=CREATED&orderDirection=ASC";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 10000;
    private static final String POST = "POST";
    private static final String TEST_ENTRIES_2_PAGE_1 = "/test/entries/2/page/1";
    private static final String GET = "GET";
    private static final String TEST_ENTRIES = "/test/entries";
    private static final String DELETE = "DELETE";
    private static final String TEST_ENTRIES_1 = "/test/entries/1";
    private static final String PUT = "PUT";
    private static final String TARGET = "http://localhost:10000/test";
    private static final String DEVICE_ID = UUID.randomUUID().toString();

    @Mock
    private RequestAuthentication requestAuthentication;

    @Mock
    private HttpServletRequest httpServletRequest;

    private InvocationFactory invocationFactory;

    @Before
    public void setup() {
        given(httpServletRequest.getAttribute(DEVICE_ID_HEADER)).willReturn(DEVICE_ID);
        List<CallStrategy> callStrategyList = Arrays.asList(new PostCallStrategy(), new PutCallStrategy(), new GetCallStrategy(), new DeleteCallStrategy());
        WebTarget webTarget = ClientBuilder.newBuilder()
                .build()
                .target(TARGET);
        invocationFactory = new InvocationFactory(webTarget, requestAuthentication, callStrategyList, httpServletRequest);
        Map<String, String> auth = new HashMap<>();
        auth.put(AUTHORIZATION, BEARER_TOKEN);
        given(requestAuthentication.getAuthenticationHeader()).willReturn(auth);
    }

    @Test
    public void shouldGetInvocationForAuthenticatedGetWithQueryParameters() throws JsonProcessingException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ENTRIES_CATEGORY_PAGE)
                .addPathParameter(ID, String.valueOf(2L))
                .addPathParameter(PAGE, String.valueOf(1))
                .addRequestParameters(LIMIT, String.valueOf(10))
                .addRequestParameters(ORDER_BY, OrderBy.Entry.CREATED.name())
                .addRequestParameters(ORDER_DIRECTION, OrderDirection.ASC.name())
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(GET));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES_2_PAGE_1));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getUri().getQuery(), equalTo(QUERY_STRING));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
    }

    @Test
    public void shouldGetInvocationForSimpleGet() throws JsonProcessingException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ENTRIES)
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(GET));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedPost() throws JsonProcessingException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.ENTRIES)
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(POST));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getEntity(), equalTo(entryCreateRequestModel));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedDelete() throws JsonProcessingException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.ENTRIES_BY_ID)
                .addPathParameter(ID, String.valueOf(1L))
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(DELETE));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES_1));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedPut() throws JsonProcessingException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.ENTRIES_BY_ID)
                .addPathParameter(ID, String.valueOf(1L))
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(PUT));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES_1));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getEntity(), equalTo(entryCreateRequestModel));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
    }

    private ClientRequest getClientRequest(Invocation result) {
        try {
            Field requestContext = JerseyInvocation.class.getDeclaredField("requestContext");
            requestContext.setAccessible(true);
            return (ClientRequest) ReflectionUtils.getField(requestContext, result);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Failed to access requestContext field", e);
        }
    }
}
