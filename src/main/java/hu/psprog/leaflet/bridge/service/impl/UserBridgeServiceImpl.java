package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserInitializeRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserBridgeService}.
 *
 * @author Peter Smith
 */
@Service
public class UserBridgeServiceImpl implements UserBridgeService {

    private BridgeClient bridgeClient;

    @Autowired
    public UserBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public UserListDataModel getAllUsers() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.USERS)
                .authenticated()
                .build();

        return bridgeClient
                .call(restRequest, UserListDataModel.class);
    }

    @Override
    public ExtendedUserDataModel createUser(UserCreateRequestModel userCreateRequestModel) throws CommunicationFailureException {
        return null;
    }

    @Override
    public ExtendedUserDataModel initUserDatabase(UserInitializeRequestModel userInitializeRequestModel) throws CommunicationFailureException {
        return null;
    }

    @Override
    public ExtendedUserDataModel getUserByID(Long userID) throws CommunicationFailureException {
        return null;
    }

    @Override
    public void deleteUser(Long userID) throws CommunicationFailureException {

    }

    @Override
    public ExtendedUserDataModel updateRole(Long userID, UpdateRoleRequestModel updateRoleRequestModel) throws CommunicationFailureException {
        return null;
    }

    @Override
    public ExtendedUserDataModel updateProfile(Long userID, UpdateProfileRequestModel updateProfileRequestModel) throws CommunicationFailureException {
        return null;
    }

    @Override
    public ExtendedUserDataModel updatePassword(Long userID, UserPasswordRequestModel userPasswordRequestModel) throws CommunicationFailureException {
        return null;
    }

    @Override
    public LoginResponseDataModel claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_CLAIM)
                .requestBody(loginRequestModel)
                .build();

        return bridgeClient
                .call(restRequest, LoginResponseDataModel.class);
    }

    @Override
    public ExtendedUserDataModel signUp(UserInitializeRequestModel userInitializeRequestModel) throws CommunicationFailureException {
        return null;
    }
}
