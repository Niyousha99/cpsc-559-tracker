package Tracker.Infrastructure.HttpServer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread
{
    private final String ip;
    private final int port;
    private static final int TIMEOUT = 1000;

    public Server(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }

    public void listen()
    {
        try
        {
            ServerSocket serverSocket;
            if (ip == null) serverSocket = new ServerSocket(port);
            else serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip.trim()));
            serverSocket.setSoTimeout(TIMEOUT);
            ExecutorService cachedThreads = Executors.newCachedThreadPool();

            while (true)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(TIMEOUT);
                    cachedThreads.execute(new Task(socket));
                } catch (SocketTimeoutException se)
                {
                    serverSocket.setSoTimeout(1000);
                }
            }
        } catch (Exception e)
        {

        }
    }
}
