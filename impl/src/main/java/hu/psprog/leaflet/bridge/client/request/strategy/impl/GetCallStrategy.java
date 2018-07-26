package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;

import javax.ws.rs.client.Invocation;

/**
 * Call strategy for GET requests.
 *
 * @author Peter Smith
 */
public class GetCallStrategy extends AbstractCallStrategy {

    @Override
    public Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request) throws JsonProcessingException {
        return builder.buildGet();
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.GET;
    }
}
