package hu.psprog.leaflet.bridge.client.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Model object containing all necessary information of a request.
 *
 * @author Peter Smith
 */
public class RESTRequest {

    private RequestMethod method;
    private Path path;
    private boolean authenticationRequired;
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

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
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

    /**
     * Builder for {@link RESTRequest} object.
     */
    public static class Builder {

        private RESTRequest restRequest;

        public Builder() {
            restRequest = new RESTRequest();
        }

        /**
         * _Mandatory_
         * Sets request method.
         *
         * @param method an available request method from {@link RequestMethod} enumeration
         * @return Builder instance
         */
        public Builder method(RequestMethod method) {
            restRequest.method = method;
            return this;
        }

        /**
         * _Mandatory_
         * Path to be called by the request (base URL is not required).
         *
         * @param path an available path from {@link Path} enumeration
         * @return Builder instance
         */
        public Builder path(Path path) {
            restRequest.path = path;
            return this;
        }

        /**
         * _Optional_
         * Flag that notifies BridgeClient to send authentication information in header.
         *
         * @return Builder instance
         */
        public Builder authenticated() {
            restRequest.authenticationRequired = true;
            return this;
        }

        /**
         * _Optional_
         * Content of the request body.
         *
         * @param requestBody content of any type that is available in leaflet-rest-api module
         * @return Builder instance
         */
        public Builder requestBody(Serializable requestBody) {
            restRequest.requestBody = requestBody;
            return this;
        }

        /**
         * _Optional_
         * Adds a path parameter to the request. Placeholders in the given path will be changed the given concrete values.
         * For example
         * - given a path parameter with key = id and value = 1 and
         * - given a path /users/{id}
         * - then resolved path will be /users/1
         *
         * @param key key of the path parameter
         * @param value concrete value of the path parameter
         * @return Builder instance
         */
        public Builder addPathParameter(String key, String value) {
            restRequest.pathParameters.put(key, value);
            return this;
        }

        /**
         * _Optional_
         * Adds a GET parameter to the request.
         *
         * @param key key of the GET parameter
         * @param value concrete value of the GET parameter
         * @return Builder instance
         */
        public Builder addRequestParameters(String key, String value) {
            restRequest.requestParameters.put(key, value);
            return this;
        }

        /**
         * Returns built {@link RESTRequest} object.
         *
         * @return built {@link RESTRequest} object
         */
        public RESTRequest build() {
            return restRequest;
        }
    }
}
