package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.model.BodyWrapperModel;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;

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
    public UserListDataModel getAllUsers() throws CommunicationFailureException {

        RESTRequest restRequest = new RESTRequest.Builder()
                .method(RequestMethod.GET)
                .path(Path.USERS)
                .authenticated()
                .build();

        return bridgeClient
                .call(restRequest, new GenericType<BodyWrapperModel<UserListDataModel>>() {})
                .getBody();
    }

    @Override
    public LoginResponseDataModel claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = new RESTRequest.Builder()
                .method(RequestMethod.POST)
                .path(Path.USERS_CLAIM)
                .requestBody(loginRequestModel)
                .build();

        return bridgeClient
                .call(restRequest, new GenericType<BodyWrapperModel<LoginResponseDataModel>>() {})
                .getBody();
    }
}
