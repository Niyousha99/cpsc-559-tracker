package Tracker.BusinessLogic.Utiles;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Utils.FailureException;

public class HttpRequestBuilder {
    private String httpMethod;
    private String path;
    private String httpVersion;
    private Map<String, String> headers;
    private String body;

    private static final Map<String, Boolean> acceptedMethods = Map.ofEntries(
        new AbstractMap.SimpleEntry<String, Boolean>("GET", true),
        new AbstractMap.SimpleEntry<String, Boolean>("POST", true),
        new AbstractMap.SimpleEntry<String, Boolean>("PUT", true),
        new AbstractMap.SimpleEntry<String, Boolean>("OPTIONS", true),
        new AbstractMap.SimpleEntry<String, Boolean>("DELETE", true),
        new AbstractMap.SimpleEntry<String, Boolean>("PATCH", true)
    );

    private static final Map<String, Boolean> acceptedVersions = Map.ofEntries(
        new AbstractMap.SimpleEntry<String, Boolean>("HTTP/1.1", true),
        new AbstractMap.SimpleEntry<String, Boolean>("HTTP/1.0", true)
    );

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequestBuilder withHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpRequestBuilder withPath(String path) {
        this.path = path;
        return this;
    }


    public HttpRequestBuilder withHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpRequestBuilder withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder withBody(String body) {
        this.body = body;
        return this;
    }

    public HttpRequestObject build() throws FailureException {
        if (!acceptedMethods.getOrDefault(httpMethod, false)) {
            throw new FailureException();
        }

        if (path.charAt(0) != '/') {
            if (!(path.charAt(0) == '*' && httpMethod.equals("OPTIONS"))) {
                throw new FailureException();
            }
        }

        String host = headers.getOrDefault("Host", null);
        if (host == null || host.isEmpty()) {
            throw new FailureException();
        }

        return new HttpRequestObject(httpMethod, path, httpVersion, headers, body);
    }


}
