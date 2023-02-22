package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.Action;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.ActionBuilder;

public class NonTerminal<T extends Action> implements Production {
    private HashMap<TokenIdentifier, ArrayList<Grammer>> productions;
    private final ActionBuilder<T> actionBuilder;
    
    public NonTerminal(ActionBuilder<T> actionBuilder) {
        this.actionBuilder = actionBuilder;
    }

    public void setProductions(HashMap<TokenIdentifier, ArrayList<Grammer>> productions) {
        this.productions = productions;
    }

    public Action generateAction() {
        return this.actionBuilder.build();
    }

    @Override
    public ArrayList<Grammer> produce(Token token) {
        return productions.getOrDefault(token.getIdentifier(), productions.get(TokenIdentifier.EOF));
    }


    
}
