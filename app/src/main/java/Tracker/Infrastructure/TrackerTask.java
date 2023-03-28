package Tracker.Infrastructure;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.Election.ElectionMessage;
import Tracker.Infrastructure.Election.MessageType;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class TrackerTask implements Runnable
{
    private final Socket socket;

    public TrackerTask(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            ObjectInputStream pushBackIS = new ObjectInputStream(socket.getInputStream());
            String preamble = ((String) pushBackIS.readObject());
            ElectionMessage message = new Gson().fromJson(preamble, ElectionMessage.class);

            if (message.messageType().equals(MessageType.election))
            {
                System.out.println("Received Election Message: " + message.messageType() + " From: " + message.process());
                ElectionManager.receiveMessage(socket, message);
            } else if (message.messageType().equals(MessageType.ping))
            {
                System.out.println("Received Ping Message: " + message.messageType() + " From: " + message.process());
            } else if (message.messageType().equals(MessageType.leader))
            {
                System.out.println("Received Leader Message: " + message.messageType() + " From: " + message.process());
            } else
            {
                System.out.println("Received other Message: " + message.messageType() + " From: " + message.process());
                HttpConnection httpConnection = new HttpConnection(this.socket);
                HttpRequestObject httpRequest = httpConnection.getHttpRequest();
                httpConnection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
            }
        } catch (StreamCorruptedException e)
        {
            try
            {
                HttpConnection httpConnection = new HttpConnection(this.socket);
                HttpRequestObject httpRequest = httpConnection.getHttpRequest();
                httpConnection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
            } catch (IOException | FailureException ex)
            {
                e.printStackTrace();
            }
            System.out.println();
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException | FailureException e)
        {
            e.printStackTrace();
        }
    }
}
