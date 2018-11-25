module leaflet.component.bridge.integration.spring {
    requires java.compiler;
    requires java.ws.rs;
    requires javax.servlet.api;
    requires com.fasterxml.jackson.jaxrs.json;
    requires jersey.media.multipart;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires leaflet.component.bridge.api;
    requires leaflet.component.bridge.implementation;
    requires java.annotation;
}