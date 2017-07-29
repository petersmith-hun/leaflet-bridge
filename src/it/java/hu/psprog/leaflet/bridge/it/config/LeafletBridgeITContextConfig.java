package hu.psprog.leaflet.bridge.it.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.util.HashMap;
import java.util.Map;

import static hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig.COMPONENT_SCAN_PACKAGE;
import static hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE;

/**
 * Context configuration for integration tests.
 *
 * @author Peter Smith
 */
@Profile(INTEGRATION_TEST_CONFIG_PROFILE)
@Configuration
@ComponentScan(basePackages = COMPONENT_SCAN_PACKAGE)
public class LeafletBridgeITContextConfig {

    public static final String INTEGRATION_TEST_CONFIG_PROFILE = "it";
    static final String COMPONENT_SCAN_PACKAGE = "hu.psprog.leaflet.bridge";
    private static final String WIRE_MOCK_SERVER_ADDRESS = "http://localhost:9999";

    private RequestAuthentication requestAuthentication = () -> {
        Map<String, String> auth = new HashMap<>();
        auth.put("Authorization", "Bearer token");
        return auth;
    };

    @Bean
    @Primary
    public WebTarget webTargetStub() {
        return ClientBuilder.newBuilder()
                .register(new JacksonJsonProvider())
                .build()
                .target(WIRE_MOCK_SERVER_ADDRESS);
    }

    @Bean
    public RequestAuthentication requestAuthenticationStub() {
        return requestAuthentication;
    }

    @Bean
    public HttpServletRequest httpServletRequestStub() {
        return Mockito.mock(HttpServletRequest.class);
    }

    @Bean
    public HttpServletResponse httpServletResponse() {
        return Mockito.mock(HttpServletResponse.class);
    }
}
