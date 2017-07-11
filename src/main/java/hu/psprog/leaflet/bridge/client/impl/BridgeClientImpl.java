package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Implementation of {@link BridgeClient}.
 *
 * @author Peter Smith
 */
@Component
class BridgeClientImpl implements BridgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeClientImpl.class);

    private final InvocationFactory invocationFactory;
    private final ResponseReader responseReader;

    @Autowired
    public BridgeClientImpl(InvocationFactory invocationFactory, ResponseReader responseReader) {
        this.invocationFactory = invocationFactory;
        this.responseReader = responseReader;
    }

    @Override
    public <T extends BaseBodyDataModel> WrapperBodyDataModel<T> call(RESTRequest request, GenericType<WrapperBodyDataModel<T>> responseType) throws CommunicationFailureException {
        Response response = doCall(request);
        return responseReader.read(response, responseType);
    }

    @Override
    public <T extends BaseBodyDataModel> T call(RESTRequest request, Class<T> responseType) throws CommunicationFailureException {
        Response response = doCall(request);
        return responseReader.read(response, new GenericType<>(responseType));
    }

    private <T extends BaseBodyDataModel> Response doCall(RESTRequest request) throws CommunicationFailureException {
        try {
            return invocationFactory
                    .getInvocationFor(request)
                    .invoke();
        } catch (IOException e) {
            LOGGER.error("Bridge failed to process request [{}]", request);
            throw new CommunicationFailureException(e);
        }
    }
}
