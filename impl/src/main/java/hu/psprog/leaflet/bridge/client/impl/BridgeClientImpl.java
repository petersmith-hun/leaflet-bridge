package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Implementation of {@link BridgeClient}.
 *
 * @author Peter Smith
 */
public class BridgeClientImpl implements BridgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeClientImpl.class);

    private final WebTarget webTarget;
    private final InvocationFactory invocationFactory;
    private final ResponseReader responseReader;

    public BridgeClientImpl(WebTarget webTarget, InvocationFactory invocationFactory, ResponseReader responseReader) {
        this.webTarget = webTarget;
        this.invocationFactory = invocationFactory;
        this.responseReader = responseReader;
    }

    @Override
    public <T> T call(RESTRequest request, GenericType<T> responseType) throws CommunicationFailureException {
        Response response = doCall(request);
        return responseReader.read(response, responseType);
    }

    @Override
    public <T> T call(RESTRequest request, Class<T> responseType) throws CommunicationFailureException {
        Response response = doCall(request);
        return responseReader.read(response, new GenericType<>(responseType));
    }

    @Override
    public void call(RESTRequest request) throws CommunicationFailureException {
        Response response = doCall(request);
        responseReader.read(response);
    }

    private Response doCall(RESTRequest request) throws CommunicationFailureException {
        try {
            return invocationFactory
                    .getInvocationFor(webTarget, request)
                    .invoke();
        } catch (IOException e) {
            LOGGER.error("Bridge failed to process request [{}]", request);
            throw new CommunicationFailureException(e);
        }
    }
}
