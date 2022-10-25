package hu.psprog.leaflet.bridge.integration.request.adapter;

import hu.psprog.leaflet.bridge.client.request.RequestAdapter;

import java.util.UUID;

/**
 * {@link RequestAdapter} implementation for services not using Leaflet-Link identification.
 * The client ID will always the registration ID of the Bridge client, and the device ID is a random UUID generated
 * on instantiation. Access token consumption is not supported.
 *
 * @author Peter Smith
 */
public class StaticRequestAdapter implements RequestAdapter {

    private final String deviceID;
    private final String clientID;

    public StaticRequestAdapter(String clientID) {
        this.deviceID = UUID.randomUUID().toString();
        this.clientID = clientID;
    }

    @Override
    public String provideDeviceID() {
        return deviceID;
    }

    @Override
    public String provideClientID() {
        return clientID;
    }

    @Override
    public void consumeAuthenticationToken(String token) {
        throw new UnsupportedOperationException("Static request adapter implementation does not support consuming the auth token");
    }
}
