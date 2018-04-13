package hu.psprog.leaflet.bridge.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Base configuration for BridgeClient.
 *
 * @author Peter Smith
 */
@Configuration
public class BridgeConfiguration {

    public static final String DEVICE_ID_HEADER = "X-Device-ID";
    public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";

    @Bean
    public Client jacksonClient() {

        return ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .register(MultiPartFeature.class)
                .build();
    }
}
