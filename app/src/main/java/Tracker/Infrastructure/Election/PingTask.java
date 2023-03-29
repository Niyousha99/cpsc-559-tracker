package Tracker.Infrastructure.Election;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class PingTask implements Callable<Boolean>
{
    private final String sourceIp;
    private final String destinationIp;
    private final int destinationPort;
    private final byte[] message;

    public PingTask(String sourceIp, String destinationIp, int destinationPort, byte[] message)
    {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.message = message;
    }

    @Override
    public Boolean call() throws Exception
    {
        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(InetAddress.getByName(sourceIp), 0));
        socket.connect(new InetSocketAddress(InetAddress.getByName(destinationIp), destinationPort));
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(message);
        outputStream.flush();
        socket.shutdownOutput();

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        byte[] response = inputStream.readAllBytes();
        socket.close();
        if ((new String(response)).equalsIgnoreCase("ok"))
        {
            return true;
        }
        throw new Exception();
    }

}
