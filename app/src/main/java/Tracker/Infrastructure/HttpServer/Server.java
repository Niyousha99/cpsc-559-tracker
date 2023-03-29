package Tracker.Infrastructure.HttpServer;

import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.ProcessManager;
import Tracker.Infrastructure.Task;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private static final int TIMEOUT = 1000;
    private final int port;
    private String ip;

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
            if (ip == null)
            {
                serverSocket = new ServerSocket(port);
                ip = serverSocket.getInetAddress().getHostAddress();
            } else
            {
                serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip.trim()));
            }
            serverSocket.setSoTimeout(TIMEOUT);
            ExecutorService cachedThreads = Executors.newCachedThreadPool();
            ElectionManager.initialize(ip, port);
            cachedThreads.execute(new ProcessManager());
            while (true)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    System.out.println("Got connection from " + ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress());
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
