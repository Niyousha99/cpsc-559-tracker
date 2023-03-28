package Tracker.Infrastructure;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;

import java.io.IOException;
import java.net.Socket;

public class ClientTask implements Runnable
{

    private final HttpConnection connection;

    public ClientTask(Socket socket)
    {
        this.connection = new HttpConnection(socket);
    }

    @Override
    public void run()
    {
        try
        {
            HttpRequestObject httpRequest = this.connection.getHttpRequest();
            this.connection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
        } catch (IOException | FailureException e)
        {
            e.printStackTrace();
        }
    }
}
