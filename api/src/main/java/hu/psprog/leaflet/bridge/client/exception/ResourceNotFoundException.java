package hu.psprog.leaflet.bridge.client.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception to throw when Leaflet returns with HTTP 404.
 *
 * @author Peter Smith
 */
public class ResourceNotFoundException extends DefaultNonSuccessfulResponseException {

    public ResourceNotFoundException(Response response) {
        super(response);
    }
}
