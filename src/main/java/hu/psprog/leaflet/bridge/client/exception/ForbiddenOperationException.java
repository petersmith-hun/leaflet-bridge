package hu.psprog.leaflet.bridge.client.exception;

import javax.ws.rs.core.Response;

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
