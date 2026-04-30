package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

/**
 * Exception to throw when Leaflet returns with HTTP 404.
 *
 * @author Peter Smith
 */
public class ResourceNotFoundException extends DefaultNonSuccessfulResponseException {

    public ResourceNotFoundException(ErrorMessageResponse response, int status) {
        super(response, status);
    }
}
