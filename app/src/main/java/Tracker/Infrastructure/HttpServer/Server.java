package Tracker.Infrastructure.HttpServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private final int port;
    private static final int TIMEOUT = 1000;

    public Server(int port) {
        this.port = port;
    }

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(TIMEOUT);
            ExecutorService cachedThreads = Executors.newCachedThreadPool();

            while (true) {
                Socket socket = serverSocket.accept();
                cachedThreads.execute(new Task(socket));
            }
        }

        catch (Exception e) {

        }
    }
}
