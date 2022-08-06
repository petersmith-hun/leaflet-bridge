package hu.psprog.leaflet.bridge.oauth.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for {@link DelegatingOAuth2AuthorizedClientManager}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class DelegatingOAuth2AuthorizedClientManagerTest {

    private static final OAuth2AuthorizeRequest AUTHORIZE_REQUEST = prepareAuthorizeRequest();

    @Mock
    private OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private RequestAttributes requestAttributes;

    private DelegatingOAuth2AuthorizedClientManager delegatingOAuth2AuthorizedClientManager;

    @BeforeEach
    public void setup() {
        delegatingOAuth2AuthorizedClientManager = new DelegatingOAuth2AuthorizedClientManager(defaultOAuth2AuthorizedClientManager,
                authorizedClientServiceOAuth2AuthorizedClientManager);
    }

    @Test
    public void shouldAuthorizeWithDefaultClientManagerForWebContext() {

        // given
        given(defaultOAuth2AuthorizedClientManager.authorize(AUTHORIZE_REQUEST)).willReturn(authorizedClient);

        OAuth2AuthorizedClient result;
        try (MockedStatic<RequestContextHolder> requestContextHolder = mockStatic(RequestContextHolder.class)) {

            requestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);

            // when
            result = delegatingOAuth2AuthorizedClientManager.authorize(AUTHORIZE_REQUEST);
        }

        // then
        assertThat(result, equalTo(authorizedClient));
    }

    @Test
    public void shouldAuthorizeWithAuthorizedClientServiceClientManagerForNonWebContext() {

        // given
        given(authorizedClientServiceOAuth2AuthorizedClientManager.authorize(AUTHORIZE_REQUEST)).willReturn(authorizedClient);

        // when
        OAuth2AuthorizedClient result = delegatingOAuth2AuthorizedClientManager.authorize(AUTHORIZE_REQUEST);

        // then
        assertThat(result, equalTo(authorizedClient));
    }

    private static OAuth2AuthorizeRequest prepareAuthorizeRequest() {

        return OAuth2AuthorizeRequest
                .withClientRegistrationId("client1")
                .principal(new UsernamePasswordAuthenticationToken("user1", "pass1"))
                .build();
    }
}
