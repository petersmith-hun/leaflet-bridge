package hu.psprog.leaflet.bridge.client.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Smith
 */
public class RESTRequest {

    private RequestMethod method;
    private Path path;
    private String authorizationToken;
    private Serializable requestBody;
    private Map<String, String> pathParameters = new HashMap<>();
    private Map<String, String> requestParameters = new HashMap<>();

    private RESTRequest() {
        // prevent direct initialization
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Path getPath() {
        return path;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public Serializable getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public static class Builder {

        private RESTRequest restRequest;

        public Builder() {
            restRequest = new RESTRequest();
        }

        public Builder method(RequestMethod method) {
            restRequest.method = method;
            return this;
        }

        public Builder path(Path path) {
            restRequest.path = path;
            return this;
        }

        public Builder token(String token) {
            restRequest.authorizationToken = token;
            return this;
        }

        public Builder requestBody(Serializable requestBody) {
            restRequest.requestBody = requestBody;
            return this;
        }

        public Builder addPathParameter(String key, String value) {
            restRequest.pathParameters.put(key, value);
            return this;
        }

        public Builder addRequestParameters(String key, String value) {
            restRequest.requestParameters.put(key, value);
            return this;
        }

        public RESTRequest build() {
            return restRequest;
        }
    }
}
