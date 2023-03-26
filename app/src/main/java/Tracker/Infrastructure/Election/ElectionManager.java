package Tracker.Infrastructure.Election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;

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
        try {
            return pingLeader();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static synchronized void initialize(String ip, int port) {
        self_ip = ip;
        self_port = port;
    }
    
    private static boolean pingLeader() throws UnknownHostException, IOException {
        Socket socket = new Socket(InetAddress.getByName(leader), self_port, InetAddress.getByName(self_ip), 0);
        byte[] pingMessage = gson.toJson(new ElectionMessage(MessageType.ping, self_ip), ElectionMessage.class).getBytes();
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(pingMessage);
        outputStream.flush();
        outputStream.close();

        long startTime = Instant.now().toEpochMilli();
        boolean noResponse = true;
        while ((Instant.now().toEpochMilli() - startTime) < waitTime) {
            if (socket.getInputStream().available() > 0) {
                noResponse = false;
            }
        }

        socket.getInputStream().close();
        socket.close();

        if (noResponse) {
            return true;
        }
        return false;
    }
    
    public static synchronized void initiateElection() throws UnknownHostException, IOException {
        running = true;
        leader = null;
        String[] strs = leader.split(".");
        String prefix = strs[0] + "." + strs[1] + "." + strs[2];
        int startSuffix = Integer.parseInt(strs[3]) + 1;
        byte[] initiateMessage = gson.toJson(new ElectionMessage(MessageType.election, self_ip), ElectionMessage.class).getBytes();
        ArrayList<Socket> sockets = new ArrayList<Socket>();

        while (startSuffix < 255) {
            Socket socket = new Socket(InetAddress.getByName(prefix + "." + startSuffix), self_port, InetAddress.getByName(self_ip), 0);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(initiateMessage);
            outputStream.flush();
            outputStream.close();
            sockets.add(socket);
            startSuffix++;
        }
        // current time
        long startTime = Instant.now().toEpochMilli();
        boolean bullyReceived = false;
        // wait for t time units
        while ((Instant.now().toEpochMilli() - startTime) < waitTime) {
            // check if response received 
            for (Socket socket : sockets) {
                if (socket.getInputStream().available() > 0) {
                    bullyReceived = true;
                    break;
                }
            }
            if (bullyReceived) {
                break;
            }
        }

        // if no bully, inform processes they are the leader 
        if (!bullyReceived) {
            leader = self_ip;
            byte[] leaderMessage = gson.toJson(new ElectionMessage(MessageType.leader, self_ip), ElectionMessage.class).getBytes();
            for (int i = 1; i < 255; i++) {
                if (i == Integer.parseInt(strs[3])) {
                    continue;
                }
                Socket socket = new Socket(InetAddress.getByName(prefix + "." + i), self_port, InetAddress.getByName(self_ip), 0);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.write(leaderMessage);
                outputStream.flush();
                outputStream.close();
                socket.close();
            }
            running = false;
        }

        for (Socket socket : sockets) {
            socket.close();
        }        
    }

    public static synchronized void receiveMessage(Socket socket) throws IOException, JsonSyntaxException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        byte[] message = inputStream.readAllBytes();
        ElectionMessage electionMessage = gson.fromJson(new String(message), ElectionMessage.class);
        inputStream.close(); 

        if (electionMessage.getMessageType() == MessageType.leader) {
            leader = electionMessage.getProcess();
            running = false;
            socket.close();
        }

        else if (electionMessage.getMessageType() == MessageType.election) {
            if (electionMessage.getProcess().compareToIgnoreCase(self_ip) < 0) {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                ElectionMessage bully = new ElectionMessage(MessageType.bully, self_ip);
                outputStream.write(gson.toJson(bully, ElectionMessage.class).getBytes("utf-8"));
                outputStream.flush(); 
                outputStream.close();
                socket.close();
                // if not running then initiate election
                if (!running) {
                    initiateElection();
                }
            }
            socket.close();
        }

        else if (electionMessage.getMessageType() == MessageType.ping) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(new String("OK").getBytes());
            outputStream.flush();
            outputStream.close();
            socket.close();
        }
    }
}
