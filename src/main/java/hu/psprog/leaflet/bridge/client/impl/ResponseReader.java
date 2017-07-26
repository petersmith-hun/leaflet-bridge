package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static hu.psprog.leaflet.bridge.config.BridgeConfiguration.AUTH_TOKEN_HEADER;

/**
 * Handles Jersey's response.
 *
 * @author Peter Smith
 */
@Component
class ResponseReader {

    private final HttpServletResponse httpServletResponse;

    @Autowired
    public ResponseReader(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * Reads given response and parses it to the given response type of {@link GenericType}
     *
     * @param response raw {@link Response} of Jersey client
     * @param responseType target type of response content
     * @param <T> T type of target
     * @return response payload as T
     */
    <T> T read(Response response, GenericType<T> responseType) {
        checkResponse(response);
        extractToken(response);
        return response.readEntity(responseType);
    }

    /**
     * Reads given void response.
     *
     * @param response raw {@link Response} of Jersey client
     */
    void read(Response response) {
        checkResponse(response);
        extractToken(response);
    }

    private void extractToken(Response response) {
        httpServletResponse.setHeader(AUTH_TOKEN_HEADER, response.getHeaderString(AUTH_TOKEN_HEADER));
    }

    private void checkResponse(Response response) {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            raiseException(response);
        }
    }

    private void raiseException(Response response) {
        switch (Response.Status.fromStatusCode(response.getStatus())) {
            case BAD_REQUEST:
                throw new ValidationFailureException(response.readEntity(String.class));
            case NOT_FOUND:
                throw new ResourceNotFoundException(response.readEntity(String.class));
            default:
                throw new RequestProcessingFailureException(response.readEntity(String.class));
        }
    }
}
