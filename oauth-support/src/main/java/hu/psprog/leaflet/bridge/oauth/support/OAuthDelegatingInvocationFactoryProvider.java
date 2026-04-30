package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactoryProvider;
import hu.psprog.leaflet.bridge.client.impl.InvocationFactoryImpl;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.integration.request.adapter.StaticRequestAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;

/**
 * {@link InvocationFactoryProvider} implementation for creating OAuth compatible {@link InvocationFactory} instances for Bridge clients.
 * <p>
 * To enable creating an OAuth {@link InvocationFactory}, set the Spring OAuth registration ID in the Bridge client's configuration.
 * This way, instead of the default {@link InvocationFactory} instance, a separate instance will be created, using the
 * {@link SpringIntegratedOAuthRequestAuthentication} implementation of {@link RequestAuthentication} interface.
 *
 * @author Peter Smith
 */
@Primary
@Component
public class OAuthDelegatingInvocationFactoryProvider implements InvocationFactoryProvider {

    private final InvocationFactory defaultInvocationFactory;
    private final RequestAdapter defaultRequestAdapter;
    private final OAuthRequestAuthenticationFactory oAuthRequestAuthenticationFactory;
    private final JsonMapper jsonMapper;

    @Autowired
    public OAuthDelegatingInvocationFactoryProvider(InvocationFactory defaultInvocationFactory, RequestAdapter defaultRequestAdapter,
                                                    OAuthRequestAuthenticationFactory oAuthRequestAuthenticationFactory, JsonMapper jsonMapper) {
        this.defaultInvocationFactory = defaultInvocationFactory;
        this.defaultRequestAdapter = defaultRequestAdapter;
        this.oAuthRequestAuthenticationFactory = oAuthRequestAuthenticationFactory;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public InvocationFactory getInvocationFactory(BridgeSettings bridgeSettings) {

        return isOAuthClient(bridgeSettings)
                ? createInvocationFactory(bridgeSettings)
                : defaultInvocationFactory;
    }

    private boolean isOAuthClient(BridgeSettings bridgeSettings) {
        return Objects.nonNull(bridgeSettings.getOAuthRegistrationID());
    }

    private InvocationFactory createInvocationFactory(BridgeSettings bridgeSettings) {

        RequestAuthentication oAuthRequestAuthentication = oAuthRequestAuthenticationFactory.createRequestAuthentication(bridgeSettings);
        RequestAdapter requestAdapter = getRequestAdapter(bridgeSettings);

        return new InvocationFactoryImpl(oAuthRequestAuthentication, requestAdapter, jsonMapper);
    }

    private RequestAdapter getRequestAdapter(BridgeSettings bridgeSettings) {

        return bridgeSettings.isUseLeafletLink()
                ? defaultRequestAdapter
                : new StaticRequestAdapter(bridgeSettings.getOAuthRegistrationID());
    }
}
