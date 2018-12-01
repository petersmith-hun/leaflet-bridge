open module leaflet.component.bridge.implementation {
    requires java.ws.rs;
    requires com.fasterxml.jackson.core;
    requires slf4j.api;
    requires leaflet.component.bridge.api;
    requires org.apache.commons.lang3;

    exports hu.psprog.leaflet.bridge.client.request.strategy.impl;
    exports hu.psprog.leaflet.bridge.client.impl;
}