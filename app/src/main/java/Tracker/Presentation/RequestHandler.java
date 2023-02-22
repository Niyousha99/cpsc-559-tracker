package Tracker.Presentation;


import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;

public class RequestHandler {

    public HttpResponse handleRequest(HttpRequestObject httpRequest) {
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

    public HttpResponse handleGet(HttpRequestObject httpRequest) {
        if (httpRequest.getPath().equals("/foo")) {
            
            return HttpResponse.builder()
                .withStatus("OK")
                .withStatusCode(200)
                .withBody("Got to foo")
                .build();
        }
        return null;
    }

    public HttpResponse handlePost(HttpRequestObject httpRequest) {
        return null;
    }
    
}
