package hu.psprog.leaflet.bridge.client.handler;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * @author Peter Smith
 */
public interface ResponseReader {

    <T> T read(Response response, GenericType<T> responseType);

    void read(Response response);
}
