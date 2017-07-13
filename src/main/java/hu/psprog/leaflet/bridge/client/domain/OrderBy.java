package hu.psprog.leaflet.bridge.client.domain;

/**
 * Available ordering fields.
 *
 * @author Peter Smith
 */
public class OrderBy {

    /**
     * Available ordering options for entries.
     */
    public enum Entry {
        ID("id"),
        TITLE("title"),
        CREATED("created");

        private String field;

        Entry(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

    private OrderBy() {
    }
}
