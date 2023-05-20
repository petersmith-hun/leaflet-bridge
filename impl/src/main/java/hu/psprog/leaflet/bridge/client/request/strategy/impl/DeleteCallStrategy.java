package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import jakarta.ws.rs.client.Invocation;

/**
 * Call strategy for DELETE requests.
 *
 * @author Peter Smith
 */
public class DeleteCallStrategy extends AbstractCallStrategy {

    @Override
    public Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request) {
        return builder.buildDelete();
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.DELETE;
    }
}
