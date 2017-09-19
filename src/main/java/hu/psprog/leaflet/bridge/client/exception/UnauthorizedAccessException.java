package hu.psprog.leaflet.bridge.client.exception;

import javax.ws.rs.core.Response;

/**
 * Exception to throw for HTTP 401 response.
 *
 * @author Peter Smith
 */
public class UnauthorizedAccessException extends DefaultNonSuccessfulResponseException {

    public UnauthorizedAccessException(Response response) {
        super(response);
    }
}
