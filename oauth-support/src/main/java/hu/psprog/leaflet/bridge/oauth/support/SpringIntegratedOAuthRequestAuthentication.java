package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * {@link RequestAuthentication} implementation for Bridge clients using Spring's OAuth integration.
 *
 * Instead of using the Security Context to gather the stored access token, this implementation utilizes Spring's
 * {@link OAuth2AuthorizedClientManager}s to request a token when it's necessary, leveraging Spring's standard OAuth integration.
 *
 * @author Peter Smith
 */
public class SpringIntegratedOAuthRequestAuthentication implements RequestAuthentication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringIntegratedOAuthRequestAuthentication.class);

    private static final String HEADER_PARAMETER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer {0}";

    private final String registrationID;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public SpringIntegratedOAuthRequestAuthentication(String registrationID, OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.registrationID = registrationID;
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public Map<String, String> getAuthenticationHeader() {

        OAuth2AuthorizeRequest authorizeRequest = createAuthorizeRequest();
        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);

        return Optional.ofNullable(authorizedClient)
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(AbstractOAuth2Token::getTokenValue)
                .map(this::formatAuthorizationHeader)
                .orElseGet(() -> {
                    LOGGER.warn("Failed to provide access token for registration [{}]", registrationID);
                    return Collections.emptyMap();
                });
    }

    private OAuth2AuthorizeRequest createAuthorizeRequest() {

        return OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationID)
                .principal(SecurityContextHolder.getContext().getAuthentication())
                .build();
    }

    private Map<String, String> formatAuthorizationHeader(String accessToken) {
        return Map.of(HEADER_PARAMETER_AUTHORIZATION, MessageFormat.format(AUTHORIZATION_SCHEMA, accessToken));
    }
}
