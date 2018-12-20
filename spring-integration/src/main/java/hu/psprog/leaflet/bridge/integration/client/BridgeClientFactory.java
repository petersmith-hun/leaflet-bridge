package hu.psprog.leaflet.bridge.integration.client;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.BridgeClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Factory to properly build BridgeClient instances.
 *
 * @author Peter Smith
 */
@Component
class BridgeClientFactory {

    private Client client;
    private InvocationFactory invocationFactory;
    private ResponseReader responseReader;

    @Autowired
    public BridgeClientFactory(Client client, InvocationFactory invocationFactory, ResponseReader responseReader) {
        this.client = client;
        this.invocationFactory = invocationFactory;
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

        return new BridgeClientImpl(createWebTarget(bridgeSettings.getHostUrl()), invocationFactory, responseReader);
    }

    private WebTarget createWebTarget(String remoteServiceHost) {
        return client.target(remoteServiceHost);
    }
}
