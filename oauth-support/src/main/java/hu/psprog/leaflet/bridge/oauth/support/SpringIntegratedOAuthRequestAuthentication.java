package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

    private final ClientRegistration clientRegistration;
    private final Consumer<OAuth2AuthorizeRequest.Builder> principalConsumer;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public SpringIntegratedOAuthRequestAuthentication(ClientRegistration clientRegistration, Consumer<OAuth2AuthorizeRequest.Builder> principalConsumer,
                                                      OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.clientRegistration = clientRegistration;
        this.principalConsumer = principalConsumer;
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public Map<String, String> getAuthenticationHeader() {

        OAuth2AuthorizeRequest authorizeRequest = createAuthorizeRequest();
        Optional<OAuth2AuthorizedClient> authorizedClient = getAuthorizedClient(authorizeRequest);

        return authorizedClient
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(AbstractOAuth2Token::getTokenValue)
                .map(this::formatAuthorizationHeader)
                .orElseGet(() -> {
                    LOGGER.warn("Failed to provide access token for registration [{}]", clientRegistration.getRegistrationId());
                    return Collections.emptyMap();
                });
    }

    private OAuth2AuthorizeRequest createAuthorizeRequest() {

        OAuth2AuthorizeRequest.Builder builder = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId());
        principalConsumer.accept(builder);

        return builder.build();
    }

    private Optional<OAuth2AuthorizedClient> getAuthorizedClient(OAuth2AuthorizeRequest authorizeRequest) {

        OAuth2AuthorizedClient authorizedClient = null;
        try {
            authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);
        } catch (ClientAuthorizationRequiredException exception) {
            LOGGER.warn("Couldn't retrieve an authorized client for principal {}", authorizeRequest.getPrincipal(), exception);
        }

        return Optional.ofNullable(authorizedClient);
    }

    private Map<String, String> formatAuthorizationHeader(String accessToken) {
        return Map.of(HEADER_PARAMETER_AUTHORIZATION, MessageFormat.format(AUTHORIZATION_SCHEMA, accessToken));
    }
}
