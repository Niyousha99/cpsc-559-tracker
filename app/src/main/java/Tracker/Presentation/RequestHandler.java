package Tracker.Presentation;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponseObject;

public class RequestHandler {

    public HttpResponseObject handleRequest(HttpRequestObject httpRequest) {
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
