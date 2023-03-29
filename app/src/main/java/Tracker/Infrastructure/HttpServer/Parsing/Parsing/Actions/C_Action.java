package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.TokenIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class C_Action implements Action
{
    boolean done = false;
    String fieldValue = null;

    private C_Action() {}

    public static ActionBuilder<C_Action> getBuilder()
    {
        return new ActionBuilder<>(C_Action::new);
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
        } else if (fieldValue != null)
        {
            fieldValue = fieldValue + token.lexeme();
        } else if ((token.identifier().equals(TokenIdentifier.WORD)) || (token.identifier().equals(TokenIdentifier.COLON)))
        {
            fieldValue = token.lexeme();
        }
    }

    @Override
    public ArrayList<String> collect()
    {
        return new ArrayList<>(List.of(Objects.requireNonNullElse(fieldValue, "")));
    }

}
