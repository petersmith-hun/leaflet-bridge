package hu.psprog.leaflet.bridge.client.domain.error;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Validation error message response model.
 *
 * @author Peter Smith
 */
@Builder(setterPrefix = "with", builderMethodName = "getBuilder")
@Jacksonized
public record ValidationErrorMessageListResponse(
        List<ValidationErrorMessageResponse> validation
) implements Serializable { }
