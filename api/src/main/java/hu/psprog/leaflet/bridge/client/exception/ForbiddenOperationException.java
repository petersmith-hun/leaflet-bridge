package hu.psprog.leaflet.bridge.client.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception to throw for HTTP 403 response.
 *
 * @author Peter Smith
 */
public class ForbiddenOperationException extends DefaultNonSuccessfulResponseException {

    public ForbiddenOperationException(Response response) {
        super(response);
    }
}
