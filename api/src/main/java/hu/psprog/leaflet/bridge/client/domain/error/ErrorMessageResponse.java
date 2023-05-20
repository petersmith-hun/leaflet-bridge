package hu.psprog.leaflet.bridge.client.domain.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Standard error message response model.
 *
 * @author Peter Smith
 */
@Builder(setterPrefix = "with", builderMethodName = "getBuilder")
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorMessageResponse(
        String message
) implements Serializable { }
