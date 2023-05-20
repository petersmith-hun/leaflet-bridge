package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import jakarta.ws.rs.client.Invocation;

/**
 * Call strategy for PUT requests.
 *
 * @author Peter Smith
 */
public class PutCallStrategy extends AbstractCallStrategy {

    @Override
    public Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request) {
        return builder.buildPut(createEntity(request));
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.PUT;
    }
}
