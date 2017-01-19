package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

/**
 * Implementation of {@link BridgeClient}.
 *
 * @author Peter Smith
 */
@Component
public class BridgeClientImpl implements BridgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeClientImpl.class);

    @Autowired
    private Client bridgeClient;

    @Autowired
    private WebTarget webTarget;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequestAuthentication requestAuthentication;

    @Override
    public <T> T call(RESTRequest request, GenericType<T> responseType) throws CommunicationFailureException {

        try {
            return doCall(request, responseType);
        } catch (IOException e) {
            LOGGER.error("Bridge failed to process request [{}]", request);
            throw new CommunicationFailureException(e);
        }
    }

    private <T> T doCall(RESTRequest request, GenericType<T> responseType) throws IOException {

        WebTarget target = webTarget.path(resolvePath(request));
        fillRequestParameters(target, request);

        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
        authenticate(builder, request);

        Response response = null;
        switch (request.getMethod()) {
            case GET:
                response = builder.get();
                break;
            case POST:
                response = builder.post(createEntity(request));
                break;
            case PUT:
                response = builder.put(createEntity(request));
                break;
            case DELETE:
                response = builder.delete();
                break;
        }

        return response.readEntity(responseType);
    }

    private void authenticate(Invocation.Builder builder, RESTRequest request) {

        if (request.isAuthenticationRequired()) {
            requestAuthentication.getAuthenticationHeader().entrySet()
                    .forEach(entry -> builder.header(entry.getKey(), entry.getValue()));
        }
    }

    private Entity createEntity(RESTRequest request) throws JsonProcessingException {

        return Entity.entity(request.getRequestBody(), MediaType.APPLICATION_JSON_TYPE);
    }

    private String resolvePath(RESTRequest request) {

        String path = request.getPath().getUri();
        request.getPathParameters().entrySet()
                .forEach(entry -> fillPathVariablePlaceholder(entry, path));

        return path;
    }

    private void fillRequestParameters(WebTarget webTarget, RESTRequest request) {

        request.getRequestParameters().entrySet()
                .forEach(entry -> webTarget.queryParam(entry.getKey(), entry.getValue()));
    }

    private String fillPathVariablePlaceholder(Map.Entry<String, String> entry, String path) {

        return path.replace("{" + entry.getKey() + "}", entry.getValue());
    }
}
