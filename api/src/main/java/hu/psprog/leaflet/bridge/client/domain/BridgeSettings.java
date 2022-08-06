package hu.psprog.leaflet.bridge.client.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Configuration wrapper for BridgeClient instance settings.
 *
 * @author Peter Smith
 */
public class BridgeSettings {
    
    private String hostUrl;
    private String oAuthRegistrationID;

    public String getHostUrl() {
        return hostUrl;
    }

    void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getOAuthRegistrationID() {
        return oAuthRegistrationID;
    }

    void setOAuthRegistrationID(String oAuthRegistrationID) {
        this.oAuthRegistrationID = oAuthRegistrationID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BridgeSettings that = (BridgeSettings) o;

        return new EqualsBuilder()
                .append(hostUrl, that.hostUrl)
                .append(oAuthRegistrationID, that.oAuthRegistrationID)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostUrl)
                .append(oAuthRegistrationID)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("hostUrl", hostUrl)
                .append("oAuthRegistrationID", oAuthRegistrationID)
                .toString();
    }

    public static BridgeSettingsBuilder getBuilder() {
        return new BridgeSettingsBuilder();
    }

    /**
     * Builder for {@link BridgeSettings}.
     */
    public static final class BridgeSettingsBuilder {
        private String hostUrl;
        private String oAuthRegistrationID;

        private BridgeSettingsBuilder() {
        }

        public BridgeSettingsBuilder withHostUrl(String hostUrl) {
            this.hostUrl = hostUrl;
            return this;
        }

        public BridgeSettingsBuilder withOAuthRegistrationID(String oAuthRegistrationID) {
            this.oAuthRegistrationID = oAuthRegistrationID;
            return this;
        }

        public BridgeSettings build() {
            BridgeSettings bridgeSettings = new BridgeSettings();
            bridgeSettings.hostUrl = this.hostUrl;
            bridgeSettings.oAuthRegistrationID = this.oAuthRegistrationID;
            return bridgeSettings;
        }
    }
}
