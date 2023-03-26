package Tracker.Infrastructure;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.HttpServer.HttpConnection;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Presentation.RequestHandler;

public class Task implements Runnable  {

    private final Socket socket;

    public Task(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PushbackInputStream pushBackIS = new PushbackInputStream(socket.getInputStream());
            byte[] preamble = pushBackIS.readNBytes(8);
            if ((new String(preamble)).equalsIgnoreCase("Election")) {
                pushBackIS.unread(preamble);
                ElectionManager.receiveMessage(socket);
                pushBackIS.close();
            }
            else {
                pushBackIS.unread(preamble);
                HttpConnection httpConnection = new HttpConnection(this.socket);
                HttpRequestObject httpRequest = httpConnection.getHttpRequest();
                httpConnection.HttpResponse(new RequestHandler().handleRequest(httpRequest));
                pushBackIS.close();
            }
        } catch (IOException | FailureException e) {
        }
    }
}
