package hu.psprog.leaflet.bridge.integration.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.InvocationFactoryImpl;
import hu.psprog.leaflet.bridge.client.impl.ResponseReaderImpl;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.DeleteCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.GetCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PostCallStrategy;
import hu.psprog.leaflet.bridge.client.request.strategy.impl.PutCallStrategy;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;

/**
 * Base configuration for BridgeClient.
 *
 * @author Peter Smith
 */
@Configuration
@ComponentScan("hu.psprog.leaflet.bridge")
public class BridgeConfiguration {

    @Bean
    public Client jacksonClient() {

        return ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .register(MultiPartFeature.class)
                .build();
    }

    @Bean
    public CallStrategy getCallStrategy() {
        return new GetCallStrategy();
    }

    @Bean
    public CallStrategy postCallStrategy() {
        return new PostCallStrategy();
    }

    @Bean
    public CallStrategy putCallStrategy() {
        return new PutCallStrategy();
    }

    @Bean
    public CallStrategy deleteCallStrategy() {
        return new DeleteCallStrategy();
    }

    @Bean
    @Autowired
    public InvocationFactory invocationFactory(RequestAuthentication requestAuthentication, List<CallStrategy> callStrategyList, RequestAdapter requestAdapter) {
        return new InvocationFactoryImpl(requestAuthentication, callStrategyList, requestAdapter);
    }

    @Bean
    @Autowired
    public ResponseReader responseReader(RequestAdapter requestAdapter) {
        return new ResponseReaderImpl(requestAdapter);
    }
}
