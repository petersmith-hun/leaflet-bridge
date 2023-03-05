package hu.psprog.leaflet.bridge.oauth.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SpringIntegratedOAuthRequestAuthentication}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class SpringIntegratedOAuthRequestAuthenticationTest {

    private static final String REGISTRATION_ID = "oauth-client-1";
    private static final String ACCESS_TOKEN_VALUE = "jwt-token";
    private static final String ACCESS_TOKEN_BEARER = "Bearer " + ACCESS_TOKEN_VALUE;
    private static final ClientRegistration CLIENT_REGISTRATION = prepareClientRegistration();

    @Mock
    private OAuth2AuthorizedClientManager clientManager;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private OAuth2AccessToken accessToken;

    @Captor
    private ArgumentCaptor<OAuth2AuthorizeRequest> authorizeRequestCaptor;

    @Mock
    private SpringIntegratedOAuthRequestAuthentication springIntegratedOAuthRequestAuthentication;

    @BeforeEach
    public void setup() {

        Consumer<OAuth2AuthorizeRequest.Builder> principalConsumer = builder -> builder.principal(REGISTRATION_ID);

        springIntegratedOAuthRequestAuthentication =
                new SpringIntegratedOAuthRequestAuthentication(CLIENT_REGISTRATION, principalConsumer, clientManager);
    }

    @Test
    public void shouldGetAuthenticationHeaderRequestTokenFromClientManager() {

        // given
        given(clientManager.authorize(any(OAuth2AuthorizeRequest.class))).willReturn(authorizedClient);
        given(authorizedClient.getAccessToken()).willReturn(accessToken);
        given(accessToken.getTokenValue()).willReturn(ACCESS_TOKEN_VALUE);

        // when
        Map<String, String> result = springIntegratedOAuthRequestAuthentication.getAuthenticationHeader();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get("Authorization"), equalTo(ACCESS_TOKEN_BEARER));

        verify(clientManager).authorize(authorizeRequestCaptor.capture());
        OAuth2AuthorizeRequest capturedAuthorizeRequest = authorizeRequestCaptor.getValue();
        assertThat(capturedAuthorizeRequest.getClientRegistrationId(), equalTo(REGISTRATION_ID));
        assertThat(capturedAuthorizeRequest.getPrincipal(), isA(AbstractAuthenticationToken.class));
        assertThat(capturedAuthorizeRequest.getPrincipal().getPrincipal(), equalTo(REGISTRATION_ID));
    }

    @Test
    public void shouldGetAuthenticationHeaderReturnEmptyMapForEmptytoken() {

        // given
        given(clientManager.authorize(any(OAuth2AuthorizeRequest.class))).willReturn(authorizedClient);
        given(authorizedClient.getAccessToken()).willReturn(null);

        // when
        Map<String, String> result = springIntegratedOAuthRequestAuthentication.getAuthenticationHeader();

        // then
        assertThat(result.isEmpty(), is(true));

        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldGetAuthenticationHeaderReturnEmptyMapForUnknownClient() {

        // given
        doThrow(ClientAuthorizationRequiredException.class).when(clientManager).authorize(any(OAuth2AuthorizeRequest.class));

        // when
        Map<String, String> result = springIntegratedOAuthRequestAuthentication.getAuthenticationHeader();

        // then
        assertThat(result.isEmpty(), is(true));

        SecurityContextHolder.clearContext();
    }

    private static ClientRegistration prepareClientRegistration() {

        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(REGISTRATION_ID)
                .tokenUri("http://localhost:9999/token")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
    }
}
