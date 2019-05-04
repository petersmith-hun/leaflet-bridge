open module leaflet.component.bridge.api {
    requires java.ws.rs;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.lang3;
    requires static spring.context;

    exports hu.psprog.leaflet.bridge.adapter;
    exports hu.psprog.leaflet.bridge.client;
    exports hu.psprog.leaflet.bridge.client.domain;
    exports hu.psprog.leaflet.bridge.client.domain.error;
    exports hu.psprog.leaflet.bridge.client.exception;
    exports hu.psprog.leaflet.bridge.client.handler;
    exports hu.psprog.leaflet.bridge.client.request;
    exports hu.psprog.leaflet.bridge.client.request.strategy;
}