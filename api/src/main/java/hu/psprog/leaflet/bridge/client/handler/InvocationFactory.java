package hu.psprog.leaflet.bridge.client.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * @author Peter Smith
 */
public interface InvocationFactory {

    Invocation getInvocationFor(WebTarget webTarget, RESTRequest restRequest) throws JsonProcessingException;
}
