package hu.psprog.leaflet.bridge.client.request;

import hu.psprog.leaflet.bridge.adapter.RequestBodyAdapter;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.X_CAPTCHA_RESPONSE;

/**
 * Model object containing all necessary information of a request.
 *
 * @author Peter Smith
 */
@Getter
public class RESTRequest {

    private RequestMethod method;
    private Path path;
    private boolean authenticationRequired;
    private Serializable requestBody;
    private final Map<String, Object> pathParameters = new HashMap<>();
    private final Map<String, Object> requestParameters = new HashMap<>();
    private final Map<String, Object> headerParameters = new HashMap<>();
    private boolean multipart;
    private RequestBodyAdapter adapter;

    private RESTRequest() {
        // prevent direct initialization
    }

    public static RESTRequestBuilder getBuilder() {
        return new RESTRequestBuilder();
    }

    /**
     * Builder for {@link RESTRequest} object.
     */
    public static class RESTRequestBuilder {

        private final RESTRequest restRequest;

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
         * Adds a header parameter to the request.
         *
         * @param key key of the header parameter
         * @param value concrete value of the header parameter
         * @return Builder instance
         */
        public RESTRequestBuilder addHeaderParameter(String key, String value) {
            restRequest.headerParameters.put(key, value);
            return this;
        }

        /**
         * _Optional_
         * Adds ReCaptcha response token to the request as X-Captcha-Response header parameter.
         * Mandatory for ReCaptcha-validated requests!
         *
         * @param value ReCaptcha response token
         * @return Builder instance
         */
        public RESTRequestBuilder recaptchaResponse(String value) {
            return addHeaderParameter(X_CAPTCHA_RESPONSE, value);
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
            addRequestParameters(key, String.join(",", valueList));
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
