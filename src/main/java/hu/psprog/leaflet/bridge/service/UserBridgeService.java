package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.Map;

/**
 * @author Peter Smith
 */
public interface UserBridgeService {

    Map<String, Object> getAllUsers() throws CommunicationFailureException;

    Map<String, Object> claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException;
}
