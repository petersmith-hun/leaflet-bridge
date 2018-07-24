package hu.psprog.leaflet.bridge.adapter;

import java.io.Serializable;

/**
 * Interface for adapters that can transform given request body object to another one for Jersey.
 * In some cases, request objects cannot be sent as REST requests via Jersey. For these cases
 * a {@link RequestBodyAdapter} can be implemented to transform the request to a type,
 * which is transportable.
 *
 * @param <S> source request body type
 * @param <D> destination request body tpye
 * @author Peter Smith
 */
@FunctionalInterface
public interface RequestBodyAdapter<S extends Serializable, D> {

    /**
     * Converts source object of type S to destination object of type D.
     *
     * @param source source object
     * @return transformed object
     */
    D adapt(S source);
}
