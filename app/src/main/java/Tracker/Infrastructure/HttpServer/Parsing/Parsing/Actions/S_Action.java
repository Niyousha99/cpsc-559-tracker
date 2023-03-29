package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.TokenIdentifier;

import java.util.ArrayList;
import java.util.Arrays;

public class S_Action implements Action
{
    boolean done = false;
    String httpMethod = null;
    String path = null;
    String version = null;

    private S_Action() {}

    public static Action generate()
    {
        return new S_Action();
    }

    public static ActionBuilder<S_Action> getBuilder()
    {
        return new ActionBuilder<>(S_Action::new);
    }

    @Override
    public boolean isDone()
    {
        return done;
    }

    @Override
    public void act(Token token)
    {
        if (httpMethod == null)
        {
            httpMethod = token.lexeme();
        } else if (path == null)
        {
            if (token.identifier().equals(TokenIdentifier.WORD))
            {
                path = token.lexeme();
            }
        } else if (version == null)
        {
            if (token.identifier().equals(TokenIdentifier.WORD))
            {
                version = token.lexeme();
                done = true;
            }
        }
    }

    @Override
    public ArrayList<String> collect()
    {
        return new ArrayList<>(Arrays.asList(httpMethod, path, version));
    }
}
