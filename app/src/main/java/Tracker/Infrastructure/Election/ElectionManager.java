package Tracker.Infrastructure.Election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ElectionManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static boolean running = false;
    private static String leader = null;
    private static String self_ip;
    private static int self_port;
    private static long waitTime = 10000;

    
    public static synchronized boolean detectFailure() {
        if (leader == null) {
            return true;
        }

        if (leader.equalsIgnoreCase(self_ip)) {
            return false;
        }
        byte[] pingMessage = gson.toJson(
            new ElectionMessage(MessageType.ping, self_ip), ElectionMessage.class).getBytes();
        
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();
        List<PingTask> pingTaskList = new ArrayList<>();
        pingTaskList.add(new PingTask(self_ip, leader, self_port, pingMessage));
        System.out.println("Sending ping");
        try {
            return !executor.invokeAny(pingTaskList, waitTime, TimeUnit.MILLISECONDS);
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Leader did not respond to ping");
        }
        return true;
    }

    public static synchronized void initialize(String ip, int port) {
        self_ip = ip;
        self_port = port;
    }
        
    public static synchronized void initiateElection() {
        running = true;
        leader = null;

        String[] strs = self_ip.split("\\.");
        System.out.println(self_ip);
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        int startSuffix = Integer.parseInt(strs[3]) + 1;

        byte[] initiateMessage = gson.toJson(new ElectionMessage(MessageType.election, self_ip), ElectionMessage.class).getBytes();

        List<ElectionTask> initiateElectionTaskList = new ArrayList<>();
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();

        boolean bullyReceived = false; 

        while (startSuffix < 10) {
            initiateElectionTaskList.add(new ElectionTask(self_ip, prefix + "." + startSuffix, self_port, initiateMessage));
            startSuffix++;
        }

        try
        {
          bullyReceived = executor.invokeAny(initiateElectionTaskList, waitTime, TimeUnit.MILLISECONDS);
          System.out.println("bully received");
          //executor.shutdown(); 
        } 
        catch (InterruptedException e) 
        {
          //e.printStackTrace();
        } 
        catch (NullPointerException e) 
        {
          //e.printStackTrace();
        } 
        catch (TimeoutException e) 
        {
          //e.printStackTrace();
        } 
        catch (ExecutionException e) {
          //e.printStackTrace();
        }
        catch (RejectedExecutionException e) {
           // e.printStackTrace();
        }
        
        // if no bully, inform processes they are the leader 
        if (!bullyReceived) {
            leader = self_ip;
            System.out.println("leader set to " + leader);
            byte[] leaderMessage = gson.toJson(new ElectionMessage(MessageType.leader, self_ip), ElectionMessage.class).getBytes();
            for (int i = 1; i < 10; i++) {
                if (i == Integer.parseInt(strs[3])) {
                    continue;
                }
                executor.execute(new LeaderTask(self_ip, prefix + "." + i, self_port, leaderMessage));
            }
        }
 
        running = false;
    }

    public static synchronized void receiveMessage(Socket socket, DataInputStream inputStream) throws IOException, JsonSyntaxException {
        byte[] message = inputStream.readAllBytes();
        ElectionMessage electionMessage = gson.fromJson(new String(message), ElectionMessage.class);

        if (electionMessage.getMessageType() == MessageType.leader) {
            leader = electionMessage.getProcess();
            running = false;
            socket.close();
            System.out.println("leader set to " + leader);
        }

        else if (electionMessage.getMessageType() == MessageType.election) {
            if (electionMessage.getProcess().compareToIgnoreCase(self_ip) < 0) {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                ElectionMessage bully = new ElectionMessage(MessageType.bully, self_ip);
                outputStream.write(gson.toJson(bully, ElectionMessage.class).getBytes("utf-8"));
                outputStream.flush(); 
                socket.close();
                // if not running then initiate election
                if (!running) {
                    initiateElection();
                }
            }
            else {
                socket.close();
            }
        }

        else if (electionMessage.getMessageType() == MessageType.ping) {
            System.out.println("Got ping");
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(new String("OK").getBytes());
            outputStream.flush();
            socket.close();
        }
    }
}
