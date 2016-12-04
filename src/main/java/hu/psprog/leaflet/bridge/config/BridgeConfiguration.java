package hu.psprog.leaflet.bridge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * @author Peter Smith
 */
@Configuration
@ComponentScan(basePackages = "hu.psprog.leaflet.bridge")
public class BridgeConfiguration {

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
    public Client bridgeClient() {
        return ClientBuilder.newClient();
    }

    @Bean
    @DependsOn("bridgeClient")
    public WebTarget webTarget() {

        return bridgeClient().target(baseUrl);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
