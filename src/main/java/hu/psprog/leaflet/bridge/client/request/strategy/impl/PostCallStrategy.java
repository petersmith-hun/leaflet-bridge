package hu.psprog.leaflet.bridge.client.request.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * Call strategy for POST requests.
 *
 * @author Peter Smith
 */
@Component
public class PostCallStrategy extends AbstractCallStrategy {

    @Override
    public Response call(Invocation.Builder builder, RESTRequest request) throws JsonProcessingException {
        return builder.post(createEntity(request));
    }

    @Override
    public RequestMethod forMethod() {
        return RequestMethod.POST;
    }
}
