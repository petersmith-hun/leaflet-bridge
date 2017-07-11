package hu.psprog.leaflet.bridge.client.exception;

/**
 * Exception to throw when Leaflet returns with HTTP 404.
 *
 * @author Peter Smith
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
