package hu.psprog.leaflet.bridge.integration.client;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactoryProvider;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.BridgeClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

/**
 * Factory to properly build BridgeClient instances.
 *
 * @author Peter Smith
 */
@Component
public class BridgeClientFactory {

    private final Client client;
    private final InvocationFactoryProvider invocationFactoryProvider;
    private final ResponseReader responseReader;

    @Autowired
    public BridgeClientFactory(Client client, InvocationFactoryProvider invocationFactoryProvider, ResponseReader responseReader) {
        this.client = client;
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

        return new BridgeClientImpl(createWebTarget(bridgeSettings.getHostUrl()), invocationFactory, responseReader);
    }

    private WebTarget createWebTarget(String remoteServiceHost) {
        return client.target(remoteServiceHost);
    }
}
