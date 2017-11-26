package hu.psprog.leaflet.bridge.client.request;

/**
 * Enumeration of available paths.
 *
 * @author Peter Smith
 */
public enum Path {

    // attachment related paths
    ATTACHMENTS("/attachments"),

    // category related paths
    CATEGORIES("/categories"),
    CATEGORIES_PUBLIC("/categories/public"),
    CATEGORIES_BY_ID("/categories/{id}"),
    CATEGORIES_STATUS("/categories/{id}/status"),

    // comment related paths
    COMMENTS("/comments"),
    COMMENTS_PUBLIC_PAGE_BY_ENTRY("/comments/entry/{id}/{page}"),
    COMMENTS_ALL_PAGE_BY_ENTRY("/comments/entry/{id}/{page}/all"),
    COMMENTS_BY_ID("/comments/{id}"),
    COMMENTS_STATUS("/comments/{id}/status"),
    COMMENTS_DELETE_PERMANENT("/comments/{id}/permanent"),

    // DCP related paths
    DCP("/dcp"),
    DCP_BY_KEY("/dcp/{key}"),

    // document related paths
    DOCUMENTS("/documents"),
    DOCUMENTS_BY_ID("/documents/{id}"),
    DOCUMENTS_BY_LINK("/documents/link/{link}"),
    DOCUMENTS_STATUS("/documents/{id}/status"),

    // entry related paths
    ENTRIES("/entries"),
    ENTRIES_PAGE("/entries/page/{page}"),
    ENTRIES_PAGE_ALL("/entries/page/{page}/all"),
    ENTRIES_CATEGORY_PAGE("/entries/{id}/page/{page}"),
    ENTRIES_BY_LINK("/entries/link/{link}"),
    ENTRIES_BY_ID("/entries/{id}"),
    ENTRIES_STATUS("/entries/{id}/status"),

    // file related paths
    FILES("/files"),
    FILES_ONLY_UUID("/files/{fileIdentifier}"),
    FILES_BY_ID("/files/{fileIdentifier}/{storedFilename}"),
    FILES_DIRECTORIES("/files/directories"),

    // tag related paths
    TAGS("/tags"),
    TAGS_PUBLIC("/tags/public"),
    TAGS_BY_ID("/tags/{id}"),
    TAGS_STATUS("/tags/{id}/status"),
    TAGS_ASSIGN("/tags/assign"),

    // user related paths
    USERS("/users"),
    USERS_CLAIM("/users/claim"),
    USERS_ID("/users/{id}"),
    USERS_ROLE("/users/{id}/role"),
    USERS_PROFILE("/users/{id}/profile"),
    USERS_PASSWORD("/users/{id}/password"),
    USERS_INIT("/users/init"),
    USERS_REGISTER("/users/register"),
    USERS_REVOKE("/users/revoke"),
    USERS_RECLAIM("/users/reclaim"),
    USERS_RENEW("/users/renew");

    private String uri;

    Path(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }
}
