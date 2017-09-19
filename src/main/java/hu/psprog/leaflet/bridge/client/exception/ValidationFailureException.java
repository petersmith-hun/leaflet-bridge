package hu.psprog.leaflet.bridge.client.exception;

import hu.psprog.leaflet.api.rest.response.common.ValidationErrorMessageListDataModel;

import javax.ws.rs.core.Response;

/**
 * Exception to throw when Leaflet application returns with validation failure (HTTP 400 Bad Request).
 *
 * @author Peter Smith
 */
public class ValidationFailureException extends RuntimeException {

    private static final String VALIDATION_FAILURE = "Validation failure.";

    private ValidationErrorMessageListDataModel errorMessageList;

    public ValidationFailureException(Response response) {
        super(VALIDATION_FAILURE);
        this.errorMessageList = readErrorResponse(response);
    }

    public ValidationErrorMessageListDataModel getErrorMessage() {
        return errorMessageList;
    }

    private ValidationErrorMessageListDataModel readErrorResponse(Response response) {
        return response.readEntity(ValidationErrorMessageListDataModel.class);
    }
}
