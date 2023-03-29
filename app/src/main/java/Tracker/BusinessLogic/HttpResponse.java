package Tracker.BusinessLogic;

import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;

import java.util.HashMap;

public class HttpResponse<T>
{
    private final static String CRLF = "\r\n";
    private final int statusCode;
    private final String status;
    private final HashMap<String, String> headers;
    private final T body;

    public HttpResponse(int statusCode, String status, HashMap<String, String> headers, T body)
    {
        this.statusCode = statusCode;
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static <T> HttpResponseBuilder<T> builder()
    {
        return new HttpResponseBuilder<>();
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getStatus()
    {
        return status;
    }

    public HashMap<String, String> getHeaders()
    {
        return headers;
    }

    public T getBody()
    {
        return body;
    }

    @Override
    public String toString()
    {
        String res = "HTTP/1.1 " + statusCode + " " + status + CRLF;
        for (String key : headers.keySet().stream().toList())
        {
            res = res + key + ": " + headers.get(key) + CRLF;
        }
        res = res + CRLF;
        res = res + body.toString();
        return res;
    }
}
