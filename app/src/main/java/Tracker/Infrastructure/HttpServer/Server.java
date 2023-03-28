package Tracker.Infrastructure.HttpServer;

import Tracker.Infrastructure.ClientTask;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.ProcessManager;
import Tracker.Infrastructure.TrackerTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            ServerSocket serverSocketClient, serverSocketTracker;
            if (ip == null)
            {
                serverSocketClient = new ServerSocket(port);
                serverSocketTracker = new ServerSocket(port + 1);
                ip = serverSocketClient.getInetAddress().getHostAddress();
            } else
            {
                serverSocketClient = new ServerSocket(port, 50, InetAddress.getByName(ip.trim()));
                serverSocketTracker = new ServerSocket(port + 1, 50, InetAddress.getByName(ip.trim()));
            }
            serverSocketClient.setSoTimeout(TIMEOUT);
            serverSocketTracker.setSoTimeout(TIMEOUT);
            ExecutorService cachedThreads = Executors.newCachedThreadPool();
            ElectionManager.initialize(ip, port + 1);
            cachedThreads.execute(new ProcessManager());
            Thread clientSocketThread = new Thread(() -> {
                while (true)
                {
                    try
                    {
                        Socket clientSocket = serverSocketClient.accept();
                        clientSocket.setSoTimeout(TIMEOUT);
                        cachedThreads.execute(new ClientTask(clientSocket));
                    } catch (IOException e)
                    {
                        try
                        {
                            serverSocketClient.setSoTimeout(1000);
                        } catch (SocketException ex)
                        {
                        }
                    }
                }
            });

            Thread trackerSocketThread = new Thread(() -> {
                while (true)
                {
                    try
                    {
                        Socket trackerSocket = serverSocketTracker.accept();
                        trackerSocket.setSoTimeout(TIMEOUT);
                        cachedThreads.execute(new TrackerTask(trackerSocket));
                    } catch (IOException e)
                    {
                        try
                        {
                            serverSocketTracker.setSoTimeout(1000);
                        } catch (SocketException ex)
                        {
                        }
                    }
                }
            });

            clientSocketThread.start();
            trackerSocketThread.start();
        } catch (Exception e)
        {

        }
    }
}
