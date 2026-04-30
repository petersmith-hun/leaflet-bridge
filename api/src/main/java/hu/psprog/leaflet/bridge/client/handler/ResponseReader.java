package hu.psprog.leaflet.bridge.client.handler;

import org.apache.hc.core5.http.ClassicHttpResponse;
import tools.jackson.core.type.TypeReference;

/**
 * Handles Jersey's response.
 *
 * @author Peter Smith
 */
public interface ResponseReader {

    /**
     * Reads given response and parses it to the given response type of {@link TypeReference}
     *
     * @param response raw {@link ClassicHttpResponse} of the Apache HTTP client
     * @param responseType target type of response content
     * @param <T> T type of target
     * @return response payload as T
     */
    <T> T read(ClassicHttpResponse response, TypeReference<T> responseType);

    /**
     * Reads given void response.
     *
     * @param response raw {@link ClassicHttpResponse} of the Apache HTTP client
     */
    void read(ClassicHttpResponse response);
}
