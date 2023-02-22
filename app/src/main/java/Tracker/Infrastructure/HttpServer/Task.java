package Tracker.Infrastructure.HttpServer;

import java.io.IOException;
import java.net.Socket;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;

public class Task implements Runnable  {

    private final HttpConnection connection;

    public Task(Socket socket) {
        this.connection = new HttpConnection(socket);
    }

    @Override
    public void run() {
        try {
            HttpRequestObject httpRequest = this.connection.getHttpRequest();
            this.connection.HttpResponse(new RequestHandler().handleRequest(httpRequest)); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FailureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
