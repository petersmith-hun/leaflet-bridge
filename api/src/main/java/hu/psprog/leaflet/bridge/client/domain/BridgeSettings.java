package hu.psprog.leaflet.bridge.client.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration wrapper for BridgeClient instance settings.
 *
 * @author Peter Smith
 */
@Data
@Builder(setterPrefix = "with", builderMethodName = "getBuilder")
public class BridgeSettings {
    
    private String hostUrl;
    private String oAuthRegistrationID;

    @Builder.Default
    private boolean useLeafletLink = true;
}
