package hu.psprog.leaflet.bridge.integration.client;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactoryProvider;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.impl.BridgeClientImpl;
import org.apache.hc.client5.http.classic.HttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link BridgeClientFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class BridgeClientFactoryTest {

    private static final BridgeSettings BRIDGE_SETTINGS = BridgeSettings.getBuilder()
            .withHostUrl("http://localhost:9999/svc")
            .build();

    @Mock
    private HttpClient bridgeHttpClient;

    @Mock
    private InvocationFactory invocationFactory;

    @Mock
    private InvocationFactoryProvider invocationFactoryProvider;

    @Mock
    private ResponseReader responseReader;

    @InjectMocks
    private BridgeClientFactory bridgeClientFactory;

    @Test
    public void shouldCreateBridgeClient() throws IllegalAccessException {

        // given
        given(invocationFactoryProvider.getInvocationFactory(BRIDGE_SETTINGS)).willReturn(invocationFactory);

        // when
        BridgeClient result = bridgeClientFactory.createBridgeClient(BRIDGE_SETTINGS);

        // then
        assertThat(result, notNullValue());
        assertField(result, "httpClient", bridgeHttpClient);
        assertField(result, "invocationFactory", invocationFactory);
        assertField(result, "responseReader", responseReader);
    }

    private void assertField(BridgeClient result, String fieldName, Object expectedInstance) throws IllegalAccessException {

        var field = Objects.requireNonNull(ReflectionUtils.findField(BridgeClientImpl.class, fieldName));
        field.setAccessible(true);

        assertThat(field.get(result), equalTo(expectedInstance));
    }
}