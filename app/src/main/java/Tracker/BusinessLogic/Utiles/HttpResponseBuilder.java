package Tracker.BusinessLogic.Utiles;

import Tracker.BusinessLogic.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder<T>
{
    private static final String DATE_PATTERN = "EEE, dd MMM yyyy hh:mm:ss zzz";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_PATTERN);
    private static final String SERVER_NAME = "Tracker";
    private static final Map<String, Boolean> acceptedMethods = Map.ofEntries(new AbstractMap.SimpleEntry<>("GET", true), new AbstractMap.SimpleEntry<>("POST", true), new AbstractMap.SimpleEntry<>("PUT", true), new AbstractMap.SimpleEntry<>("OPTIONS", true), new AbstractMap.SimpleEntry<>("DELETE", true), new AbstractMap.SimpleEntry<>("PATCH", true));
    private int statusCode;
    private String status;
    private HashMap<String, String> headers;
    private T body;

    public HttpResponseBuilder()
    {
        headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Content-Type", "application/json");
        headers.put("Date", getUpdatedDate());
        headers.put("Server", SERVER_NAME);
        headers.put("Content-Length", "0");
        headers.put("Connection", "close");
    }

    private String getUpdatedDate()
    {
        return FORMATTER.format(new Date(System.currentTimeMillis()));
    }

    public HttpResponseBuilder<T> withStatus(String status)
    {
        this.status = status;
        return this;
    }

    public HttpResponseBuilder<T> withStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseBuilder<T> withHeaders(HashMap<String, String> headers)
    {
        this.headers = headers;
        return this;
    }

    public HttpResponseBuilder<T> withBody(T body)
    {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.toString().length()));
        if (body instanceof String)
        {
            headers.put("Content-Type", "text");
        }
        return this;
    }

    public HttpResponse<T> build()
    {
        return new HttpResponse<>(statusCode, status, headers, body);
    }


}
