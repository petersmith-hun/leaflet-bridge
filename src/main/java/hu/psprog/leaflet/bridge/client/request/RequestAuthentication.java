package hu.psprog.leaflet.bridge.client.request;

import java.util.Map;

/**
 * Interface to retrieve frontend application's own authentication object.
 * Should be implemented by frontend application.
 *
 * @author Peter Smith
 */
public interface RequestAuthentication {

    /**
     * Returns authentication header field(s).
     *
     * @return authentication header field(s) as header key - value pairs
     */
    Map<String, String> getAuthenticationHeader();
}
