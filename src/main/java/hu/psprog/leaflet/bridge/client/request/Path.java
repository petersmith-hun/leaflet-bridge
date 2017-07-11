package hu.psprog.leaflet.bridge.client.request;

/**
 * Enumeration of available paths.
 *
 * @author Peter Smith
 */
public enum Path {

    // user related paths
    USERS("/users"),
    USERS_CLAIM("/users/claim"),
    USERS_ID("/users/{id}"),
    USERD_ROLE("/users/{id}/role"),
    USERD_PROFILE("/users/{id}/profile"),
    USERD_PASSWORD("/users/{id}/password"),
    USERS_INIT("/users/init"),
    USERS_REGISTER("/users/register"),

    // entry related paths
    ENTRIES("/entries"),
    ENTRY_BY_LINK("/entries/link/{link}");

    private String uri;

    Path(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }
}
