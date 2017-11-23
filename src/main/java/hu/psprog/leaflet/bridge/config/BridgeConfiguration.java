package hu.psprog.leaflet.bridge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Base configuration for BridgeClient.
 * Package 'hu.psprog.leaflet.bridge' must be component scanned in frontend application.
 *
 * @author Peter Smith
 */
@Configuration
@ComponentScan(basePackages = "hu.psprog.leaflet.bridge")
public class BridgeConfiguration {

    public static final String DEVICE_ID_HEADER = "X-Device-ID";
    public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";

    private static final String BRIDGE_CONFIG_PROPERTIES = "bridge-config.properties";
    private static final String BRIDGE_BASE_URL = "${bridge.baseUrl}";

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {

        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource(BRIDGE_CONFIG_PROPERTIES));

        return placeholderConfigurer;
    }

    @Value(BRIDGE_BASE_URL)
    private String baseUrl;

    @Bean
    public Client jacksonClient() {

        return ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .register(MultiPartFeature.class)
                .build();
    }

    @Bean
    @DependsOn("jacksonClient")
    public WebTarget webTarget(Client bridgeClient) {

        return bridgeClient.target(baseUrl);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
