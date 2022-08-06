package hu.psprog.leaflet.bridge.client.handler;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;

/**
 * Abstract factory interface for creating a proper {@link InvocationFactory} for a specific client.
 *
 * @author Peter Smith
 */
public interface InvocationFactoryProvider {

    /**
     * Creates an {@link InvocationFactory} configured for a specific Bridge client.
     * Configuration of the {@link InvocationFactory} instance is based on the given {@link BridgeSettings}.
     *
     * @param bridgeSettings {@link BridgeSettings} object containing configuration information for the {@link InvocationFactory}
     * @return created {@link InvocationFactory} instance
     */
    InvocationFactory getInvocationFactory(BridgeSettings bridgeSettings);
}
