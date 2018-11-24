module bridge.spring.integration {
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
    requires bridge.api;
    requires bridge.implementation;
    requires java.annotation;
}