package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

/**
 * Exception to throw on any other processing failure.
 *
 * @author Peter Smith
 */
public class RequestProcessingFailureException extends DefaultNonSuccessfulResponseException {

    public RequestProcessingFailureException(ErrorMessageResponse response, int status) {
        super(response, status);
    }

    public RequestProcessingFailureException(Throwable cause) {
        super(cause);
    }
}
