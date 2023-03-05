package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link OAuthRequestAuthenticationFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class OAuthRequestAuthenticationFactoryTest {

    private static final String REGISTRATION_ID = "client-1";
    private static final BridgeSettings BRIDGE_SETTINGS = prepareBridgeSettings();

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    @Mock
    private Authentication authentication;

    private OAuthRequestAuthenticationFactory oAuthRequestAuthenticationFactory;

    @BeforeEach
    public void setup() {
        oAuthRequestAuthenticationFactory = new OAuthRequestAuthenticationFactory(clientRegistrationRepository,
                defaultOAuth2AuthorizedClientManager, authorizedClientServiceOAuth2AuthorizedClientManager);
    }

    @Test
    public void shouldCreateRequestAuthenticationPrepareClientForClientCredentialsFlow() throws IllegalAccessException {

        // given
        ClientRegistration clientRegistration = prepareClientRegistration(AuthorizationGrantType.CLIENT_CREDENTIALS);
        given(clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID)).willReturn(clientRegistration);

        // when
        RequestAuthentication result = oAuthRequestAuthenticationFactory.createRequestAuthentication(BRIDGE_SETTINGS);

        // then
        assertThat(result, notNullValue());
        assertRegistrationID(result);
        assertClientManager(result, authorizedClientServiceOAuth2AuthorizedClientManager);
        assertPrincipalConsumer(result, REGISTRATION_ID);
    }

    @Test
    public void shouldCreateRequestAuthenticationPrepareClientForAuthorizationCodeFlow() throws IllegalAccessException {

        // given
        String expectedPrincipal = "authorized-user";
        ClientRegistration clientRegistration = prepareClientRegistration(AuthorizationGrantType.AUTHORIZATION_CODE);
        given(clientRegistrationRepository.findByRegistrationId(REGISTRATION_ID)).willReturn(clientRegistration);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(authentication.getPrincipal()).willReturn(expectedPrincipal);

        // when
        RequestAuthentication result = oAuthRequestAuthenticationFactory.createRequestAuthentication(BRIDGE_SETTINGS);

        // then
        assertThat(result, notNullValue());
        assertRegistrationID(result);
        assertClientManager(result, defaultOAuth2AuthorizedClientManager);
        assertPrincipalConsumer(result, expectedPrincipal);
        SecurityContextHolder.clearContext();
    }

    private void assertRegistrationID(RequestAuthentication requestAuthentication) throws IllegalAccessException {

        ClientRegistration clientRegistration = extractFieldValue(requestAuthentication, "clientRegistration");
        assertThat(clientRegistration.getRegistrationId(), equalTo(REGISTRATION_ID));
    }

    private void assertClientManager(RequestAuthentication requestAuthentication, OAuth2AuthorizedClientManager expectedClientManager) throws IllegalAccessException {

        OAuth2AuthorizedClientManager attachedClientManager = extractFieldValue(requestAuthentication, "oAuth2AuthorizedClientManager");
        assertThat(attachedClientManager, equalTo(expectedClientManager));
    }

    private void assertPrincipalConsumer(RequestAuthentication requestAuthentication, Object expectedPrincipal) throws IllegalAccessException {

        OAuth2AuthorizeRequest.Builder builder = OAuth2AuthorizeRequest.withClientRegistrationId(REGISTRATION_ID);

        Consumer<OAuth2AuthorizeRequest.Builder> principalConsumer = extractFieldValue(requestAuthentication, "principalConsumer");
        principalConsumer.accept(builder);
        OAuth2AuthorizeRequest request = builder.build();

        assertThat(request.getPrincipal().getPrincipal(), equalTo(expectedPrincipal));
    }

    private <T> T extractFieldValue(Object targetObject, String fieldName) throws IllegalAccessException {

        Field field = ReflectionUtils.findField(targetObject.getClass(), fieldName);
        Objects.requireNonNull(field).setAccessible(true);

        return (T) field.get(targetObject);
    }

    private static BridgeSettings prepareBridgeSettings() {

        return BridgeSettings.getBuilder()
                .withOAuthRegistrationID(REGISTRATION_ID)
                .build();
    }

    private static ClientRegistration prepareClientRegistration(AuthorizationGrantType grantType) {

        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(REGISTRATION_ID)
                .tokenUri("http://localhost:9999/token")
                .authorizationUri("http://localhost:9999/authorize")
                .redirectUri("http://localhost:8888/redirect")
                .authorizationGrantType(grantType)
                .build();
    }
}
