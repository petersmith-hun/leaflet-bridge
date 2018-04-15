package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Handles BridgeClient instance registration based on Bridge configuration provided by the application.
 * Configuration must be specified in the following way:
 * 'bridge.clients.client-name.parameters' (currently only the host-url parameter is supported)
 * For every records in the configuration like the one above, a new BridgeClient instance will be instantiated,
 * and will be put into the bean context with the specified 'client-name'.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties("bridge")
class BridgeClientRegistration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeClientRegistration.class);

    private BridgeClientFactory bridgeClientFactory;
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private Map<String, BridgeSettings> clients;

    @Autowired
    public BridgeClientRegistration(BridgeClientFactory bridgeClientFactory, ConfigurableListableBeanFactory configurableListableBeanFactory) {
        this.bridgeClientFactory = bridgeClientFactory;
        this.configurableListableBeanFactory = configurableListableBeanFactory;
    }

    @PostConstruct
    public void initRegistry() {
        Optional.ofNullable(clients)
                .orElse(Collections.emptyMap())
                .entrySet().stream()
                .peek(entry -> LOGGER.info("Registering client [{}] with configuration [{}]", entry.getKey(), entry.getValue()))
                .forEach(entry -> {
                    BridgeClient bridgeClient = bridgeClientFactory.createBridgeClient(entry.getValue());
                    configurableListableBeanFactory.registerSingleton(entry.getKey(), bridgeClient);
                    configurableListableBeanFactory.initializeBean(bridgeClient, entry.getKey());
                });
    }

    public Map<String, BridgeSettings> getClients() {
        return clients;
    }

    public void setClients(Map<String, BridgeSettings> clients) {
        this.clients = clients;
    }
}
