package hu.psprog.leaflet.bridge.client.model;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;

import javax.ws.rs.core.GenericType;

/**
 * JSON response reader helper class for response types wrapped in {@link WrapperBodyDataModel}.
 *
 * @param <T> type of wrapped response object
 * @author Peter Smith
 */
public class WrappedResponseGenericType<T extends BaseBodyDataModel> extends GenericType<WrapperBodyDataModel<T>> {

    private WrappedResponseGenericType() {
    }

    /**
     * Instantiates a generic type for given {@link BaseBodyDataModel} descendant type.
     *
     * @param type class of wrapped {@link BaseBodyDataModel} descendant
     * @param <T> type of wrapped {@link BaseBodyDataModel} descendant
     * @return a new {@link WrappedResponseGenericType} instance
     */
    public static <T extends BaseBodyDataModel> WrappedResponseGenericType<T> forType(Class<T> type) {
        return new WrappedResponseGenericType<>();
    }
}
