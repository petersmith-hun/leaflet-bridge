package hu.psprog.leaflet.bridge.client;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;

import java.util.Map;

/**
 * Interface for Leaflet REST communication bridge, which is technically an API between a frontend and the Leaflet
 * backend application.
 *
 * @author Peter Smith
 */
public interface BridgeClient {

    /**
     * Sends a request to the Leaflet backend application.
     *
     * @param request {@link RESTRequest} object containing all necessary request parameters
     * @return JSON formatted answer as a key-value map
     * @throws CommunicationFailureException when request could not be fulfilled because of a technical error
     */
    Map<String, Object> call(RESTRequest request) throws CommunicationFailureException;
}
