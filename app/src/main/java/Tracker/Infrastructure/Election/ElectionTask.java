package Tracker.Infrastructure.Election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import Tracker.Infrastructure.Utils.FailureException;

public class ElectionTask implements Callable<Boolean> {
    private final String sourceIp;
    private final String destinationIp;
    private final int destinationPort;
    private final byte[] message;
    private final Gson gson;
    
    public ElectionTask(String sourceIp, String destinationIp, int destinationPort, byte[] message) {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.message = message;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Boolean call() throws JsonSyntaxException, UnknownHostException, IOException, FailureException  {
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(InetAddress.getByName(sourceIp), 0));
        socket.connect(new InetSocketAddress(InetAddress.getByName(destinationIp), destinationPort));
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(message);
        outputStream.flush();
        socket.shutdownOutput();

        // check to see if bully message received 
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        byte[] input = inputStream.readAllBytes();
        ElectionMessage reply = gson.fromJson(new String(input), ElectionMessage.class);
        socket.close();
        if (reply.getMessageType() == MessageType.bully) {
            return true;
        }

        throw new FailureException();
    }
    
}
