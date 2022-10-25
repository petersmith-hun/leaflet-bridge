package hu.psprog.leaflet.bridge.integration.request.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link StaticRequestAdapter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class StaticRequestAdapterTest {

    private static final String CLIENT_ID = "client1";

    private final StaticRequestAdapter staticRequestAdapter = new StaticRequestAdapter(CLIENT_ID);

    @Test
    public void shouldProvideDeviceIDReturnARandomUUID() {

        // when
        String result = staticRequestAdapter.provideDeviceID();

        // then
        UUID.fromString(result);
    }

    @Test
    public void shouldProvideClientIDReturnTheRegisteredClientID() {

        // when
        String result = staticRequestAdapter.provideClientID();

        // then
        assertThat(result, equalTo(CLIENT_ID));
    }

    @Test
    public void shouldConsumeAuthenticationTokenThrowException() {

        // when
        assertThrows(UnsupportedOperationException.class, () -> staticRequestAdapter.consumeAuthenticationToken("token"));

        // then
        // exception expected
    }
}