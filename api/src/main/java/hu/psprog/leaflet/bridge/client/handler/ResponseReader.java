package hu.psprog.leaflet.bridge.client.handler;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Handles Jersey's response.
 *
 * @author Peter Smith
 */
public interface ResponseReader {

    /**
     * Reads given response and parses it to the given response type of {@link GenericType}
     *
     * @param response raw {@link Response} of Jersey client
     * @param responseType target type of response content
     * @param <T> T type of target
     * @return response payload as T
     */
    <T> T read(Response response, GenericType<T> responseType);

    /**
     * Reads given void response.
     *
     * @param response raw {@link Response} of Jersey client
     */
    void read(Response response);
}
