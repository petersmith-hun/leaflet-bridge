package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.Map;

/**
 * BridgeClient interface for user related calls.
 *
 * @author Peter Smith
 */
public interface UserBridgeService {

    /**
     * Retrieves all users.
     *
     * @return response as a Map
     * @throws CommunicationFailureException
     */
    Map<String, Object> getAllUsers() throws CommunicationFailureException;

    /**
     * Claims an authentication token.
     *
     * @param loginRequestModel {@link LoginRequestModel} object holding user information
     * @return response as a Map
     * @throws CommunicationFailureException
     */
    Map<String, Object> claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException;
}
