package hu.psprog.leaflet.bridge.client;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;

import javax.ws.rs.core.GenericType;

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
     * @param responseType response type to map JSON answer to
     * @return answer mapped to given T type
     * @throws CommunicationFailureException when request could not be fulfilled because of a technical error
     */
    <T> T call(RESTRequest request, GenericType<T> responseType) throws CommunicationFailureException;
}
