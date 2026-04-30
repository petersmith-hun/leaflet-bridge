package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;
import lombok.Getter;
import org.apache.hc.core5.http.HttpStatus;

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

    DefaultNonSuccessfulResponseException(ErrorMessageResponse response, int status) {
        super(readErrorResponse(response));
        this.status = status;
    }

    DefaultNonSuccessfulResponseException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
        this.status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    private static String readErrorResponse(ErrorMessageResponse response) {

        return Optional.ofNullable(response)
                .map(ErrorMessageResponse::message)
                .orElse(UNKNOWN_ERROR_OCCURRED);
    }
}
