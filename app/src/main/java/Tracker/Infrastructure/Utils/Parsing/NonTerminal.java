package Tracker.Infrastructure.Utils.Parsing;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NonTerminal implements Production {
    private Map<Token, ArrayList<Grammer>> productions;
    private Action action;
    
    public NonTerminal() {
    }

    public void setProductions(Map<Token, ArrayList<Grammer>> productions) {
        this.productions = productions;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action releaseAction() {
        return this.action;
    }

    @Override
    public ArrayList<Grammer> produce(Token token) {
        return productions.getOrDefault(token, null);
    }


    
}
