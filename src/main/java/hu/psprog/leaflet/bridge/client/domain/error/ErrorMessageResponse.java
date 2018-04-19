package hu.psprog.leaflet.bridge.client.domain.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Standard error message response model.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = ErrorMessageResponse.ErrorMessageResponseBuilder.class)
public class ErrorMessageResponse {

    protected String message;

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ErrorMessageResponse that = (ErrorMessageResponse) o;

        return new EqualsBuilder()
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(message)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("message", message)
                .toString();
    }

    public static ErrorMessageResponseBuilder getBuilder() {
        return new ErrorMessageResponseBuilder();
    }

    /**
     * Builder for {@link ErrorMessageResponse}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ErrorMessageResponseBuilder {
        private String message;

        private ErrorMessageResponseBuilder() {
        }

        public ErrorMessageResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorMessageResponse build() {
            ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse();
            errorMessageResponse.message = this.message;
            return errorMessageResponse;
        }
    }
}
