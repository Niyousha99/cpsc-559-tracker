package Tracker.BusinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Tracker.BusinessLogic.Utiles.HttpRequestBuilder;

public class HttpRequestObject {
    private final String httpMethod;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequestObject(String httpMethod, String path, String httpVersion, Map<String, String> headers, String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }
    
    public String getPath() {
        return this.path;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        return this.body;
    }
}
