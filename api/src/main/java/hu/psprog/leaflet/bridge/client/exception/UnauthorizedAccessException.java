package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

/**
 * Exception to throw for HTTP 401 response.
 *
 * @author Peter Smith
 */
public class UnauthorizedAccessException extends DefaultNonSuccessfulResponseException {

    public UnauthorizedAccessException(ErrorMessageResponse response, int status) {
        super(response, status);
    }
}
