open module leaflet.component.bridge.integration.spring {
    requires java.annotation;
    requires java.compiler;
    requires java.ws.rs;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jaxrs.json;
    requires jersey.media.multipart;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires leaflet.component.bridge.api;
    requires leaflet.component.bridge.implementation;
    requires org.apache.tomcat.embed.core;
}