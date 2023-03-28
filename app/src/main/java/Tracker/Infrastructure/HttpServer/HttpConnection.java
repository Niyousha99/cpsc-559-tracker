package Tracker.Infrastructure.HttpServer;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpRequestBuilder;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Parser;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Scanner;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;
import Tracker.Infrastructure.Utils.FailureException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class HttpConnection
{
    private final Socket socket;
    private final DataInputStream inputStream;

    public HttpConnection(Socket socket, DataInputStream inputStream)
    {
        this.socket = socket;
        this.inputStream = inputStream;
    }

    public HttpRequestObject getHttpRequest() throws IOException, FailureException
    {
        Queue<Token> queue = new LinkedList<Token>();
        Scanner scanner = new Scanner(inputStream, queue);
        Parser parser = new Parser(scanner, queue);
        HttpRequestBuilder requestBuilder = parser.parse();

        int iterator = scanner.getContext();
        String context = scanner.consumeContext();
        String body = context.substring(iterator);
        scanner.nextContext();
        String tmp = scanner.consumeContext();
        if (tmp != null)
        {
            body = body + tmp;
        }
        requestBuilder = requestBuilder.withBody(body).withSourceIP(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress()).withSourcePort(String.valueOf(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort()));
        return requestBuilder.build();
    }

    public void HttpResponse(HttpResponse response) throws IOException
    {
        DataOutputStream outputStream = new DataOutputStream(this.socket.getOutputStream());
        outputStream.write(response.toString().getBytes());
        outputStream.flush();
        socket.shutdownOutput();
        outputStream.close();
        this.socket.close();
    }

}
