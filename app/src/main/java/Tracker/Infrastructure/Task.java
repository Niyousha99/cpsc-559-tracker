package Tracker.Infrastructure;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;

public class Task implements Runnable
{

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Socket socket;

    public Task(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            PushbackInputStream pushBackIS = new PushbackInputStream(socket.getInputStream());
            int preamble = pushBackIS.read();
            boolean isElection = preamble == (int) '{';
            pushBackIS.unread(preamble);

            if (isElection)
            {
                ElectionManager.receiveMessage(socket, new DataInputStream(pushBackIS));
            } else
            {
                HttpConnection httpConnection = new HttpConnection(this.socket, new DataInputStream(pushBackIS));
                HttpRequestObject httpRequest = httpConnection.getHttpRequest();
                httpConnection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
            }
        } catch (IOException | FailureException e)
        {
            e.printStackTrace();
        }
    }
}
