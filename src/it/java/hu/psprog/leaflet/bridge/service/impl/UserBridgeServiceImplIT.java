package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserInitializeRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link UserBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class UserBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private UserBridgeService userBridgeService;

    @Test
    public void shouldGetAllUsers() throws CommunicationFailureException {

        // given
        UserListDataModel userListDataModel = prepareUserListDataModel();
        givenThat(get(Path.USERS.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(userListDataModel)));

        // when
        UserListDataModel result = userBridgeService.getAllUsers();

        // then
        assertThat(result, equalTo(userListDataModel));
        verify(getRequestedFor(urlEqualTo(Path.USERS.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateUser() throws JsonProcessingException, CommunicationFailureException {

        // given
        UserCreateRequestModel userCreateRequestModel = prepareUserCreateRequestModel();
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(userCreateRequestModel));
        givenThat(post(Path.USERS.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.createUser(userCreateRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(postRequestedFor(urlEqualTo(Path.USERS.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldInitializeUserDatabase() throws JsonProcessingException, CommunicationFailureException {

        // given
        UserInitializeRequestModel userInitializeRequestModel = prepareUserInitializeRequestModel();
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(userInitializeRequestModel));
        givenThat(post(Path.USERS_INIT.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.initUserDatabase(userInitializeRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(postRequestedFor(urlEqualTo(Path.USERS_INIT.getURI()))
                .withRequestBody(requestBody));
    }

    @Test
    public void shouldGetUserByID() throws CommunicationFailureException {

        // given
        Long userID = 1L;
        String uri = prepareURI(Path.USERS_ID.getURI(), userID);
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(userID);
        givenThat(get(uri)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.getUserByID(userID);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(getRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteUser() throws CommunicationFailureException {

        // given
        Long userID = 1L;
        String uri = prepareURI(Path.USERS_ID.getURI(), userID);
        givenThat(delete(uri));

        // when
        userBridgeService.deleteUser(userID);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateRole() throws JsonProcessingException, CommunicationFailureException {

        // given
        Long userID = 1L;
        String uri = prepareURI(Path.USERS_ROLE.getURI(), userID);
        UpdateRoleRequestModel updateRoleRequestModel = prepareUpdateRoleRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(updateRoleRequestModel));
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.updateRole(userID, updateRoleRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateProfile() throws JsonProcessingException, CommunicationFailureException {

        // given
        Long userID = 1L;
        String uri = prepareURI(Path.USERS_PROFILE.getURI(), userID);
        UpdateProfileRequestModel updateProfileRequestModel = prepareUpdateProfileRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(updateProfileRequestModel));
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.updateProfile(userID, updateProfileRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdatePassword() throws JsonProcessingException, CommunicationFailureException {

        // given
        Long userID = 1L;
        String uri = prepareURI(Path.USERS_PASSWORD.getURI(), userID);
        UserPasswordRequestModel userPasswordRequestModel = prepareUserPasswordRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(userPasswordRequestModel));
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        givenThat(put(uri)
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.updatePassword(userID, userPasswordRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(putRequestedFor(urlEqualTo(uri))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldClaimToken() throws JsonProcessingException, CommunicationFailureException {

        // given
        LoginRequestModel loginRequestModel = prepareLoginRequestModel();
        LoginResponseDataModel loginResponseDataModel = prepareLoginResponseDataModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(loginRequestModel));
        givenThat(post(Path.USERS_CLAIM.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(loginResponseDataModel)));

        // when
        LoginResponseDataModel result = userBridgeService.claimToken(loginRequestModel);

        // then
        assertThat(result, equalTo(loginResponseDataModel));
        verify(postRequestedFor(urlEqualTo(Path.USERS_CLAIM.getURI()))
                .withRequestBody(requestBody));
    }

    @Test
    public void shouldSignUp() throws JsonProcessingException, CommunicationFailureException {

        // given
        UserInitializeRequestModel userInitializeRequestModel = prepareUserInitializeRequestModel();
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel(1L);
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(userInitializeRequestModel));
        givenThat(post(Path.USERS_REGISTER.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(extendedUserDataModel)));

        // when
        ExtendedUserDataModel result = userBridgeService.signUp(userInitializeRequestModel);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
        verify(postRequestedFor(urlEqualTo(Path.USERS_REGISTER.getURI()))
                .withRequestBody(requestBody));
    }

    @Test
    public void shouldRevokeToken() throws CommunicationFailureException {

        // given
        givenThat(post(Path.USERS_REVOKE.getURI())
                .willReturn(aResponse().withStatus(204)));

        // when
        userBridgeService.revokeToken();

        // then
        verify(postRequestedFor(urlEqualTo(Path.USERS_REVOKE.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDemandPasswordReset() throws JsonProcessingException, CommunicationFailureException {

        // given
        PasswordResetDemandRequestModel passwordResetDemandRequestModel = preparePasswordResetDemandRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(passwordResetDemandRequestModel));
        givenThat(post(Path.USERS_RECLAIM.getURI())
                .withRequestBody(requestBody)
                .willReturn(aResponse().withStatus(201)));

        // when
        userBridgeService.demandPasswordReset(passwordResetDemandRequestModel);

        // then
        verify(postRequestedFor(urlEqualTo(Path.USERS_RECLAIM.getURI()))
                .withRequestBody(requestBody));
    }

    @Test
    public void shouldConfirmPasswordReset() throws JsonProcessingException, CommunicationFailureException {

        // given
        UserPasswordRequestModel userPasswordRequestModel = prepareUserPasswordRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(userPasswordRequestModel));
        givenThat(put(Path.USERS_RECLAIM.getURI())
                .withRequestBody(requestBody)
                .willReturn(aResponse().withStatus(201)));

        // when
        userBridgeService.confirmPasswordReset(userPasswordRequestModel);

        // then
        verify(putRequestedFor(urlEqualTo(Path.USERS_RECLAIM.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldRenewToken() throws JsonProcessingException, CommunicationFailureException {

        // given
        LoginResponseDataModel loginResponseDataModel = prepareLoginResponseDataModel();
        givenThat(put(Path.USERS_RENEW.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(loginResponseDataModel)));

        // when
        LoginResponseDataModel result = userBridgeService.renewToken();

        // then
        assertThat(result, equalTo(loginResponseDataModel));
        verify(putRequestedFor(urlEqualTo(Path.USERS_RENEW.getURI())));
    }

    private LoginRequestModel prepareLoginRequestModel() {
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail("user@it.dev");
        loginRequestModel.setPassword("password");
        return loginRequestModel;
    }

    private LoginResponseDataModel prepareLoginResponseDataModel() {
        return LoginResponseDataModel.getBuilder()
                .withStatus(LoginResponseDataModel.AuthenticationResult.AUTH_SUCCESS)
                .withToken("token")
                .build();
    }

    private UpdateRoleRequestModel prepareUpdateRoleRequestModel() {
        UpdateRoleRequestModel updateRoleRequestModel = new UpdateRoleRequestModel();
        updateRoleRequestModel.setRole("ADMIN");
        return updateRoleRequestModel;
    }

    private UpdateProfileRequestModel prepareUpdateProfileRequestModel() {
        UpdateProfileRequestModel updateProfileRequestModel = new UpdateProfileRequestModel();
        updateProfileRequestModel.setEmail("new_email@it.dev");
        updateProfileRequestModel.setDefaultLocale(Locale.ENGLISH);
        updateProfileRequestModel.setUsername("new username");
        return updateProfileRequestModel;
    }

    private UserPasswordRequestModel prepareUserPasswordRequestModel() {
        UserPasswordRequestModel userPasswordRequestModel = new UserPasswordRequestModel();
        userPasswordRequestModel.setPassword("new password");
        userPasswordRequestModel.setPasswordConfirmation("new password");
        return userPasswordRequestModel;
    }

    private UserInitializeRequestModel prepareUserInitializeRequestModel() {
        UserInitializeRequestModel userInitializeRequestModel = new UserInitializeRequestModel();
        userInitializeRequestModel.setEmail("testuser@it.dev");
        userInitializeRequestModel.setUsername("testuser");
        return userInitializeRequestModel;
    }

    private UserCreateRequestModel prepareUserCreateRequestModel() {
        UserCreateRequestModel userCreateRequestModel = new UserCreateRequestModel();
        userCreateRequestModel.setEmail("testuser@it.dev");
        userCreateRequestModel.setUsername("testuser");
        userCreateRequestModel.setRole("USER");
        return userCreateRequestModel;
    }

    private UserListDataModel prepareUserListDataModel() {
        return UserListDataModel.getBuilder()
                .withItem(prepareUserDataModel(1L))
                .withItem(prepareUserDataModel(2L))
                .build();
    }

    private ExtendedUserDataModel prepareExtendedUserDataModel(Long userID) {
        return ExtendedUserDataModel.getExtendedBuilder()
                .withId(userID)
                .withUsername("User #" + userID)
                .withEmail("user" + userID + "@it.dev")
                .build();
    }

    private UserDataModel prepareUserDataModel(Long userID) {
        return UserDataModel.getBuilder()
                .withId(userID)
                .withUsername("User #" + userID)
                .build();
    }

    private PasswordResetDemandRequestModel preparePasswordResetDemandRequestModel() {
        PasswordResetDemandRequestModel passwordResetDemandRequestModel = new PasswordResetDemandRequestModel();
        passwordResetDemandRequestModel.setEmail("user1@it.dev");
        return passwordResetDemandRequestModel;
    }
}
