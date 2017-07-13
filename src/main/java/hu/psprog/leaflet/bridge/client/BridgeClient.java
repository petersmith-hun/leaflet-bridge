package hu.psprog.leaflet.bridge.client;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
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
     * For wrapped responses.
     *
     * @param request {@link RESTRequest} object containing all necessary request parameters
     * @param responseType response type as {@link GenericType} to map JSON answer to
     * @return answer mapped to given T type
     * @throws CommunicationFailureException when request could not be fulfilled because of a technical error
     */
    <T extends BaseBodyDataModel> WrapperBodyDataModel<T> call(RESTRequest request, GenericType<WrapperBodyDataModel<T>> responseType) throws CommunicationFailureException;

    /**
     * Sends a request to the Leaflet backend application.
     * For unwrapped responses.
     *
     * @param request {@link RESTRequest} object containing all necessary request parameters
     * @param responseType response type as {@link GenericType} to map JSON answer to
     * @return answer mapped to given T type
     * @throws CommunicationFailureException when request could not be fulfilled because of a technical error
     */
    <T extends BaseBodyDataModel> T call(RESTRequest request, Class<T> responseType) throws CommunicationFailureException;

    /**
     * Sends a request to the Leaflet backend application.
     * For void responses.
     *
     * @param request {@link RESTRequest} object containing all necessary request parameters
     * @throws CommunicationFailureException when request could not be fulfilled because of a technical error
     */
    void call(RESTRequest request) throws CommunicationFailureException;
}
