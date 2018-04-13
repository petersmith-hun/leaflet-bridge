package hu.psprog.leaflet.bridge.client.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Peter Smith
 */
public class BridgeSettings {
    
    private String hostUrl;

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BridgeSettings that = (BridgeSettings) o;

        return new EqualsBuilder()
                .append(hostUrl, that.hostUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hostUrl", hostUrl)
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

        private BridgeSettingsBuilder() {
        }

        public BridgeSettingsBuilder withHostUrl(String hostUrl) {
            this.hostUrl = hostUrl;
            return this;
        }

        public BridgeSettings build() {
            BridgeSettings bridgeSettings = new BridgeSettings();
            bridgeSettings.hostUrl = this.hostUrl;
            return bridgeSettings;
        }
    }
}
