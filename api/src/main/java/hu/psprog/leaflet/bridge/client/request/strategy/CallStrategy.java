package hu.psprog.leaflet.bridge.client.request.strategy;

import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import jakarta.ws.rs.client.Invocation;

/**
 * Call strategy based on request method.
 *
 * @author Peter Smith
 */
public interface CallStrategy {

    /**
     * Prepares invocation for given request for the leaflet backend.
     *
     * @param builder {@link Invocation.Builder} object
     * @param request {@link RESTRequest} object containing request parameters
     * @return Invocation object
     */
    Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request);

    /**
     * Returns method to use strategy for.
     *
     * @return RequestMethod element
     */
    RequestMethod forMethod();
}

