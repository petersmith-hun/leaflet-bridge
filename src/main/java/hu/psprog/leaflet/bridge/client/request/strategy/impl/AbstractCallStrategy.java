package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.Optional;

/**
 * Abstract {@link CallStrategy} implementation for common methods.
 *
 * @author Peter Smith
 */
abstract class AbstractCallStrategy implements CallStrategy {

    /**
     * Wraps a request body into JaxWS compatible {@link Entity}.
     *
     * @param request {@link RESTRequest} object containing request body
     * @return Entity object
     * @throws JsonProcessingException on JSON processing failure
     */
    Entity createEntity(RESTRequest request) throws JsonProcessingException {

        return Entity.entity(extractBody(request), MediaType.APPLICATION_JSON_TYPE);
    }

    private Serializable extractBody(RESTRequest request) {
        return Optional.ofNullable(request.getRequestBody())
                .orElse(StringUtils.EMPTY);
    }
}
