package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;

import javax.ws.rs.core.Response;

/**
 * Exception to throw when Leaflet application returns with validation failure (HTTP 400 Bad Request).
 *
 * @author Peter Smith
 */
public class ValidationFailureException extends RuntimeException {

    private static final String VALIDATION_FAILURE = "Validation failure.";

    private ValidationErrorMessageListResponse errorMessageList;

    public ValidationFailureException(Response response) {
        super(VALIDATION_FAILURE);
        this.errorMessageList = readErrorResponse(response);
    }

    public ValidationErrorMessageListResponse getErrorMessage() {
        return errorMessageList;
    }

    private ValidationErrorMessageListResponse readErrorResponse(Response response) {
        return response.readEntity(ValidationErrorMessageListResponse.class);
    }
}
