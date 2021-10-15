package hu.psprog.leaflet.bridge.integration.client;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link BridgeClientRegistration}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class BridgeClientRegistrationTest {

    private static final BridgeSettings BRIDGE_SETTINGS = BridgeSettings.getBuilder().withHostUrl("http://localhost:9999/svc").build();
    private static final String BRIDGE_CLIENT_INSTANCE_NAME = "test";
    private static final Map<String, BridgeSettings> CLIENT_SETTINGS_MAP = new HashMap<>();

    static {
        CLIENT_SETTINGS_MAP.put(BRIDGE_CLIENT_INSTANCE_NAME, BRIDGE_SETTINGS);
    }

    @Mock
    private BridgeClientFactory bridgeClientFactory;

    @Mock
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Mock
    private BridgeClient bridgeClient;

    @InjectMocks
    private BridgeClientRegistration bridgeClientRegistration;

    @Test
    public void shouldInitRegistry() {

        // given
        bridgeClientRegistration.setClients(CLIENT_SETTINGS_MAP);
        given(bridgeClientFactory.createBridgeClient(BRIDGE_SETTINGS)).willReturn(bridgeClient);

        // when
        bridgeClientRegistration.initRegistry();

        // then
        verify(configurableListableBeanFactory).registerSingleton(BRIDGE_CLIENT_INSTANCE_NAME, bridgeClient);
        verify(configurableListableBeanFactory).initializeBean(bridgeClient, BRIDGE_CLIENT_INSTANCE_NAME);
    }

    @Test
    public void shouldInitRegistryWithNoConfiguredClients() {

        // given
        bridgeClientRegistration.setClients(null);

        // when
        bridgeClientRegistration.initRegistry();

        // then
        verifyNoInteractions(configurableListableBeanFactory, bridgeClientFactory);
    }
}