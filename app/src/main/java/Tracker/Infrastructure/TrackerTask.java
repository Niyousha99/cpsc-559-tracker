package Tracker.Infrastructure;

import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.Election.ElectionMessage;
import Tracker.Infrastructure.Election.MessageType;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TrackerTask implements Runnable
{
    private final Socket socket;

    private final HttpResponseBuilder successResponse = HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success");
    private final HttpResponseBuilder badRequestResponse = HttpResponse.builder().withStatus("BAD REQUEST").withStatusCode(400).withBody("Request could not be understood");
    private final HttpResponseBuilder serverErrorResponse = HttpResponse.builder().withStatus("INTERNAL SERVER ERROR").withStatusCode(500).withBody("Failed to process request");

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
                HttpConnection httpConnection = new HttpConnection(socket);
                httpConnection.HttpResponse(badRequestResponse.build());
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
