package hu.psprog.leaflet.bridge.client;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;

import java.util.Map;

/**
 * @author Peter Smith
 */
public interface BridgeClient {

    Map<String, Object> call(RESTRequest request) throws CommunicationFailureException;
}
