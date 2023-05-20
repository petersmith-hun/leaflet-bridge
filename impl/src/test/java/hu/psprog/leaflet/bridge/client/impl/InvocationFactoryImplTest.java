package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.DeleteCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.GetCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PostCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PutCallStrategy;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.JerseyInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.DEVICE_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.X_CAPTCHA_RESPONSE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link InvocationFactoryImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class InvocationFactoryImplTest {

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
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final String PARAMETER_LIST = "parameterList";
    private static final List<String> VALUE_LIST = Arrays.asList("param1", "param2", "param3");
    private static final String GENERATED_QUERY_FOR_MULTI_PARAMETER_REQUEST = "parameterList=param1,param2,param3";
    private static final String RECAPTCHA_TOKEN = "recaptcha-token";
    private static final WebTarget WEB_TARGET = ClientBuilder.newBuilder()
            .build()
            .target(TARGET);

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RequestAuthentication requestAuthentication;

    @Mock
    private RequestAdapter requestAdapter;

    private InvocationFactoryImpl invocationFactory;

    @BeforeEach
    public void setup() {
        given(requestAdapter.provideDeviceID()).willReturn(DEVICE_ID);
        given(requestAdapter.provideClientID()).willReturn(CLIENT_ID);
        List<CallStrategy> callStrategyList = Arrays.asList(new PostCallStrategy(), new PutCallStrategy(), new GetCallStrategy(), new DeleteCallStrategy());
        invocationFactory = new InvocationFactoryImpl(requestAuthentication, callStrategyList, requestAdapter);
        Map<String, String> auth = new HashMap<>();
        auth.put(AUTHORIZATION, BEARER_TOKEN);
        given(requestAuthentication.getAuthenticationHeader()).willReturn(auth);
    }

    @Test
    public void shouldGetInvocationForAuthenticatedGetWithQueryParameters() {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(TestPath.ENTRIES_CATEGORY_PAGE)
                .addPathParameter(ID, String.valueOf(2L))
                .addPathParameter(PAGE, String.valueOf(1))
                .addRequestParameters(LIMIT, String.valueOf(10))
                .addRequestParameters(ORDER_BY, OrderBy.Entry.CREATED.name())
                .addRequestParameters(ORDER_DIRECTION, OrderDirection.ASC.name())
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(GET));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES_2_PAGE_1));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getUri().getQuery(), equalTo(QUERY_STRING));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
        assertThat(clientRequest.getHeaderString(CLIENT_ID_HEADER), equalTo(CLIENT_ID));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
    }

    @Test
    public void shouldGetInvocationForSimpleGet() {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(TestPath.ENTRIES)
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(GET));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedAndReCaptchaValidatedPost() {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(TestPath.ENTRIES)
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .recaptchaResponse(RECAPTCHA_TOKEN)
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(POST));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getEntity(), equalTo(entryCreateRequestModel));
        assertThat(clientRequest.getHeaderString(AUTHORIZATION), equalTo(BEARER_TOKEN));
        assertThat(clientRequest.getHeaderString(DEVICE_ID_HEADER), equalTo(DEVICE_ID));
        assertThat(clientRequest.getHeaderString(X_CAPTCHA_RESPONSE), equalTo(RECAPTCHA_TOKEN));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedDelete() {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(TestPath.ENTRIES_BY_ID)
                .addPathParameter(ID, String.valueOf(1L))
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

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
    public void shouldGetInvocationForAuthenticatedPut() {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(TestPath.ENTRIES_BY_ID)
                .addPathParameter(ID, String.valueOf(1L))
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

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

    @Test
    public void shouldGetInvocationForSimpleGetWithQueryParameterList() {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(TestPath.ENTRIES)
                .addRequestParameters(PARAMETER_LIST, VALUE_LIST)
                .build();

        // when
        Invocation result = invocationFactory.getInvocationFor(WEB_TARGET, restRequest);

        // then
        ClientRequest clientRequest = getClientRequest(result);
        assertThat(clientRequest.getMethod(), equalTo(GET));
        assertThat(clientRequest.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(clientRequest.getUri().getPort(), equalTo(PORT));
        assertThat(clientRequest.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(clientRequest.getUri().getQuery(), equalTo(GENERATED_QUERY_FOR_MULTI_PARAMETER_REQUEST));
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

    private enum TestPath implements Path {

        ENTRIES_CATEGORY_PAGE("/entries/{id}/page/{page}"),
        ENTRIES("/entries"),
        ENTRIES_BY_ID("/entries/{id}");

        private final String uri;

        TestPath(String uri) {
            this.uri = uri;
        }

        @Override
        public String getURI() {
            return uri;
        }
    }
}
