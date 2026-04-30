package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

/**
 * Exception to throw for HTTP 409 response.
 *
 * @author Peter Smith
 */
public class ConflictingRequestException extends DefaultNonSuccessfulResponseException {

    public ConflictingRequestException(ErrorMessageResponse response, int status) {
        super(response, status);
    }
}
