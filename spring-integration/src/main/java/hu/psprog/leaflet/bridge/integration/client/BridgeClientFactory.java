package hu.psprog.leaflet.bridge.integration.client;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactoryProvider;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.BridgeClientImpl;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Factory to properly build BridgeClient instances.
 *
 * @author Peter Smith
 */
@Component
public class BridgeClientFactory {

    private final HttpClient bridgeHttpClient;
    private final InvocationFactoryProvider invocationFactoryProvider;
    private final ResponseReader responseReader;

    @Autowired
    public BridgeClientFactory(HttpClient bridgeHttpClient, InvocationFactoryProvider invocationFactoryProvider, ResponseReader responseReader) {
        this.bridgeHttpClient = bridgeHttpClient;
        this.invocationFactoryProvider = invocationFactoryProvider;
        this.responseReader = responseReader;
    }

    /**
     * Creates a BridgeClient instance based on given settings.
     *
     * @param bridgeSettings settings to use for the instance
     * @return BridgeClient instance
     */
    public BridgeClient createBridgeClient(BridgeSettings bridgeSettings) {

        Assert.hasLength(bridgeSettings.getHostUrl(), "Remote service host must be specified!");
        InvocationFactory invocationFactory = invocationFactoryProvider.getInvocationFactory(bridgeSettings);

        return new BridgeClientImpl(bridgeHttpClient, bridgeSettings, invocationFactory, responseReader);
    }
}
