package Tracker.Infrastructure.Election;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;

public class ElectionManager
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static boolean running = false;
    private static String leader = null;
    private static String self_ip;
    private static int self_port;
    private static final long waitTime = 10000;
    private static final int maxTrackers = 10;

    public static synchronized boolean detectFailure()
    {
        if (leader == null)
        {
            return true;
        }
        try
        {
            return pingLeader();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static synchronized void initialize(String ip, int port)
    {
        self_ip = ip;
        self_port = port;
    }

    private static boolean pingLeader() throws IOException
    {
        Socket socket = new Socket(InetAddress.getByName(leader), self_port, InetAddress.getByName(self_ip), 0);
        String pingMessage = gson.toJson(new ElectionMessage(MessageType.ping, self_ip, null), ElectionMessage.class);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(pingMessage);
        outputStream.flush();

        long startTime = Instant.now().toEpochMilli();
        boolean noResponse = true;
        while ((Instant.now().toEpochMilli() - startTime) < waitTime)
        {
            if (socket.getInputStream().available() > 0)
            {
                noResponse = false;
            }
        }

        if (noResponse)
        {
            return true;
        }
        return false;
    }

    public static synchronized void initiateElection() throws IOException
    {
        running = true;
        leader = null;
        String[] strs = self_ip.split("\\.");
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        int startingIP = Integer.parseInt(strs[3]);
        int endingIP = maxTrackers;
        String initiateMessage = gson.toJson(new ElectionMessage(MessageType.election, self_ip, null), ElectionMessage.class);
        ArrayList<Socket> sockets = new ArrayList<>();

        for (int counter = startingIP + 1; counter <= endingIP; counter++)
        {
            System.out.println(counter);
            if (InetAddress.getByName(prefix + "." + counter).isReachable(1000))
            {
                Socket socket;
                try
                {
                    socket = new Socket(InetAddress.getByName(prefix + "." + counter), self_port, InetAddress.getByName(self_ip), 0);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeObject(initiateMessage);
                    outputStream.flush();
                    sockets.add(socket);
                } catch (ConnectException e)
                {
                }
            }
        }
        // current time
        long startTime = Instant.now().toEpochMilli();
        boolean bullyReceived = false;
        // wait for t time units
        while ((Instant.now().toEpochMilli() - startTime) < waitTime)
        {
            // check if response received
            for (Socket socket : sockets)
            {
                if (!socket.isClosed()) if (socket.getInputStream().available() > 0)
                {
                    bullyReceived = true;
                    break;
                }
            }
            if (bullyReceived)
            {
                break;
            }
        }

        // if no bully, inform processes they are the leader
        if (!bullyReceived)
        {
            leader = self_ip;
            String leaderMessage = gson.toJson(new ElectionMessage(MessageType.leader, self_ip, null), ElectionMessage.class);
            for (int counter = 1; counter <= endingIP; counter++)
            {
                if (counter == Integer.parseInt(strs[3]))
                {
                    continue;
                }

                if (InetAddress.getByName(prefix + "." + counter).isReachable(1000))
                {
                    Socket socket;
                    try
                    {
                        socket = new Socket(InetAddress.getByName(prefix + "." + counter), self_port, InetAddress.getByName(self_ip), 0);
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject(leaderMessage);
                        outputStream.flush();
                    } catch (ConnectException e)
                    {
                    }
                }
            }
            running = false;
        }

        for (Socket socket : sockets)
        {
            //            socket.close();
        }
    }

    public static synchronized void receiveMessage(Socket socket, ElectionMessage message) throws IOException, JsonSyntaxException
    {
        System.out.println("Receiving");

        if (message.messageType() == MessageType.leader)
        {
            leader = message.process();
            running = false;
        } else if (message.messageType() == MessageType.election)
        {
            if (message.process().compareToIgnoreCase(self_ip) < 0)
            {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ElectionMessage bully = new ElectionMessage(MessageType.bully, self_ip, null);
                outputStream.writeObject(gson.toJson(bully, ElectionMessage.class));
                outputStream.flush();
                // if not running then initiate election
                if (!running)
                {
                    initiateElection();
                }
            }
        } else if (message.messageType() == MessageType.ping)
        {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject("OK");
            outputStream.flush();
        }
    }
}
