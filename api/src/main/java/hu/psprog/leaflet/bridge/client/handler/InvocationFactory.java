package hu.psprog.leaflet.bridge.client.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

/**
 * Prepares a Jersey invocation.
 *
 * @author Peter Smith
 */
public interface InvocationFactory {

    /**
     * Creates an {@link Invocation} for given {@link RESTRequest}.
     *
     * @param webTarget initial {@link WebTarget} to send request via
     * @param restRequest {@link RESTRequest} to build {@link Invocation} for
     * @return built {@link Invocation}
     */
    Invocation getInvocationFor(WebTarget webTarget, RESTRequest restRequest) throws JsonProcessingException;
}
