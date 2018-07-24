package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.exception.ConflictingRequestException;
import hu.psprog.leaflet.bridge.client.exception.ForbiddenOperationException;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Objects;

import static hu.psprog.leaflet.bridge.config.BridgeConfiguration.AUTH_TOKEN_HEADER;

/**
 * Handles Jersey's response.
 *
 * @author Peter Smith
 */
@Component
public class ResponseReaderImpl implements ResponseReader {

    private final RequestAdapter requestAdapter;

    @Autowired
    public ResponseReaderImpl(RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    /**
     * Reads given response and parses it to the given response type of {@link GenericType}
     *
     * @param response raw {@link Response} of Jersey client
     * @param responseType target type of response content
     * @param <T> T type of target
     * @return response payload as T
     */
    @Override
    public <T> T read(Response response, GenericType<T> responseType) {
        checkResponse(response);
        extractToken(response);
        return response.readEntity(responseType);
    }

    /**
     * Reads given void response.
     *
     * @param response raw {@link Response} of Jersey client
     */
    @Override
    public void read(Response response) {
        checkResponse(response);
        extractToken(response);
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

    private void raiseException(Response response) {
        switch (Response.Status.fromStatusCode(response.getStatus())) {
            case UNAUTHORIZED:
                throw new UnauthorizedAccessException(response);
            case FORBIDDEN:
                throw new ForbiddenOperationException(response);
            case BAD_REQUEST:
                throw new ValidationFailureException(response);
            case NOT_FOUND:
                throw new ResourceNotFoundException(response);
            case CONFLICT:
                throw new ConflictingRequestException(response);
            default:
                throw new RequestProcessingFailureException(response);
        }
    }
}
