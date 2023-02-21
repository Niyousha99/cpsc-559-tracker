package Tracker.Infrastructure.Utils.Parsing;

import java.util.Stack;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.Infrastructure.Utils.FailureException;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Parser {
    private final Scanner scanner;
    private final Queue<Token> queue;
    private final Queue<Action> actions;

    private String httpMethod;
    private String path;
    private String version;
    private Map<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
    private String body;

    private ArrayList<String> symbols;
    
    public Parser(Scanner scanner, Queue<Token> queue) {
        this.scanner = scanner;
        this.queue = queue;
        this.actions = new LinkedList<Action>();
        this.symbols = new ArrayList<String>();
    }

    public HttpRequestObject parse() throws FailureException {
        run();
        httpMethod = symbols.get(0);
        path = symbols.get(1);
        version = symbols.get(2);
        int i = 3;
        while (i < symbols.size()) {
            String fieldName = symbols.get(i);
            ArrayList<String> tmp = new ArrayList<>();
            i++;
            while (!symbols.get(i).equals("EOF")) {
                tmp.add(symbols.get(i));
                i++;
            }
            headers.put(fieldName, tmp);
            i++;
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
        if (actions.peek() != null) {
            Action action = actions.peek();
            action.act(token);
            if (action.isDone()) {
                actions.poll();
                ArrayList<String> result = action.collect();
                if (result != null) {
                    symbols.addAll(result);
                }
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
                Action action = ((NonTerminal) rule).releaseAction();
                if (action != null) {
                    actions.add(action);
                }
                ArrayList<Grammer> prediction = ((NonTerminal) rule).produce(peek());
                if (prediction == null) {
                    throw new FailureException();
                }
                while (!prediction.isEmpty()) {
                    stack.push(prediction.remove(prediction.size()-1));
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
