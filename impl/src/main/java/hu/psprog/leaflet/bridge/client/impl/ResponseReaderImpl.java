package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;
import hu.psprog.leaflet.bridge.client.exception.ConflictingRequestException;
import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ForbiddenOperationException;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ProtocolException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;

/**
 * Implementation of {@link ResponseReader}.
 *
 * @author Peter Smith
 */
public class ResponseReaderImpl implements ResponseReader {

    private final RequestAdapter requestAdapter;
    private final JsonMapper jsonMapper;

    public ResponseReaderImpl(RequestAdapter requestAdapter, JsonMapper jsonMapper) {
        this.requestAdapter = requestAdapter;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public <T> T read(ClassicHttpResponse response, TypeReference<T> responseType) {

        try (response) {
            checkResponse(response);
            extractToken(response);

            return jsonMapper.readValue(response.getEntity().getContent(), responseType);

        } catch (DefaultNonSuccessfulResponseException | ValidationFailureException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RequestProcessingFailureException(exception);
        }
    }

    @Override
    public void read(ClassicHttpResponse response) {

        try (response) {
            checkResponse(response);
            extractToken(response);

        } catch (DefaultNonSuccessfulResponseException | ValidationFailureException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RequestProcessingFailureException(exception);
        }
    }

    private void checkResponse(ClassicHttpResponse response) {

        if (response.getCode() >= HttpStatus.SC_REDIRECTION) {
            raiseException(response);
        }
    }

    private void raiseException(ClassicHttpResponse response) {

        if (response.getCode() == HttpStatus.SC_BAD_REQUEST) {
            ValidationErrorMessageListResponse errorResponse = readErrorResponse(response, ValidationErrorMessageListResponse.class);
            throw new ValidationFailureException(errorResponse);

        } else {
            ErrorMessageResponse errorResponse = readErrorResponse(response, ErrorMessageResponse.class);
            throw getExceptionFunction(response).apply(errorResponse, response.getCode());
        }
    }

    private <T> T readErrorResponse(ClassicHttpResponse response, Class<T> responseType) {

        return Optional.ofNullable(response.getEntity())
                .filter(entity -> entity.getContentLength() > 0L)
                .map(httpEntity -> {
                    try {
                        return httpEntity.getContent();
                    } catch (IOException exception) {
                        throw new RequestProcessingFailureException(exception);
                    }
                })
                .map(inputStream -> jsonMapper.readValue(inputStream, responseType))
                .orElse(null);
    }

    private BiFunction<ErrorMessageResponse, Integer, DefaultNonSuccessfulResponseException> getExceptionFunction(ClassicHttpResponse response) {

        return switch (response.getCode()) {
            case HttpStatus.SC_UNAUTHORIZED -> UnauthorizedAccessException::new;
            case HttpStatus.SC_FORBIDDEN -> ForbiddenOperationException::new;
            case HttpStatus.SC_NOT_FOUND -> ResourceNotFoundException::new;
            case HttpStatus.SC_CONFLICT -> ConflictingRequestException::new;
            default -> RequestProcessingFailureException::new;
        };
    }

    private void extractToken(ClassicHttpResponse response) throws ProtocolException {

        Header tokenHeader = response.getHeader(AUTH_TOKEN_HEADER);
        if (Objects.nonNull(tokenHeader)) {
            requestAdapter.consumeAuthenticationToken(tokenHeader.getValue());
        }
    }
}
