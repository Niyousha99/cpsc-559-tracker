package Tracker.Presentation;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponseObject;

public class RequestHandler {

    public HttpResponseObject handleRequest(HttpRequestObject httpRequest) {
        System.out.println(httpRequest.getHttpMethod());
        System.out.println(httpRequest.getPath());
        System.out.println(httpRequest.getHttpVersion());
        System.out.println(httpRequest.getHeaders().toString());
        if (httpRequest.getHttpMethod().equals("GET")) {
            return handleGet(httpRequest);
        }

        if (httpRequest.getHttpMethod().equals("POST")) {
            return handlePost(httpRequest);
        }

        return null;// should retunn response with 400 status code 
    }

    public HttpResponseObject handleGet(HttpRequestObject httpRequest) {
        if (httpRequest.getPath().equals("/foo")) {
            // return handleFoo(httpRequest)
        }
        return null;
    }

    public HttpResponseObject handlePost(HttpRequestObject httpRequest) {
        return null;
    }
    
}
