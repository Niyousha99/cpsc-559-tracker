package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.Action;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.ActionBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class NonTerminal<T extends Action> implements Production
{
    private final ActionBuilder<T> actionBuilder;
    private HashMap<TokenIdentifier, ArrayList<Grammar>> productions;

    public NonTerminal(ActionBuilder<T> actionBuilder)
    {
        this.actionBuilder = actionBuilder;
    }

    public void setProductions(HashMap<TokenIdentifier, ArrayList<Grammar>> productions)
    {
        this.productions = productions;
    }

    public Action generateAction()
    {
        return this.actionBuilder.build();
    }

    @Override
    public ArrayList<Grammar> produce(Token token)
    {
        return productions.getOrDefault(token.identifier(), productions.get(TokenIdentifier.EOF));
    }


}
