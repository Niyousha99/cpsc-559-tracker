package Tracker.Infrastructure.Utils.Parsing;

import java.util.Stack;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Utils.FailureException;
import Tracker.Infrastructure.Utils.Parsing.Actions.Action;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Parser {
    private final Scanner scanner;
    private final Queue<Token> queue;
    private Action onAction;

    private String httpMethod;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<String, String>();
    private String body;

    private ArrayList<String> symbols;
    
    public Parser(Scanner scanner, Queue<Token> queue) {
        this.scanner = scanner;
        this.queue = queue;
        this.onAction = null;
        this.symbols = new ArrayList<String>();
    }

    public HttpRequestObject parse() throws FailureException {
        run();
        httpMethod = symbols.get(0);
        path = symbols.get(1);
        version = symbols.get(2);
        int i = 3;
        while ((i+1) < symbols.size()) {
            headers.put(symbols.get(i), symbols.get(i+1).trim());
            i = i + 2;
        }
        
        int iterator = scanner.getContext();
        String context = scanner.consumeContext();
        context = context.substring(iterator);
        scanner.nextContext();
        body = context + scanner.consumeContext();

        return HttpRequestObject.builder()
            .withBody(body)
            .withHeaders(headers)
            .withHttpMethod(httpMethod)
            .withHttpVersion(version)
            .withPath(path)
            .build();
    }

    private void act(Token token) {
        if (onAction != null) {
            onAction.act(token);
            if (onAction.isDone()) {
                ArrayList<String> result = onAction.collect();
                if (result != null) {
                    symbols.addAll(result);
                }
                onAction = null;
            }
        }
    }

    private void run() throws FailureException {
       NonTerminal Start = HttpRequestContext.initialize();
       Stack<Grammer> stack = new Stack<>();
       stack.push(new Token(TokenIdentifier.EOF, ""));
       stack.push(Start);

       while (!stack.empty()) {
            Grammer rule = stack.pop();
            
            if (rule instanceof NonTerminal) {
                if (onAction == null) {
                    onAction = ((NonTerminal) rule).generateAction();
                }
                ArrayList<Grammer> prediction = ((NonTerminal) rule).produce(peek());
                if (prediction == null) {
                    throw new FailureException();
                }
                for (int i = prediction.size()-1; i >= 0; i--) {
                    stack.push(prediction.get(i));
                }
            }

            else if ((rule instanceof Token) &&  
                (((Token) rule).getIdentifier().compareTo(peek().getIdentifier()) == 0)) {
                    Token token = consume();
                    if ((token.getIdentifier().compareTo(TokenIdentifier.CRLF) == 0) 
                        && (peek().getIdentifier().compareTo(TokenIdentifier.CRLF) == 0)) {
                            act(new Token(TokenIdentifier.EOF, "EOF"));
                            break;
                    }

                    else {
                        act(token);
                    }
            }

            else {
                throw new FailureException();
            }
        
       }
    }

    private Token peek() throws FailureException  {
        while (this.queue.peek() == null) {
            scanner.scan();
        }
        return this.queue.peek();
    }

    private Token consume() throws FailureException  {
        while (this.queue.peek() == null) {
            scanner.scan();
        }
        return this.queue.poll();
    }
}
