package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.DefaultNonSuccessfulResponseException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.handler.ResponseReader;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import org.apache.hc.client5.http.classic.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Implementation of {@link BridgeClient}.
 *
 * @author Peter Smith
 */
public class BridgeClientImpl implements BridgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeClientImpl.class);

    private final HttpClient httpClient;
    private final String baseURL;
    private final InvocationFactory invocationFactory;
    private final ResponseReader responseReader;

    public BridgeClientImpl(HttpClient bridgeHttpClient, BridgeSettings bridgeSettings,
                            InvocationFactory invocationFactory, ResponseReader responseReader) {

        this.httpClient = bridgeHttpClient;
        this.baseURL = normalizeBaseURL(bridgeSettings);
        this.invocationFactory = invocationFactory;
        this.responseReader = responseReader;
    }

    @Override
    public <T> T call(RESTRequest request, TypeReference<T> responseType) throws CommunicationFailureException {

        return doCall(() -> httpClient.execute(invocationFactory.getInvocationFor(baseURL, request),
                response -> responseReader.read(response, responseType)), request);
    }

    @Override
    public <T> T call(RESTRequest request, Class<T> responseType) throws CommunicationFailureException {
        return call(request, asTypeReference(responseType));
    }

    @Override
    public void call(RESTRequest request) throws CommunicationFailureException {

        doCall(() -> {
            httpClient.execute(invocationFactory.getInvocationFor(baseURL, request), response -> {
                responseReader.read(response);
                return null;
            });

            return null;
        }, request);
    }

    private String normalizeBaseURL(BridgeSettings bridgeSettings) {

        return bridgeSettings.getHostUrl().endsWith("/")
                ? bridgeSettings.getHostUrl().substring(0, bridgeSettings.getHostUrl().length() - 1)
                : bridgeSettings.getHostUrl();
    }

    private <T> T doCall(HTTPCallSupplier<T> callSupplier, RESTRequest request) throws CommunicationFailureException {

        try {
            return callSupplier.get();

        } catch (ValidationFailureException | DefaultNonSuccessfulResponseException exception) {
            throw exception;

        } catch (Exception exception) {

            LOGGER.error("Bridge failed to process request [{}]", request);
            throw new CommunicationFailureException(exception);
        }
    }

    private <T> TypeReference<T> asTypeReference(Class<T> responseType) {

        return new TypeReference<>() {

            @Override
            public Type getType() {
                return responseType;
            }
        };
    }

    private interface HTTPCallSupplier<T> {
        T get() throws IOException;
    }
}
