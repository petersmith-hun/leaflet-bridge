package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;

/**
 * Exception to throw when Leaflet application returns with validation failure (HTTP 400 Bad Request).
 *
 * @author Peter Smith
 */
public class ValidationFailureException extends RuntimeException {

    private static final String VALIDATION_FAILURE = "Validation failure.";

    private final ValidationErrorMessageListResponse errorMessageList;

    public ValidationFailureException(ValidationErrorMessageListResponse response) {
        super(VALIDATION_FAILURE);
        this.errorMessageList = response;
    }

    public ValidationErrorMessageListResponse getErrorMessage() {
        return errorMessageList;
    }
}
