package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.request.common.OrderBy;
import hu.psprog.leaflet.api.rest.request.common.OrderDirection;
import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CONTENT_TYPE_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CONTENT_TYPE_JSON;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.DEVICE_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.X_CAPTCHA_RESPONSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

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
    private static final String JSON_REQUEST_BODY = "{\"body\": true}";

    @Mock
    private RequestAuthentication requestAuthentication;

    @Mock
    private RequestAdapter requestAdapter;

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private InvocationFactoryImpl invocationFactory;

    @BeforeEach
    public void setup() {
        given(requestAdapter.provideDeviceID()).willReturn(DEVICE_ID);
        given(requestAdapter.provideClientID()).willReturn(CLIENT_ID);
    }

    @Test
    public void shouldGetInvocationForAuthenticatedGetWithQueryParameters() throws URISyntaxException, ProtocolException {

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

        given(requestAuthentication.getAuthenticationHeader()).willReturn(Map.of(AUTHORIZATION, BEARER_TOKEN));

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(GET));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES_2_PAGE_1));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(result.getUri().getQuery(), equalTo(QUERY_STRING));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(CONTENT_TYPE_JSON));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
        assertThat(result.getHeader(CLIENT_ID_HEADER).getValue(), equalTo(CLIENT_ID));
        assertThat(result.getHeader(AUTHORIZATION).getValue(), equalTo(BEARER_TOKEN));
    }

    @Test
    public void shouldGetInvocationForSimpleGet() throws URISyntaxException, ProtocolException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(TestPath.ENTRIES)
                .multipart()
                .build();

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(GET));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER), nullValue());
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedAndReCaptchaValidatedPost() throws URISyntaxException, ProtocolException, IOException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(TestPath.ENTRIES)
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .recaptchaResponse(RECAPTCHA_TOKEN)
                .build();

        given(requestAuthentication.getAuthenticationHeader()).willReturn(Map.of(AUTHORIZATION, BEARER_TOKEN));
        given(jsonMapper.writeValueAsString(entryCreateRequestModel)).willReturn(JSON_REQUEST_BODY);

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(POST));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(new String(result.getEntity().getContent().readAllBytes()), equalTo(JSON_REQUEST_BODY));
        assertThat(result.getEntity().getContentType(), equalTo(ContentType.APPLICATION_JSON.toString()));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(CONTENT_TYPE_JSON));
        assertThat(result.getHeader(AUTHORIZATION).getValue(), equalTo(BEARER_TOKEN));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
        assertThat(result.getHeader(X_CAPTCHA_RESPONSE).getValue(), equalTo(RECAPTCHA_TOKEN));
    }

    @Test
    public void shouldGetInvocationForPostWithPlainTextBody() throws URISyntaxException, ProtocolException, IOException {

        // given
        String requestBody = "search with conditions source = 'leaflet'";
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(TestPath.ENTRIES)
                .requestBody(requestBody)
                .addHeaderParameter("Content-Type", ContentType.TEXT_PLAIN.toString())
                .authenticated()
                .recaptchaResponse(RECAPTCHA_TOKEN)
                .build();

        given(requestAuthentication.getAuthenticationHeader()).willReturn(Map.of(AUTHORIZATION, BEARER_TOKEN));

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(POST));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(new String(result.getEntity().getContent().readAllBytes()), equalTo(requestBody));
        assertThat(result.getEntity().getContentType(), equalTo(ContentType.TEXT_PLAIN.toString()));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(ContentType.TEXT_PLAIN.toString()));
        assertThat(result.getHeader(AUTHORIZATION).getValue(), equalTo(BEARER_TOKEN));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
        assertThat(result.getHeader(X_CAPTCHA_RESPONSE).getValue(), equalTo(RECAPTCHA_TOKEN));

        verifyNoInteractions(jsonMapper);
    }

    @ParameterizedTest
    @EnumSource(value = TestPath.class, names = {"ENTRIES_BY_ID", "ENTRIES_BY_ID_WITHOUT_LEADING_SLASH"})
    public void shouldGetInvocationForAuthenticatedDelete(TestPath testPath) throws URISyntaxException, ProtocolException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(testPath)
                .addPathParameter(ID, String.valueOf(1L))
                .authenticated()
                .build();

        given(requestAuthentication.getAuthenticationHeader()).willReturn(Map.of(AUTHORIZATION, BEARER_TOKEN));

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(DELETE));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES_1));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(CONTENT_TYPE_JSON));
        assertThat(result.getHeader(AUTHORIZATION).getValue(), equalTo(BEARER_TOKEN));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForAuthenticatedPut() throws URISyntaxException, ProtocolException, IOException {

        // given
        EntryCreateRequestModel entryCreateRequestModel = new EntryCreateRequestModel();
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(TestPath.ENTRIES_BY_ID)
                .addPathParameter(ID, String.valueOf(1L))
                .requestBody(entryCreateRequestModel)
                .authenticated()
                .build();

        given(requestAuthentication.getAuthenticationHeader()).willReturn(Map.of(AUTHORIZATION, BEARER_TOKEN));
        given(jsonMapper.writeValueAsString(entryCreateRequestModel)).willReturn(JSON_REQUEST_BODY);

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(PUT));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES_1));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(new String(result.getEntity().getContent().readAllBytes()), equalTo(JSON_REQUEST_BODY));
        assertThat(result.getEntity().getContentType(), equalTo(ContentType.APPLICATION_JSON.toString()));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(CONTENT_TYPE_JSON));
        assertThat(result.getHeader(AUTHORIZATION).getValue(), equalTo(BEARER_TOKEN));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
    }

    @Test
    public void shouldGetInvocationForSimpleGetWithQueryParameterList() throws URISyntaxException, ProtocolException {

        // given
        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(TestPath.ENTRIES)
                .addRequestParameters(PARAMETER_LIST, VALUE_LIST)
                .build();

        // when
        ClassicHttpRequest result = invocationFactory.getInvocationFor(TARGET, restRequest);

        // then
        assertThat(result.getMethod(), equalTo(GET));
        assertThat(result.getUri().getPath(), equalTo(TEST_ENTRIES));
        assertThat(result.getUri().getPort(), equalTo(PORT));
        assertThat(result.getUri().getHost(), equalTo(LOCALHOST));
        assertThat(result.getUri().getQuery(), equalTo(GENERATED_QUERY_FOR_MULTI_PARAMETER_REQUEST));
        assertThat(result.getHeader(CONTENT_TYPE_HEADER).getValue(), equalTo(CONTENT_TYPE_JSON));
        assertThat(result.getHeader(DEVICE_ID_HEADER).getValue(), equalTo(DEVICE_ID));
    }

    public enum TestPath implements Path {

        ENTRIES_CATEGORY_PAGE("/entries/{id}/page/{page}"),
        ENTRIES("/entries"),
        ENTRIES_BY_ID("/entries/{id}"),
        ENTRIES_BY_ID_WITHOUT_LEADING_SLASH("entries/{id}");

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
