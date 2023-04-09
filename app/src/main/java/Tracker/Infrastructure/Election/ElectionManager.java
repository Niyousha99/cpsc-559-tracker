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

    public static String getLeader()
    {
        if (leader == null) return null;
        else if (self_ip.equalsIgnoreCase(leader)) return "self";
        else return leader;
    }

    public static int getPort()
    {
        return self_port;
    }

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
        System.out.println("Sending ping");
        try
        {
            return !executor.invokeAny(pingTaskList, waitTime, TimeUnit.MILLISECONDS);
        } catch (Exception e)
        {
            System.out.println("Leader did not respond to ping");
        }
        return true;
    }

    public static synchronized void initialize(String ip, int port)
    {
        self_ip = ip;
        self_port = port;
    }

    public static synchronized void initiateElection()
    {
        running = true;
        leader = null;

        String[] strs = self_ip.split("\\.");
        System.out.println(self_ip);
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        int startSuffix = Integer.parseInt(strs[3]) + 1;

        byte[] initiateMessage = gson.toJson(new ElectionMessage(MessageType.election, self_ip), ElectionMessage.class).getBytes();

        List<ElectionTask> initiateElectionTaskList = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        if (initialStartup)
        {
            initialStartup = false;
            List<RequestTask> requestTaskList = new ArrayList<>();
            for (int ipSuffix = 1; ipSuffix <= 10; ipSuffix++)
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
            }
        }

        boolean bullyReceived = false;

        while (startSuffix < 10)
        {
            initiateElectionTaskList.add(new ElectionTask(self_ip, prefix + "." + startSuffix, self_port, initiateMessage));
            startSuffix++;
        }

        try
        {
            bullyReceived = executor.invokeAny(initiateElectionTaskList, waitTime, TimeUnit.MILLISECONDS);
            System.out.println("bully received");
            //executor.shutdown();
        } catch (InterruptedException | NullPointerException | RejectedExecutionException | ExecutionException |
                 TimeoutException e)
        {
            //e.printStackTrace();
        }

        // if no bully, inform processes they are the leader
        if (!bullyReceived)
        {
            leader = self_ip;
            System.out.println("leader set to " + leader);
            byte[] leaderMessage = gson.toJson(new ElectionMessage(MessageType.leader, self_ip), ElectionMessage.class).getBytes();
            for (int i = 1; i < 10; i++)
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

    public static synchronized void receiveMessage(Socket socket, DataInputStream inputStream) throws IOException, JsonSyntaxException
    {
        byte[] message = inputStream.readAllBytes();
        ElectionMessage electionMessage = gson.fromJson(new String(message), ElectionMessage.class);

        if (electionMessage.getMessageType() == MessageType.request && leader.equalsIgnoreCase(self_ip))
        {
            System.out.println("Sending data to requester");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            byte[] dataMessage = gson.toJson(new ElectionMessage(MessageType.request, self_ip, new GsonBuilder().setPrettyPrinting().create().toJson(new DataDBImpl(DatabaseConnectionManager.getConnection()).getDB())), ElectionMessage.class).getBytes();
            outputStream.write(dataMessage);
            outputStream.flush();
            socket.close();
        } else if (electionMessage.getMessageType() == MessageType.leader)
        {
            leader = electionMessage.getProcess();
            running = false;
            socket.close();
            System.out.println("leader set to " + leader);
        } else if (electionMessage.getMessageType() == MessageType.election)
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
        } else if (electionMessage.getMessageType() == MessageType.ping)
        {
            System.out.println("Got ping");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write("OK".getBytes());
            outputStream.flush();
            socket.close();
        } else if (electionMessage.getMessageType() == MessageType.sync)
        {
            System.out.println("Got sync data");
            if (electionMessage.getProcess().equals(leader) && !electionMessage.getProcess().equals(self_ip))
                DatabaseConnectionManager.importDB(electionMessage.getData());
        }
    }

    public static synchronized void syncFollowers()
    {
        System.out.println("Sending sync data");
        String[] strs = self_ip.split("\\.");
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        ExecutorService executor = Executors.newCachedThreadPool();

        byte[] dataMessage = gson.toJson(new ElectionMessage(MessageType.sync, self_ip, new GsonBuilder().setPrettyPrinting().create().toJson(new DataDBImpl(DatabaseConnectionManager.getConnection()).getDB())), ElectionMessage.class).getBytes();
        for (int i = 1; i < 10; i++)
        {
            if (i == Integer.parseInt(strs[3]))
            {
                continue;
            }
            executor.execute(new SyncTask(self_ip, prefix + "." + i, self_port, dataMessage));
        }
    }
}
