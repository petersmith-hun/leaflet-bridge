package hu.psprog.leaflet.bridge.client.exception;

/**
 * Exception to throw on any other processing failure.
 *
 * @author Peter Smith
 */
public class RequestProcessingFailureException extends RuntimeException {

    public RequestProcessingFailureException(String message) {
        super(message);
    }
}
