module bridge.implementation {
    requires java.ws.rs;
    requires com.fasterxml.jackson.core;
    requires slf4j.api;
    requires bridge.api;

    exports hu.psprog.leaflet.bridge.client.request.strategy.impl;
    exports hu.psprog.leaflet.bridge.client.impl;
}