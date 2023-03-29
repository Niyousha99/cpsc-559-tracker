package Tracker.Infrastructure.Election;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SyncTask implements Runnable {
    private final String sourceIp;
    private final String destinationIp;
    private final int destinationPort;
    private final byte[] message;

    public SyncTask(String sourceIp, String destinationIp, int destinationPort, byte[] message) {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.message = message;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(new InetSocketAddress(InetAddress.getByName(sourceIp), 0));
            socket.connect(new InetSocketAddress(InetAddress.getByName(destinationIp), destinationPort), 10000);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(message);
            outputStream.flush();
            socket.shutdownOutput();
            socket.close();
        }

        catch (IOException ie) {
            //ie.printStackTrace();
            try {socket.close();} catch (IOException ie1) {}
        }
    }
}
