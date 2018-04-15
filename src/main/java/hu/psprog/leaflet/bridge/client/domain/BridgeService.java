package hu.psprog.leaflet.bridge.client.domain;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for client implementations that rely on Bridge.
 *
 * @author Peter Smith
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@DependsOn("bridgeClientRegistration")
public @interface BridgeService {

    /**
     * Sets BridgeClient instance qualifier.
     * Mandatory parameter.
     *
     * @return qualifier
     */
    String client();
}
