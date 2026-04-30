package hu.psprog.leaflet.bridge.client.handler;

import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.apache.hc.core5.http.ClassicHttpRequest;

/**
 * Prepares a Jersey invocation.
 *
 * @author Peter Smith
 */
public interface InvocationFactory {

    /**
     * Creates a {@link ClassicHttpRequest} for given {@link RESTRequest}.
     *
     * @param baseURL host base URL to prepend to each request path
     * @param restRequest {@link RESTRequest} to build {@link ClassicHttpRequest} for
     * @return built {@link ClassicHttpRequest}
     */
    ClassicHttpRequest getInvocationFor(String baseURL, RESTRequest restRequest);
}
