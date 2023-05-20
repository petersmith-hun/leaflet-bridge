package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

import java.util.Optional;

/**
 * Exception to throw when a non-200 response is received.
 *
 * @author Peter Smith
 */
@Getter
public abstract class DefaultNonSuccessfulResponseException extends RuntimeException {

    private static final String UNKNOWN_ERROR_OCCURRED = "Unknown error occurred.";

    private final int status;

    DefaultNonSuccessfulResponseException(Response response) {
        super(readErrorResponse(response));
        this.status = response.getStatus();
    }

    private static String readErrorResponse(Response response) {
        return Optional.ofNullable(response.readEntity(ErrorMessageResponse.class))
                .map(ErrorMessageResponse::message)
                .orElse(UNKNOWN_ERROR_OCCURRED);
    }
}
