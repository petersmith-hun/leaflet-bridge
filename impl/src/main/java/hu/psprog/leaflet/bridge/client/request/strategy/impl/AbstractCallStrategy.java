package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;

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
     */
    Entity<?> createEntity(RESTRequest request) {
        return Entity.entity(extractBody(request), extractMediaType(request));
    }

    private MediaType extractMediaType(RESTRequest request) {
        return request.isMultipart()
                ? MediaType.MULTIPART_FORM_DATA_TYPE
                : MediaType.APPLICATION_JSON_TYPE;
    }

    private Object extractBody(RESTRequest request) {
        return Optional.ofNullable(request.getRequestBody())
                .map(requestBody -> Optional.ofNullable(request.getAdapter())
                        .map(adapter -> adapter.adapt(requestBody))
                        .orElse(requestBody))
                .orElse(StringUtils.EMPTY);
    }
}
