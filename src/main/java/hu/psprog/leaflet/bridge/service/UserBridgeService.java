package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for user related calls.
 *
 * @author Peter Smith
 */
public interface UserBridgeService {

    /**
     * Retrieves all users.
     *
     * @return response mapped to {@link UserListDataModel}
     * @throws CommunicationFailureException
     */
    UserListDataModel getAllUsers() throws CommunicationFailureException;

    /**
     * Claims an authentication token.
     *
     * @param loginRequestModel {@link LoginRequestModel} object holding user information
     * @return response mapped to {@link LoginResponseDataModel}
     * @throws CommunicationFailureException
     */
    LoginResponseDataModel claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException;
}
