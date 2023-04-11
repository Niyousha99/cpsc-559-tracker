package Tracker.Infrastructure.Election;

import Tracker.Infrastructure.DataDBImpl;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnectionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ElectionManager
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final long waitTime = 10000;
    private static boolean initialStartup = true;
    private static boolean running = false;
    private static String leader = null;
    private static String self_ip;
    private static int self_port;

    // Stores the listening IP and port of the tracker
    public static synchronized void initialize(String ip, int port)
    {
        self_ip = ip;
        self_port = port;
    }

    // Returns the leader's IP address, null if there isn't one and self if it is the tracker itself
    public static String getLeader()
    {
        if (leader == null) return null;
        else if (self_ip.equalsIgnoreCase(leader)) return "self";
        else return leader;
    }

    // Gets the tracker's listening port
    public static int getPort()
    {
        return self_port;
    }

    // Checks to see if the leader is still alive
    public static synchronized boolean detectFailure()
    {
        if (leader == null)
        {
            return true;
        }

        if (leader.equalsIgnoreCase(self_ip))
        {
            return false;
        }
        byte[] pingMessage = gson.toJson(new ElectionMessage(MessageType.ping, self_ip), ElectionMessage.class).getBytes();

        ExecutorService executor = Executors.newCachedThreadPool();
        List<PingTask> pingTaskList = new ArrayList<>();
        pingTaskList.add(new PingTask(self_ip, leader, self_port, pingMessage));
        System.out.println("Pinging leader");
        try
        {
            return !executor.invokeAny(pingTaskList, waitTime, TimeUnit.MILLISECONDS);
        } catch (Exception e)
        {
            System.out.println("Leader did not respond to ping");
        }
        return true;
    }

    // Initiates an election
    public static synchronized void initiateElection()
    {
        running = true;
        leader = null;

        // Get the local IP address and store the range where other tracker's could be found
        String[] strs = self_ip.split("\\.");
        System.out.println(self_ip);
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        int startSuffix = Integer.parseInt(strs[3]) + 1;

        // Creates an election initiation message
        byte[] initiateMessage = gson.toJson(new ElectionMessage(MessageType.election, self_ip), ElectionMessage.class).getBytes();

        // Create a pool that will handle the distribution of the election initiation message
        List<ElectionTask> initiateElectionTaskList = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        // If it's the first startup of the tracker, then asks around for the existing leader's DB and imports it in first
        if (initialStartup)
        {
            initialStartup = false;
            List<RequestTask> requestTaskList = new ArrayList<>();
            for (int ipSuffix = 1; ipSuffix <= 254; ipSuffix++)
            {
                if (ipSuffix == Integer.parseInt(strs[3])) continue;

                byte[] requestMessage = gson.toJson(new ElectionMessage(MessageType.request, self_ip), ElectionMessage.class).getBytes();
                requestTaskList.add(new RequestTask(self_ip, prefix + "." + ipSuffix, self_port, requestMessage));
            }

            byte[] leaderDataReceived;
            try
            {
                leaderDataReceived = executor.invokeAny(requestTaskList, waitTime, TimeUnit.MILLISECONDS);
                System.out.println("Received data from existing leader");
                if (leaderDataReceived != null)
                    DatabaseConnectionManager.importDB(gson.fromJson(new String(leaderDataReceived), ElectionMessage.class).getData());
            } catch (InterruptedException | NullPointerException | RejectedExecutionException | ExecutionException |
                     TimeoutException e)
            {
                System.out.println("Did not receive data from existing leader");
            }
        }

        // Sends out the election initiation message
        boolean bullyReceived = false;
        while (startSuffix < 254)
        {
            initiateElectionTaskList.add(new ElectionTask(self_ip, prefix + "." + startSuffix, self_port, initiateMessage));
            startSuffix++;
        }

        // Checks to see if a bully message was received
        try
        {
            bullyReceived = executor.invokeAny(initiateElectionTaskList, waitTime, TimeUnit.MILLISECONDS);
            System.out.println("Bully message received");
        } catch (InterruptedException | NullPointerException | RejectedExecutionException | ExecutionException |
                 TimeoutException e)
        {
            System.out.println("No bully message received");
        }

        // If no bully message was received, inform the rest that it is now the leader and send a DB update
        if (!bullyReceived)
        {
            leader = self_ip;
            System.out.println("leader set to " + leader);
            byte[] leaderMessage = gson.toJson(new ElectionMessage(MessageType.leader, self_ip), ElectionMessage.class).getBytes();
            for (int i = 1; i < 254; i++)
            {
                if (i == Integer.parseInt(strs[3]))
                {
                    continue;
                }
                executor.execute(new LeaderTask(self_ip, prefix + "." + i, self_port, leaderMessage));
            }
            syncFollowers();
        }

        running = false;
    }

    // Called any time a message is received that is not designed for the Rest API
    public static synchronized void receiveMessage(Socket socket, DataInputStream inputStream) throws IOException, JsonSyntaxException
    {
        byte[] message = inputStream.readAllBytes();
        ElectionMessage electionMessage = gson.fromJson(new String(message), ElectionMessage.class);

        // Checks to see if the tracker is the leader & that if it is being requested for the current DB by a recently started tracker
        if (electionMessage.getMessageType() == MessageType.request && leader.equalsIgnoreCase(self_ip))
        {
            System.out.println("Sending data to requester");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            byte[] dataMessage = gson.toJson(new ElectionMessage(MessageType.request, self_ip, new GsonBuilder().setPrettyPrinting().create().toJson(new DataDBImpl(DatabaseConnectionManager.getConnection()).getDB())), ElectionMessage.class).getBytes();
            outputStream.write(dataMessage);
            outputStream.flush();
            socket.close();
        }
        // Stores the new leader's IP if a leader message was received
        else if (electionMessage.getMessageType() == MessageType.leader)
        {
            leader = electionMessage.getProcess();
            running = false;
            socket.close();
            System.out.println("Leader set to " + leader);
        }
        // Either send a bully message or initiate an election based on if the IP address is greater than or less than that of the sender
        else if (electionMessage.getMessageType() == MessageType.election)
        {
            if (electionMessage.getProcess().compareToIgnoreCase(self_ip) < 0)
            {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                ElectionMessage bully = new ElectionMessage(MessageType.bully, self_ip);
                outputStream.write(gson.toJson(bully, ElectionMessage.class).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                socket.close();
                // if not running then initiate election
                if (!running)
                {
                    initiateElection();
                }
            } else
            {
                socket.close();
            }
        }
        // Handle a heartbeat ping (intended for the leader)
        else if (electionMessage.getMessageType() == MessageType.ping)
        {
            System.out.println("Got ping message");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write("OK".getBytes());
            outputStream.flush();
            socket.close();
        }
        // Handle a sync message by imported in the passed DB
        else if (electionMessage.getMessageType() == MessageType.sync)
        {
            System.out.println("Got sync data");
            if (electionMessage.getProcess().equals(leader) && !electionMessage.getProcess().equals(self_ip))
                DatabaseConnectionManager.importDB(electionMessage.getData());
        }
    }

    // Sends a serialized copy of the DB to all the follower trackers in the network
    public static synchronized void syncFollowers()
    {
        System.out.println("Sending sync data");
        String[] strs = self_ip.split("\\.");
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        ExecutorService executor = Executors.newCachedThreadPool();

        byte[] dataMessage = gson.toJson(new ElectionMessage(MessageType.sync, self_ip, new GsonBuilder().setPrettyPrinting().create().toJson(new DataDBImpl(DatabaseConnectionManager.getConnection()).getDB())), ElectionMessage.class).getBytes();
        for (int i = 1; i < 254; i++)
        {
            if (i == Integer.parseInt(strs[3]))
            {
                continue;
            }
            executor.execute(new SyncTask(self_ip, prefix + "." + i, self_port, dataMessage));
        }
    }
}
