package hu.psprog.leaflet.bridge.oauth.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

/**
 * {@link OAuth2AuthorizedClientManager} implementation that is able to delegate client authorization to the proper manager implementation.
 * Decision is based on the existence of HTTP request context:
 *  - If there is an active HTTP request, the client will be authorized using the default client manager of Spring;
 *  - Otherwise, Spring's authorized client service based client manager will be used for client authorization.
 *
 * @author Peter Smith
 */
public class DelegatingOAuth2AuthorizedClientManager implements OAuth2AuthorizedClientManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingOAuth2AuthorizedClientManager.class);

    private final OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager;
    private final OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    public DelegatingOAuth2AuthorizedClientManager(OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager,
                                                   OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager) {
        this.defaultOAuth2AuthorizedClientManager = defaultOAuth2AuthorizedClientManager;
        this.authorizedClientServiceOAuth2AuthorizedClientManager = authorizedClientServiceOAuth2AuthorizedClientManager;
    }

    @Override
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizeRequest authorizeRequest) {

        OAuth2AuthorizedClient authorizedClient = null;
        try {
            authorizedClient = Objects.isNull(RequestContextHolder.getRequestAttributes())
                    ? authorizedClientServiceOAuth2AuthorizedClientManager.authorize(authorizeRequest)
                    : defaultOAuth2AuthorizedClientManager.authorize(authorizeRequest);

        } catch (ClientAuthorizationRequiredException exception) {
            LOGGER.warn("Couldn't retrieve an authorized client for principal {}", authorizeRequest.getPrincipal(), exception);
        }

        return authorizedClient;
    }
}
