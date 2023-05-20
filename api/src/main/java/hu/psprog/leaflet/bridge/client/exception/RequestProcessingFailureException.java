package hu.psprog.leaflet.bridge.client.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception to throw on any other processing failure.
 *
 * @author Peter Smith
 */
public class RequestProcessingFailureException extends DefaultNonSuccessfulResponseException {

    public RequestProcessingFailureException(Response response) {
        super(response);
    }
}
