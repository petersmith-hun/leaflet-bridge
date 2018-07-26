package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;

import javax.ws.rs.client.Invocation;

/**
 * Call strategy for DELETE requests.
 *
 * @author Peter Smith
 */
public class DeleteCallStrategy extends AbstractCallStrategy {

    @Override
    public Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request) throws JsonProcessingException {
        return builder.buildDelete();
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.DELETE;
    }
}
