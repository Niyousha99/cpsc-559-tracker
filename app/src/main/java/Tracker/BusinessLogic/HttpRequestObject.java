package Tracker.BusinessLogic;

import Tracker.BusinessLogic.Utiles.HttpRequestBuilder;

import java.util.Map;

public record HttpRequestObject(String httpMethod, String path, String httpVersion, Map<String, String> headers,
                                String body, String sourceIP, String sourcePort)
{

    public static HttpRequestBuilder builder()
    {
        return new HttpRequestBuilder();
    }

}
