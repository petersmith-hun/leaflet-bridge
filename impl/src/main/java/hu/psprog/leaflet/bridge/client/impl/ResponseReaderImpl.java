package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.exception.ConflictingRequestException;
import hu.psprog.leaflet.bridge.client.exception.ForbiddenOperationException;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.Closeable;
import java.util.Objects;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;

/**
 * Implementation of {@link ResponseReader}.
 *
 * @author Peter Smith
 */
public class ResponseReaderImpl implements ResponseReader {

    private final RequestAdapter requestAdapter;

    public ResponseReaderImpl(RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    @Override
    public <T> T read(Response response, GenericType<T> responseType) {
        try {
            checkResponse(response);
            extractToken(response);
            return response.readEntity(responseType);
        } finally {
            if (!isCloseable(responseType)) {
                close(response);
            }
        }
    }

    @Override
    public void read(Response response) {
        try {
            checkResponse(response);
            extractToken(response);
        } finally {
            close(response);
        }
    }

    private void extractToken(Response response) {
        String token = response.getHeaderString(AUTH_TOKEN_HEADER);
        if (Objects.nonNull(token)) {
            requestAdapter.consumeAuthenticationToken(token);
        }
    }

    private void checkResponse(Response response) {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            raiseException(response);
        }
    }

    private <T> boolean isCloseable(GenericType<T> responseType) {
        return Closeable.class.isAssignableFrom(responseType.getRawType());
    }

    private void close(Response response) {
        if (Objects.nonNull(response)) {
            response.close();
        }
    }

    private void raiseException(Response response) {
        switch (Response.Status.fromStatusCode(response.getStatus())) {
            case UNAUTHORIZED -> throw new UnauthorizedAccessException(response);
            case FORBIDDEN -> throw new ForbiddenOperationException(response);
            case BAD_REQUEST -> throw new ValidationFailureException(response);
            case NOT_FOUND -> throw new ResourceNotFoundException(response);
            case CONFLICT -> throw new ConflictingRequestException(response);
            default -> throw new RequestProcessingFailureException(response);
        }
    }
}
