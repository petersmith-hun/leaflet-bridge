package hu.psprog.leaflet.bridge.client.request;

/**
 * Enumeration of available paths.
 *
 * @author Peter Smith
 */
public enum Path {

    // attachment related paths
    ATTACHMENTS("/attachments"),

    // entry related paths
    ENTRIES("/entries"),
    ENTRIES_PAGE("/entries/page/{page}"),
    ENTRIES_CATEGORY_PAGE("/entries/{id}/page/{page}"),
    ENTRIES_BY_LINK("/entries/link/{link}"),
    ENTRIES_BY_ID("/entries/id/{id}"),
    ENTRIES_STATUS("/entries/{id}/status"),

    // user related paths
    USERS("/users"),
    USERS_CLAIM("/users/claim"),
    USERS_ID("/users/{id}"),
    USERS_ROLE("/users/{id}/role"),
    USERS_PROFILE("/users/{id}/profile"),
    USERS_PASSWORD("/users/{id}/password"),
    USERS_INIT("/users/init"),
    USERS_REGISTER("/users/register");

    private String uri;

    Path(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }
}
