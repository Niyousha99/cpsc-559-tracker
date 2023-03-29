package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.Utiles.HttpRequestBuilder;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.Action;
import Tracker.Infrastructure.Utils.FailureException;

import java.util.*;

public class Parser
{
    private final Scanner scanner;
    private final Queue<Token> queue;
    private final Map<String, String> headers = new HashMap<>();
    private final ArrayList<String> symbols;
    private Action onAction;
    private String httpMethod;
    private String path;
    private String version;

    public Parser(Scanner scanner, Queue<Token> queue)
    {
        this.scanner = scanner;
        this.queue = queue;
        this.onAction = null;
        this.symbols = new ArrayList<>();
    }

    public HttpRequestBuilder parse() throws FailureException
    {
        run();
        httpMethod = symbols.get(0);
        path = symbols.get(1);
        version = symbols.get(2);
        int i = 3;
        while ((i + 1) < symbols.size())
        {
            headers.put(symbols.get(i), symbols.get(i + 1).trim());
            i = i + 2;
        }

        return HttpRequestObject.builder().withHeaders(headers).withHttpMethod(httpMethod).withHttpVersion(version).withPath(path);
    }

    private void act(Token token)
    {
        if (onAction != null)
        {
            onAction.act(token);
            if (onAction.isDone())
            {
                ArrayList<String> result = onAction.collect();
                if (result != null)
                {
                    symbols.addAll(result);
                }
                onAction = null;
            }
        }
    }

    private void run() throws FailureException
    {
        NonTerminal Start = HttpRequestContext.initialize();
        Stack<Grammar> stack = new Stack<>();
        stack.push(new Token(TokenIdentifier.EOF, ""));
        stack.push(Start);

        while (!stack.empty())
        {
            Grammar rule = stack.pop();

            if (rule instanceof NonTerminal)
            {
                if (onAction == null)
                {
                    onAction = ((NonTerminal) rule).generateAction();
                }
                ArrayList<Grammar> prediction = ((NonTerminal) rule).produce(peek());
                if (prediction == null)
                {
                    throw new FailureException();
                }
                for (int i = prediction.size() - 1; i >= 0; i--)
                {
                    stack.push(prediction.get(i));
                }
            } else if ((rule instanceof Token) && (((Token) rule).identifier().compareTo(peek().identifier()) == 0))
            {
                Token token = consume();
                if ((token.identifier().compareTo(TokenIdentifier.CRLF) == 0) && (peek().identifier().compareTo(TokenIdentifier.CRLF) == 0))
                {
                    act(new Token(TokenIdentifier.EOF, "EOF"));
                    break;
                } else
                {
                    act(token);
                }
            } else
            {
                throw new FailureException();
            }

        }
    }

    private Token peek() throws FailureException
    {
        while (this.queue.peek() == null)
        {
            scanner.scan();
        }
        return this.queue.peek();
    }

    private Token consume() throws FailureException
    {
        while (this.queue.peek() == null)
        {
            scanner.scan();
        }
        return this.queue.poll();
    }
}
