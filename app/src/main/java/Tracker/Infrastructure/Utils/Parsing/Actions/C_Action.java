package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class C_Action implements Action {
    boolean done = false;
    String fieldValue = null;

    private C_Action() {}

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (token.getIdentifier().equals(TokenIdentifier.CRLF)) {
            done = true;
            return;
        }

        else if (fieldValue != null) {
            fieldValue = fieldValue + token.getLexeme();
        }

        else if ((token.getIdentifier().equals(TokenIdentifier.WORD)) || (token.getIdentifier().equals(TokenIdentifier.COLON))) {
            fieldValue = token.getLexeme();
        }
    }
    
    @Override
    public ArrayList<String> collect() {
        if (fieldValue != null) { 
            return new ArrayList<String>(Arrays.asList(fieldValue));
        }
        return new ArrayList<String>(Arrays.asList(""));
    }

    public static ActionBuilder<C_Action> getBuilder() {
        return new ActionBuilder<C_Action>(C_Action::new);
    }
    
}
