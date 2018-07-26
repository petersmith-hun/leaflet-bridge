package hu.psprog.leaflet.bridge.client.request;

/**
 * Request adapter implementation's purpose is to join together the integrating application's
 * own "request" mechanism with Bridge's one. Bridge requires some identifiers and parameters
 * for every sent REST request. These parameters can be originated from multiple different
 * sources, and the integrating application should decide how to pass these parameters.
 *
 * @author Peter Smith
 */
public interface RequestAdapter {

    /**
     * Provides identification token for remote user's device.
     * Unique for every single user, and usually different for every session.
     *
     * @return device identifier
     */
    String provideDeviceID();

    /**
     * Provides identification token for integrating Leaflet client applications.
     * Unique for every distinct client application. Provided token must be registered on Leaflet backend service.
     *
     * @return client identifier
     */
    String provideClientID();

    /**
     * Consumes authentication token returned in a response.
     *
     * @param token retrieved token from response
     */
    void consumeAuthenticationToken(String token);
}
