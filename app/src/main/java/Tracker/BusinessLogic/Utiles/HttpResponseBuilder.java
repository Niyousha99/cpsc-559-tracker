package Tracker.BusinessLogic.Utiles;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.Infrastructure.Utils.FailureException;

public class HttpResponseBuilder<T> {
    private int statusCode; ;
    private String status;
    private HashMap<String, String> headers;
    private T body;
    private static final String DATE_PATTERN = "EEE, dd MMM yyyy hh:mm:ss zzz";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_PATTERN);
    private static final String SERVER_NAME = "Tracker";

    private static final Map<String, Boolean> acceptedMethods = Map.ofEntries(
        new AbstractMap.SimpleEntry<String, Boolean>("GET", true),
        new AbstractMap.SimpleEntry<String, Boolean>("POST", true),
        new AbstractMap.SimpleEntry<String, Boolean>("PUT", true),
        new AbstractMap.SimpleEntry<String, Boolean>("OPTIONS", true),
        new AbstractMap.SimpleEntry<String, Boolean>("DELETE", true),
        new AbstractMap.SimpleEntry<String, Boolean>("PATCH", true)
    );

    public HttpResponseBuilder() {
        headers = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Content-Type", "application/json");
        headers.put("Date", getUpdatedDate());
        headers.put("Server", SERVER_NAME);
        headers.put("Content-Length", "0");
        headers.put("Connection", "close");
    }

    private String getUpdatedDate() {
        return FORMATTER.format(new Date(System.currentTimeMillis()));
    }

    public HttpResponseBuilder<T> withStatus(String status) {
        this.status = status;
        return this;
    }

    public HttpResponseBuilder<T> withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseBuilder<T> withHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

     public HttpResponseBuilder<T> withBody(T body) {
        this.body = body;
        headers.put("Content-Length", "" + body.toString().length());
        if (body instanceof String) {
            headers.put("Content-Type", "text");
        }
        return this;
    }

    public HttpResponse<T> build()  {
        return new HttpResponse<T>(statusCode, status, headers, body);
    }


}
