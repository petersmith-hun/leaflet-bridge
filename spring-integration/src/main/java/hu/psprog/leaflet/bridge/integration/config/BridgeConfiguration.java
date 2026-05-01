package hu.psprog.leaflet.bridge.integration.config;

import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactoryProvider;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.InvocationFactoryImpl;
import hu.psprog.leaflet.bridge.client.impl.ResponseReaderImpl;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Base configuration for BridgeClient.
 *
 * @author Peter Smith
 */
@Configuration
@ComponentScan("hu.psprog.leaflet.bridge")
public class BridgeConfiguration {

    private static final TimeValue MAX_IDLE_TIME = TimeValue.ofSeconds(30L);

    @Bean
    public HttpClient bridgeHttpClient() {

        return HttpClientBuilder.create()
                .disableAuthCaching()
                .disableAutomaticRetries()
                .disableConnectionState()
                .disableCookieManagement()
                .disableRedirectHandling()
                .evictIdleConnections(MAX_IDLE_TIME)
                .build();
    }

    @Bean
    public InvocationFactory invocationFactory(RequestAuthentication requestAuthentication, RequestAdapter requestAdapter, JsonMapper jsonMapper) {
        return new InvocationFactoryImpl(requestAuthentication, requestAdapter, jsonMapper);
    }

    @Bean
    public ResponseReader responseReader(RequestAdapter requestAdapter, JsonMapper jsonMapper) {
        return new ResponseReaderImpl(requestAdapter, jsonMapper);
    }

    @Bean
    public InvocationFactoryProvider invocationFactoryProvider(InvocationFactory invocationFactory) {
        return bridgeSettings -> invocationFactory;
    }
}
