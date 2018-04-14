package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Exception to throw when a non-200 response is received.
 *
 * @author Peter Smith
 */
public abstract class DefaultNonSuccessfulResponseException extends RuntimeException {

    private static final String UNKNOWN_ERROR_OCCURRED = "Unknown error occurred.";

    private int status;

    DefaultNonSuccessfulResponseException(Response response) {
        super(readErrorResponse(response));
        this.status = response.getStatus();
    }

    public int getStatus() {
        return status;
    }

    private static String readErrorResponse(Response response) {
        return Optional.ofNullable(response.readEntity(ErrorMessageResponse.class))
                .map(ErrorMessageResponse::getMessage)
                .orElse(UNKNOWN_ERROR_OCCURRED);
    }
}
