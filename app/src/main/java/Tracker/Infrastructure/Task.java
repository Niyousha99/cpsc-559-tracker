package Tracker.Infrastructure;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.Election.ElectionMessage;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;

public class Task implements Runnable  {

    private final Socket socket;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Task(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PushbackInputStream pushBackIS = new PushbackInputStream(socket.getInputStream());
            int preamble = pushBackIS.read();
            boolean isElection = false;
            if (preamble == (int) '{') {
                isElection = true;
            }
            pushBackIS.unread(preamble);
            //pushBackIS.close();
            //System.out.println("close");

            if (isElection) {
                ElectionManager.receiveMessage(socket, new DataInputStream(pushBackIS));
            }
            else {
                HttpConnection httpConnection = new HttpConnection(this.socket, new DataInputStream(pushBackIS));
                HttpRequestObject httpRequest = httpConnection.getHttpRequest();
                httpConnection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
            }
        } catch (IOException | FailureException e) {
            e.printStackTrace();
        }
    }
}
