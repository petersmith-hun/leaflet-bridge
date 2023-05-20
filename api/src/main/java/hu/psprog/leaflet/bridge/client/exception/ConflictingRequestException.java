package hu.psprog.leaflet.bridge.client.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception to throw for HTTP 409 response.
 *
 * @author Peter Smith
 */
public class ConflictingRequestException extends DefaultNonSuccessfulResponseException {

    public ConflictingRequestException(Response response) {
        super(response);
    }
}
