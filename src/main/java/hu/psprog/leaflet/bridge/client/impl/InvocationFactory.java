package hu.psprog.leaflet.bridge.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hu.psprog.leaflet.bridge.config.BridgeConfiguration.CLIENT_ID_HEADER;
import static hu.psprog.leaflet.bridge.config.BridgeConfiguration.DEVICE_ID_HEADER;

/**
 * Prepares a Jersey invocation.
 *
 * @author Peter Smith
 */
@Component
class InvocationFactory {

    private final RequestAuthentication requestAuthentication;
    private final Map<RequestMethod, CallStrategy> callStrategyMap;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    InvocationFactory(RequestAuthentication requestAuthentication, List<CallStrategy> callStrategyList, HttpServletRequest httpServletRequest) {
        this.requestAuthentication = requestAuthentication;
        this.callStrategyMap = callStrategyList.stream()
                .collect(Collectors.toMap(CallStrategy::forMethod, Function.identity()));
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Creates an {@link Invocation} for given {@link RESTRequest}.
     *
     * @param webTarget initial {@link WebTarget} to send request via
     * @param restRequest {@link RESTRequest} to build {@link Invocation} for
     * @return built {@link Invocation}
     */
    Invocation getInvocationFor(WebTarget webTarget, RESTRequest restRequest) throws JsonProcessingException {
        WebTarget target = webTarget
                .path(restRequest.getPath().getURI())
                .resolveTemplates(restRequest.getPathParameters());
        target = fillRequestParameters(target, restRequest);
        Invocation.Builder builder = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(DEVICE_ID_HEADER, httpServletRequest.getAttribute(DEVICE_ID_HEADER))
                .header(CLIENT_ID_HEADER, httpServletRequest.getAttribute(CLIENT_ID_HEADER));
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
