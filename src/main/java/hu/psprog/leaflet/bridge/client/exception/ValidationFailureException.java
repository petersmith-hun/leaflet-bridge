package hu.psprog.leaflet.bridge.client.exception;

/**
 * Exception to throw when Leaflet application returns with validation failure (HTTP 400 Bad Request).
 *
 * @author Peter Smith
 */
public class ValidationFailureException extends RuntimeException {

    public ValidationFailureException(String message) {
        super(message);
    }
}
