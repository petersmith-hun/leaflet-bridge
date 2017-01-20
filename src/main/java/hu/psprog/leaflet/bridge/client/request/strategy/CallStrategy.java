package hu.psprog.leaflet.bridge.client.request.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * Call strategy based on request method.
 *
 * @author Peter Smith
 */
public interface CallStrategy {

    /**
     * Sends given request for the leaflet backend.
     *
     * @param request {@link RESTRequest} object containing request parameters
     * @return response of type {@link Response}
     * @throws JsonProcessingException on JSON processing failure
     */
    Response call(Invocation.Builder builder, RESTRequest request) throws JsonProcessingException;

    /**
     * Returns method to use strategy for.
     *
     * @return RequestMethod element
     */
    RequestMethod forMethod();
}

