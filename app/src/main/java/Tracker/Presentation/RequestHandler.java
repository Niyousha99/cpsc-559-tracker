package Tracker.Presentation;


import Tracker.BusinessLogic.DataDB;
import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;
import Tracker.Infrastructure.DataDBImpl;
import Tracker.Infrastructure.Database.DatabaseConnection.DatabaseConnection;
import Tracker.Infrastructure.Database.DatabaseConnection.DatabaseConnectionManager;

public class RequestHandler {
    private final DataDB dataDB;

    public RequestHandler() {
        this.dataDB = new DataDBImpl(DatabaseConnectionManager.getConnection());
    }

    public HttpResponse handleRequest(HttpRequestObject httpRequest) {
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
            return foo(httpRequest);
        }

        return null;
    }

    public HttpResponse foo(HttpRequestObject httpRequest) {
        // we implement the endpoint for foo here
        // we can use this.DataDB to read or write data from our database
        // then use the HttpResponseBuilder to return an HttpResponse 

        return HttpResponse.builder()
            .withStatus("OK")
            .withStatusCode(200)
            .withBody("Got to foo")
            .build();
    }

    public HttpResponse handlePost(HttpRequestObject httpRequest) {
        return null;
    }
    
}
