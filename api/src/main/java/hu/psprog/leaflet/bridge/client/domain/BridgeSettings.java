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
    private boolean useLeafletLink = true;

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

    public boolean isUseLeafletLink() {
        return useLeafletLink;
    }

    void setUseLeafletLink(boolean useLeafletLink) {
        this.useLeafletLink = useLeafletLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BridgeSettings that = (BridgeSettings) o;

        return new EqualsBuilder()
                .append(hostUrl, that.hostUrl)
                .append(oAuthRegistrationID, that.oAuthRegistrationID)
                .append(useLeafletLink, that.useLeafletLink)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostUrl)
                .append(oAuthRegistrationID)
                .append(useLeafletLink)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("hostUrl", hostUrl)
                .append("oAuthRegistrationID", oAuthRegistrationID)
                .append("useLeafletLink", useLeafletLink)
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
        private boolean useLeafletLink;

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

        public BridgeSettingsBuilder withUseLeafletLink(boolean useLeafletLink) {
            this.useLeafletLink = useLeafletLink;
            return this;
        }

        public BridgeSettings build() {
            BridgeSettings bridgeSettings = new BridgeSettings();
            bridgeSettings.hostUrl = this.hostUrl;
            bridgeSettings.oAuthRegistrationID = this.oAuthRegistrationID;
            bridgeSettings.useLeafletLink = this.useLeafletLink;
            return bridgeSettings;
        }
    }
}
