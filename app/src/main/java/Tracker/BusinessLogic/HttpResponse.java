package Tracker.BusinessLogic;

import java.util.HashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;

public class HttpResponse<T> {
    private final int statusCode;
    private final String status;
    private final HashMap<String, String> headers;
    private final T body;
    private final static String CRLF = "\r\n";

    public HttpResponse(int stausCode, String status, HashMap<String, String> headers, T body) {
        this.statusCode = stausCode;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public T getBody() {
        return body;
    }

    public static <T> HttpResponseBuilder<T> builder() {
        return new HttpResponseBuilder<T>();
    }

    @Override
    public String toString() {
        String res = "HTTP/1.1 " + statusCode + " " + status + CRLF;
        for (String key : headers.keySet().stream().collect(Collectors.toList())) {
            res = res + key + ": " + headers.get(key) + CRLF;
        }
        res = res + CRLF;
        res = res + body.toString();
        return res;
    }
}
