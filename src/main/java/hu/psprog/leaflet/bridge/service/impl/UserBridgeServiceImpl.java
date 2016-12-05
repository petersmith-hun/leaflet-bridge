package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of {@link UserBridgeService}.
 *
 * @author Peter Smith
 */
@Service
public class UserBridgeServiceImpl implements UserBridgeService {

    @Autowired
    private BridgeClient bridgeClient;

    @Override
    public Map<String, Object> getAllUsers() throws CommunicationFailureException {

        RESTRequest restRequest = new RESTRequest.Builder()
                .method(RequestMethod.GET)
                .path(Path.USERS)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest);
    }

    @Override
    public Map<String, Object> claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = new RESTRequest.Builder()
                .method(RequestMethod.POST)
                .path(Path.USERS_CLAIM)
                .requestBody(loginRequestModel)
                .build();

        return bridgeClient.call(restRequest);
    }
}
