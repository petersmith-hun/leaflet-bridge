package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Handles Jersey's response.
 *
 * @author Peter Smith
 */
@Component
class ResponseReader {

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
        return response.readEntity(responseType);
    }

    /**
     * Reads given void response.
     *
     * @param response raw {@link Response} of Jersey client
     */
    void read(Response response) {
        checkResponse(response);
    }

    private void checkResponse(Response response) {
        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        if (status != Response.Status.OK) {
            raiseException(status, response);
        }
    }

    private void raiseException(Response.Status status, Response response) {
        switch (status) {
            case BAD_REQUEST:
                throw new ValidationFailureException(response.readEntity(String.class));
            case NOT_FOUND:
                throw new ResourceNotFoundException(response.readEntity(String.class));
            default:
                throw new RequestProcessingFailureException(response.readEntity(String.class));
        }
    }
}
