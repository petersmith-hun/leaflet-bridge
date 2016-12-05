package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
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
    public Map<String, Object> call(RESTRequest request) throws CommunicationFailureException {

        WebTarget currentTarget = webTarget.path(resolvePath(request));
        fillRequestParameters(currentTarget, request);

        try {
            return doCall(currentTarget, request);
        } catch (IOException e) {
            LOGGER.error("Bridge failed to process request [{}]", request);
            throw new CommunicationFailureException(e);
        }
    }

    private Map<String, Object> doCall(WebTarget target, RESTRequest request) throws IOException {

        String response = null;
        Invocation.Builder builder = target.request();
        authenticate(builder, request);

        switch (request.getMethod()) {
            case GET:
                response = builder.get(String.class);
                break;
            case POST:
                response = builder.post(createEntity(request), String.class);
                break;
            case PUT:
                response = builder.put(createEntity(request), String.class);
                break;
            case DELETE:
                response = builder.delete(String.class);
                break;
        }

        return objectMapper.readValue(response, new TypeReference<HashMap<String, Object>>() {});
    }

    private void authenticate(Invocation.Builder builder, RESTRequest request) {

        if (request.isAuthenticationRequired()) {
            requestAuthentication.getAuthenticationHeader().entrySet()
                    .forEach(entry -> builder.header(entry.getKey(), entry.getValue()));
        }
    }

    private Entity createEntity(RESTRequest request) throws JsonProcessingException {

        String requestBodyAsJSON = objectMapper.writeValueAsString(request.getRequestBody());

        return Entity.entity(requestBodyAsJSON, MediaType.APPLICATION_JSON_TYPE);
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
