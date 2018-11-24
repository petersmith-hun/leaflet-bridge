package hu.psprog.leaflet.bridge.integration.request.adapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.DEVICE_ID_HEADER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link HttpServletBasedRequestAdapter}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpServletBasedRequestAdapterTest {

    private static final String DEVICE_ID = "device-id";
    private static final String CLIENT_ID = "client-id";
    private static final String AUTH_TOKEN = "auth-token";

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private HttpServletBasedRequestAdapter httpServletBasedRequestAdapter;

    @Test
    public void shouldProvideDeviceID() {

        // given
        given(httpServletRequest.getAttribute(DEVICE_ID_HEADER)).willReturn(DEVICE_ID);

        // when
        String result = httpServletBasedRequestAdapter.provideDeviceID();

        // then
        assertThat(result, equalTo(DEVICE_ID));
    }

    @Test
    public void shouldProvideClientID() {

        // given
        given(httpServletRequest.getAttribute(CLIENT_ID_HEADER)).willReturn(CLIENT_ID);

        // when
        String result = httpServletBasedRequestAdapter.provideClientID();

        // then
        assertThat(result, equalTo(CLIENT_ID));
    }

    @Test
    public void shouldConsumeAuthenticationToken() {

        // when
        httpServletBasedRequestAdapter.consumeAuthenticationToken(AUTH_TOKEN);

        // then
        verify(httpServletResponse).setHeader(AUTH_TOKEN_HEADER, AUTH_TOKEN);
    }
}
