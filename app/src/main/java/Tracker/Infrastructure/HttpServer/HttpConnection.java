package Tracker.Infrastructure.HttpServer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.util.LinkedList;
import java.util.Queue;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Infrastructure.Utils.Parsing.Parser;
import Tracker.Infrastructure.Utils.Parsing.Scanner;
import Tracker.Infrastructure.Utils.Parsing.Token;

public class HttpConnection {
    private final Socket socket;

    public HttpConnection(Socket socket) {
        this.socket = socket;
    }

    public HttpRequestObject getHttpRequest() throws IOException, FailureException {
        DataInputStream inputStream = new DataInputStream(this.socket.getInputStream());
        Queue<Token> queue = new LinkedList<Token>();
        Scanner scanner = new Scanner(inputStream, queue);
        Parser parser = new Parser(scanner, queue);
        return parser.parse();

    }

    public void HttpResponse() {

    }
    
}
