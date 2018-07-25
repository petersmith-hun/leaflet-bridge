package hu.psprog.leaflet.bridge.client.request.impl;

import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.DEVICE_ID_HEADER;

/**
 * {@link RequestAdapter} implementation for Spring (Boot) based web applications.
 * Provides required parameters via the {@link HttpServletRequest} and {@link HttpServletResponse} interfaces.
 *
 * @author Peter Smith
 */
@Component
public class HttpServletBasedRequestAdapter implements RequestAdapter {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    @Autowired
    public HttpServletBasedRequestAdapter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public String provideDeviceID() {
        return extractValue(DEVICE_ID_HEADER);
    }

    @Override
    public String provideClientID() {
        return extractValue(CLIENT_ID_HEADER);
    }

    @Override
    public void consumeAuthenticationToken(String token) {
        httpServletResponse.setHeader(AUTH_TOKEN_HEADER, token);
    }

    private String extractValue(String key) {
        return String.valueOf(httpServletRequest.getAttribute(key));
    }
}
