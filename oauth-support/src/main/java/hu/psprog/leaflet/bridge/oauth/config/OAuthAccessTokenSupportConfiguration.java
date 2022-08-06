package hu.psprog.leaflet.bridge.oauth.config;

import hu.psprog.leaflet.bridge.oauth.support.DelegatingOAuth2AuthorizedClientManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * Context configuration class for Bridge OAuth Support extension.
 *
 * @author Peter Smith
 */
@Configuration
@ComponentScan("hu.psprog.leaflet.bridge.oauth")
public class OAuthAccessTokenSupportConfiguration {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                                              OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {

        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
    }

    @Bean
    public OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                       OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {

        return new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository);
    }

    @Bean
    @Primary
    public DelegatingOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(OAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager,
                                                                                 OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager) {

        return new DelegatingOAuth2AuthorizedClientManager(defaultOAuth2AuthorizedClientManager, authorizedClientServiceOAuth2AuthorizedClientManager);
    }
}
