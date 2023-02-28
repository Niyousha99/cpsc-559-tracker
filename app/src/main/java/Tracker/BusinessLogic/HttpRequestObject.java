package Tracker.BusinessLogic;

import Tracker.BusinessLogic.Utiles.HttpRequestBuilder;

import java.util.Map;

public class HttpRequestObject
{
    private final String httpMethod;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;
    private final String sourceIP;
    private final String sourcePort;

    public HttpRequestObject(String httpMethod, String path, String httpVersion, Map<String, String> headers, String body, String sourceIP, String sourcePort)
    {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
    }

    public static HttpRequestBuilder builder()
    {
        return new HttpRequestBuilder();
    }

    public String getHttpMethod()
    {
        return this.httpMethod;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getHttpVersion()
    {
        return this.httpVersion;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public String getBody()
    {
        return this.body;
    }

    public String getSourceIP()
    {
        return this.sourceIP;
    }

    public String getSourcePort()
    {
        return this.sourcePort;
    }

}
