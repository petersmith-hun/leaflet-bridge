package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

/**
 * Exception to throw for HTTP 403 response.
 *
 * @author Peter Smith
 */
public class ForbiddenOperationException extends DefaultNonSuccessfulResponseException {

    public ForbiddenOperationException(ErrorMessageResponse response, int status) {
        super(response, status);
    }
}
