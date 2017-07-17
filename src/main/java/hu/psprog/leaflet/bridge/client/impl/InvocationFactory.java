package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Prepares a Jersey invocation.
 *
 * @author Peter Smith
 */
@Component
class InvocationFactory {

    private final WebTarget webTarget;
    private final RequestAuthentication requestAuthentication;
    private final Map<RequestMethod, CallStrategy> callStrategyMap;

    @Autowired
    InvocationFactory(WebTarget webTarget, RequestAuthentication requestAuthentication, List<CallStrategy> callStrategyList) {
        this.webTarget = webTarget;
        this.requestAuthentication = requestAuthentication;
        this.callStrategyMap = callStrategyList.stream()
                .collect(Collectors.toMap(CallStrategy::forMethod, Function.identity()));
    }

    /**
     * Creates an {@link Invocation} for given {@link RESTRequest}.
     *
     * @param restRequest {@link RESTRequest} to build {@link Invocation} for
     * @return built {@link Invocation}
     */
    Invocation getInvocationFor(RESTRequest restRequest) throws JsonProcessingException {
        WebTarget target = webTarget
                .path(restRequest.getPath().getURI())
                .resolveTemplates(restRequest.getPathParameters());
        target = fillRequestParameters(target, restRequest);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
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
