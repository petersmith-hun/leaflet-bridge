package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.domain.BridgeConstants;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import tools.jackson.databind.json.JsonMapper;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link InvocationFactory}.
 *
 * @author Peter Smith
 */
public class InvocationFactoryImpl implements InvocationFactory {

    private static final Map<RequestMethod, Function<String, ClassicHttpRequest>> HTTP_METHOD_MAP = Map.of(
            RequestMethod.GET, HttpGet::new,
            RequestMethod.POST, HttpPost::new,
            RequestMethod.PUT, HttpPut::new,
            RequestMethod.DELETE, HttpDelete::new
    );

    private final RequestAuthentication requestAuthentication;
    private final RequestAdapter requestAdapter;
    private final JsonMapper jsonMapper;

    public InvocationFactoryImpl(RequestAuthentication requestAuthentication, RequestAdapter requestAdapter, JsonMapper jsonMapper) {
        this.requestAuthentication = requestAuthentication;
        this.requestAdapter = requestAdapter;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public ClassicHttpRequest getInvocationFor(String baseURL, RESTRequest restRequest) {

        ClassicHttpRequest request = HTTP_METHOD_MAP.get(restRequest.getMethod())
                .apply(resolvePath(baseURL, restRequest));

        setRequestBody(restRequest, request);
        addCommonHeaders(restRequest, request);
        addCustomHeaderParameters(request, restRequest);
        authenticate(request, restRequest);

        return request;
    }

    private String resolvePath(String baseURL, RESTRequest restRequest) {

        String path = normalizePath(restRequest);
        for (Map.Entry<String, Object> pathVariable : restRequest.getPathParameters().entrySet()) {
            path = path.replace("{%s}".formatted(pathVariable.getKey()), String.valueOf(pathVariable.getValue()));
        }

        if (!restRequest.getRequestParameters().isEmpty()) {

            String queryParameters = restRequest.getRequestParameters()
                    .entrySet().stream()
                    .map(entry -> "%s=%s".formatted(entry.getKey(), encodeQueryParameter(entry.getValue())))
                    .collect(Collectors.joining("&"));

            path = "%s?%s".formatted(path, queryParameters);
        }

        return "%s%s".formatted(baseURL, path);
    }

    private String normalizePath(RESTRequest restRequest) {

        return restRequest.getPath().getURI().startsWith("/")
                ? restRequest.getPath().getURI()
                : "/%s".formatted(restRequest.getPath().getURI());
    }

    private String encodeQueryParameter(Object value) {
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private void setRequestBody(RESTRequest restRequest, ClassicHttpRequest request) {

        Optional.ofNullable(restRequest.getRequestBody())
                .map(requestBody -> Optional.ofNullable(restRequest.getAdapter())
                        .map(adapter -> adapter.adapt(requestBody))
                        .orElseGet(() -> createJSONRequestBody(requestBody)))
                .ifPresent(request::setEntity);
    }

    private StringEntity createJSONRequestBody(Serializable requestBody) {

        return requestBody instanceof String plainTextBody
                ? new StringEntity(plainTextBody)
                : new StringEntity(jsonMapper.writeValueAsString(requestBody), ContentType.APPLICATION_JSON);
    }

    private void addCommonHeaders(RESTRequest restRequest, ClassicHttpRequest request) {

        if (!restRequest.isMultipart()) {
            request.addHeader(BridgeConstants.CONTENT_TYPE_HEADER, BridgeConstants.CONTENT_TYPE_JSON);
        }
        request.addHeader(BridgeConstants.DEVICE_ID_HEADER, requestAdapter.provideDeviceID());
        request.addHeader(BridgeConstants.CLIENT_ID_HEADER, requestAdapter.provideClientID());
    }

    private void addCustomHeaderParameters(ClassicHttpRequest request, RESTRequest restRequest) {

        restRequest.getHeaderParameters().forEach((name, value) -> {

            if (Objects.nonNull(request.getFirstHeader(name))) {
                request.removeHeaders(name);
            }

            request.addHeader(name, value);
        });
    }

    private void authenticate(ClassicHttpRequest request, RESTRequest restRequest) {

        if (restRequest.isAuthenticationRequired()) {
            requestAuthentication.getAuthenticationHeader()
                    .forEach(request::addHeader);
        }
    }
}
