package hu.psprog.leaflet.bridge.client.request;

import hu.psprog.leaflet.bridge.adapter.RequestBodyAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Map<String, Object> pathParameters = new HashMap<>();
    private Map<String, Object> requestParameters = new HashMap<>();
    private boolean multipart;
    private RequestBodyAdapter adapter;

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

    public Map<String, Object> getPathParameters() {
        return pathParameters;
    }

    public Map<String, Object> getRequestParameters() {
        return requestParameters;
    }

    public boolean isMultipart() {
        return multipart;
    }

    public RequestBodyAdapter getAdapter() {
        return adapter;
    }

    public static RESTRequestBuilder getBuilder() {
        return new RESTRequestBuilder();
    }

    /**
     * Builder for {@link RESTRequest} object.
     */
    public static class RESTRequestBuilder {

        private RESTRequest restRequest;

        private RESTRequestBuilder() {
            restRequest = new RESTRequest();
        }

        /**
         * _Mandatory_
         * Sets request method.
         *
         * @param method an available request method from {@link RequestMethod} enumeration
         * @return Builder instance
         */
        public RESTRequestBuilder method(RequestMethod method) {
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
        public RESTRequestBuilder path(Path path) {
            restRequest.path = path;
            return this;
        }

        /**
         * _Optional_
         * Flag that notifies BridgeClient to send authentication information in header.
         *
         * @return Builder instance
         */
        public RESTRequestBuilder authenticated() {
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
        public RESTRequestBuilder requestBody(Serializable requestBody) {
            restRequest.requestBody = requestBody;
            return this;
        }

        /**
         * _Optional_
         * Marks request as multipart/form-data request.
         *
         * @return Builder instance
         */
        public RESTRequestBuilder multipart() {
            restRequest.multipart = true;
            return this;
        }

        /**
         * _Optional_
         * Adds {@link RequestBodyAdapter} to the request.
         * Given request body will be converted with the provided adapter.
         *
         * @param adapter {@link RequestBodyAdapter} instance
         * @return Builder instance
         */
        public RESTRequestBuilder adapter(RequestBodyAdapter adapter) {
            restRequest.adapter = adapter;
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
        public RESTRequestBuilder addPathParameter(String key, Object value) {
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
        public RESTRequestBuilder addRequestParameters(String key, String value) {
            restRequest.requestParameters.put(key, value);
            return this;
        }

        /**
         * _Optional_
         * Adds list of GET parameters to the request.
         *
         * @param key key of the GET parameter
         * @param valueList value list of the GET parameter - will be joined with ','
         * @return Builder instance
         */
        public RESTRequestBuilder addRequestParameters(String key, List<String> valueList) {
            addRequestParameters(key, valueList.stream()
                    .collect(Collectors.joining(",")));
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
