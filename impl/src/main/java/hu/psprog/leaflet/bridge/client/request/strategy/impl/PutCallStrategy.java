package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Invocation;

/**
 * Call strategy for PUT requests.
 *
 * @author Peter Smith
 */
@Component
public class PutCallStrategy extends AbstractCallStrategy {

    @Override
    public Invocation prepareInvocation(Invocation.Builder builder, RESTRequest request) throws JsonProcessingException {
        return builder.buildPut(createEntity(request));
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.PUT;
    }
}
