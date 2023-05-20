package hu.psprog.leaflet.bridge.client.domain.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Validation error message list response model.
 *
 * @author Peter Smith
 */
@Builder(setterPrefix = "with", builderMethodName = "getBuilder")
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidationErrorMessageResponse(
        String message,
        String field
) implements Serializable { }
