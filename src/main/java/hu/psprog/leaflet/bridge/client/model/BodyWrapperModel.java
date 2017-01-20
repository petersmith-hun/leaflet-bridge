package hu.psprog.leaflet.bridge.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Peter Smith
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BodyWrapperModel<T> {

    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
