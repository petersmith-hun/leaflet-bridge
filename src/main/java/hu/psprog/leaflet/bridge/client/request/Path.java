package hu.psprog.leaflet.bridge.client.request;

/**
 * Enumeration of available paths.
 *
 * @author Peter Smith
 */
public enum Path {
    USERS("/users"),
    USERS_CLAIM("/users/claim");

    private String uri;

    Path(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
