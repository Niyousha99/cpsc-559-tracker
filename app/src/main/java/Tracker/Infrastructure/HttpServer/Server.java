package Tracker.Infrastructure.HttpServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Tracker.Infrastructure.ProcessManager;
import Tracker.Infrastructure.Task;
import Tracker.Infrastructure.Election.ElectionManager;

public class Server 
{
    private String ip;
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
            if (ip == null) {
                serverSocket = new ServerSocket(port);
                ip = serverSocket.getInetAddress().getHostAddress();
            }
            else {
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
