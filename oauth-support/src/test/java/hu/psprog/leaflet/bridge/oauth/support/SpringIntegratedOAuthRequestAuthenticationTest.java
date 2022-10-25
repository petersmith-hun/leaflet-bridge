package hu.psprog.leaflet.bridge.oauth.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @Mock
    private OAuth2AuthorizedClientManager clientManager;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private OAuth2AccessToken accessToken;

    @Mock
    private RequestAttributes requestAttributes;

    @Captor
    private ArgumentCaptor<OAuth2AuthorizeRequest> authorizeRequestCaptor;

    private SpringIntegratedOAuthRequestAuthentication springIntegratedOAuthRequestAuthentication;

    @BeforeEach
    public void setup() {
        springIntegratedOAuthRequestAuthentication = new SpringIntegratedOAuthRequestAuthentication(REGISTRATION_ID, clientManager);
    }

    @Test
    public void shouldGetAuthenticationHeaderRequestTokenFromClientManagerForWebRequest() {

        // given
        SecurityContextHolder.getContext().setAuthentication(authentication);
        RequestContextHolder.setRequestAttributes(requestAttributes);

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
        assertThat(capturedAuthorizeRequest.getPrincipal(), equalTo(authentication));

        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void shouldGetAuthenticationHeaderRequestTokenFromClientManagerForBackgroundRequest() {

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
    public void shouldGetAuthenticationHeaderReturnEmptyMapForUnknownClient() {

        // given
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(clientManager.authorize(any(OAuth2AuthorizeRequest.class))).willReturn(null);

        // when
        Map<String, String> result = springIntegratedOAuthRequestAuthentication.getAuthenticationHeader();

        // then
        assertThat(result.isEmpty(), is(true));

        SecurityContextHolder.clearContext();
    }
}
