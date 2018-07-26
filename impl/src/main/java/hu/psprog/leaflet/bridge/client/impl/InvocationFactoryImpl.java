package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.DEVICE_ID_HEADER;

/**
 * Implementation of {@link InvocationFactory}.
 *
 * @author Peter Smith
 */
public class InvocationFactoryImpl implements InvocationFactory {

    private final RequestAuthentication requestAuthentication;
    private final Map<RequestMethod, CallStrategy> callStrategyMap;
    private final RequestAdapter requestAdapter;

    public InvocationFactoryImpl(RequestAuthentication requestAuthentication, List<CallStrategy> callStrategyList, RequestAdapter requestAdapter) {
        this.requestAuthentication = requestAuthentication;
        this.callStrategyMap = callStrategyList.stream()
                .collect(Collectors.toMap(CallStrategy::forMethod, Function.identity()));
        this.requestAdapter = requestAdapter;
    }

    @Override
    public Invocation getInvocationFor(WebTarget webTarget, RESTRequest restRequest) throws JsonProcessingException {
        WebTarget target = webTarget
                .path(restRequest.getPath().getURI())
                .resolveTemplates(restRequest.getPathParameters());
        target = fillRequestParameters(target, restRequest);
        Invocation.Builder builder = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(DEVICE_ID_HEADER, requestAdapter.provideDeviceID())
                .header(CLIENT_ID_HEADER, requestAdapter.provideClientID());
        authenticate(builder, restRequest);

        return callStrategyMap
                .get(restRequest.getMethod())
                .prepareInvocation(builder, restRequest);
    }

    private WebTarget fillRequestParameters(WebTarget webTarget, RESTRequest request) {

        WebTarget targetWithParameters = webTarget;
        for (Map.Entry entry : request.getRequestParameters().entrySet()) {
            targetWithParameters = targetWithParameters.queryParam(entry.getKey().toString(), entry.getValue());
        }

        return targetWithParameters;
    }

    private void authenticate(Invocation.Builder builder, RESTRequest request) {
        if (request.isAuthenticationRequired()) {
            requestAuthentication.getAuthenticationHeader()
                    .forEach(builder::header);
        }
    }
}
