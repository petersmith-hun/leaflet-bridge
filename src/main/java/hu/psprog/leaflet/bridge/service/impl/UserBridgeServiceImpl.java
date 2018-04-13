package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserInitializeRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link UserBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
class UserBridgeServiceImpl implements UserBridgeService {

    private static final String ID = "id";

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

        return bridgeClient.call(restRequest, UserListDataModel.class);
    }

    @Override
    public ExtendedUserDataModel createUser(UserCreateRequestModel userCreateRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS)
                .authenticated()
                .requestBody(userCreateRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public ExtendedUserDataModel initUserDatabase(UserInitializeRequestModel userInitializeRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_INIT)
                .requestBody(userInitializeRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public ExtendedUserDataModel getUserByID(Long userID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.USERS_ID)
                .authenticated()
                .addPathParameter(ID, String.valueOf(userID))
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public void deleteUser(Long userID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.USERS_ID)
                .authenticated()
                .addPathParameter(ID, String.valueOf(userID))
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public ExtendedUserDataModel updateRole(Long userID, UpdateRoleRequestModel updateRoleRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.USERS_ROLE)
                .authenticated()
                .addPathParameter(ID, String.valueOf(userID))
                .requestBody(updateRoleRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public ExtendedUserDataModel updateProfile(Long userID, UpdateProfileRequestModel updateProfileRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.USERS_PROFILE)
                .authenticated()
                .addPathParameter(ID, String.valueOf(userID))
                .requestBody(updateProfileRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public ExtendedUserDataModel updatePassword(Long userID, UserPasswordRequestModel userPasswordRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.USERS_PASSWORD)
                .authenticated()
                .addPathParameter(ID, String.valueOf(userID))
                .requestBody(userPasswordRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public LoginResponseDataModel claimToken(LoginRequestModel loginRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_CLAIM)
                .requestBody(loginRequestModel)
                .build();

        return bridgeClient.call(restRequest, LoginResponseDataModel.class);
    }

    @Override
    public ExtendedUserDataModel signUp(UserInitializeRequestModel userInitializeRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_REGISTER)
                .requestBody(userInitializeRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedUserDataModel.class);
    }

    @Override
    public void revokeToken() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_REVOKE)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void demandPasswordReset(PasswordResetDemandRequestModel passwordResetDemandRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.USERS_RECLAIM)
                .requestBody(passwordResetDemandRequestModel)
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void confirmPasswordReset(UserPasswordRequestModel userPasswordRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.USERS_RECLAIM)
                .requestBody(userPasswordRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public LoginResponseDataModel renewToken() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.USERS_RENEW)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, LoginResponseDataModel.class);
    }
}
