package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.TokenIdentifier;

import java.util.ArrayList;
import java.util.List;

public class S2_Action implements Action
{
    boolean done = false;
    String fieldName = null;
    boolean firstTime = true;

    private S2_Action() {}

    public static Action generate()
    {
        return new S2_Action();
    }

    public static ActionBuilder<S2_Action> getBuilder()
    {
        return new ActionBuilder<>(S2_Action::new);
    }

    @Override
    public boolean isDone()
    {
        return done;
    }

    @Override
    public void act(Token token)
    {
        if (token.identifier().equals(TokenIdentifier.CRLF))
        {
            done = true;
        } else if (token.identifier().equals(TokenIdentifier.WORD))
        {
            fieldName = token.lexeme();
            done = true;
        }
    }

    @Override
    public ArrayList<String> collect()
    {
        if (fieldName != null)
        {
            return new ArrayList<>(List.of(fieldName));
        }
        return null;
    }

}
