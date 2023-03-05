package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Factory implementation creating {@link SpringIntegratedOAuthRequestAuthentication} objects for OAuth-enabled Bridge clients.
 *
 * @author Peter Smith
 */
@Component
public class OAuthRequestAuthenticationFactory {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager;
    private final OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    @Autowired
    public OAuthRequestAuthenticationFactory(ClientRegistrationRepository clientRegistrationRepository,
                                             OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager,
                                             OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.defaultOAuth2AuthorizedClientManager = defaultOAuth2AuthorizedClientManager;
        this.authorizedClientServiceOAuth2AuthorizedClientManager = authorizedClientServiceOAuth2AuthorizedClientManager;
    }

    /**
     * Creates a {@link SpringIntegratedOAuthRequestAuthentication} object according to the OAuth client registration
     * backing the specified Bridge client. The implementation does two major steps:
     *  - Assigns an {@link OAuth2AuthorizedClientManager} implementation. This is going to be either the "default"
     *    implementation in case of an Authorization Code client, or the "authorized client service" based implementation
     *    for Client Credentials clients.
     *  - Sets up how the principal can be acquired for the client. For Authorization Code clients, the authentication
     *    object assigned to the user is going to be used in the authorization request (retrieved from the Security Context),
     *    otherwise (for Client Credentials clients) the registration ID itself is going to be used as a principal.
     *
     * @param bridgeSettings {@link BridgeSettings} object containing the Bridge client configuration
     * @return initialized {@link RequestAuthentication} object
     */
    public RequestAuthentication createRequestAuthentication(BridgeSettings bridgeSettings) {

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(bridgeSettings.getOAuthRegistrationID());
        Consumer<OAuth2AuthorizeRequest.Builder> principalConsumer = getPrincipalConsumer(clientRegistration);
        OAuth2AuthorizedClientManager clientManager = getClientManager(clientRegistration);

        return new SpringIntegratedOAuthRequestAuthentication(clientRegistration, principalConsumer, clientManager);
    }

    private Consumer<OAuth2AuthorizeRequest.Builder> getPrincipalConsumer(ClientRegistration clientRegistration) {

        return isClientCredentialsRegistration(clientRegistration)
                ? builder -> builder.principal(clientRegistration.getRegistrationId())
                : builder -> builder.principal(SecurityContextHolder.getContext().getAuthentication());
    }

    private OAuth2AuthorizedClientManager getClientManager(ClientRegistration clientRegistration) {

        return isClientCredentialsRegistration(clientRegistration)
                ? authorizedClientServiceOAuth2AuthorizedClientManager
                : defaultOAuth2AuthorizedClientManager;
    }

    private boolean isClientCredentialsRegistration(ClientRegistration clientRegistration) {
        return AuthorizationGrantType.CLIENT_CREDENTIALS.equals(clientRegistration.getAuthorizationGrantType());
    }
}
