package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class S2_Action implements Action {
    boolean done = false;
    String fieldName = null;
    boolean firstTime = true;

    private S2_Action() {}

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (token.getIdentifier().equals(TokenIdentifier.CRLF)) {
            done = true;
        }

        else if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
            fieldName = token.getLexeme();
            done = true;
        }
    }

    @Override
    public ArrayList<String> collect() {
        if (fieldName != null) { 
            return new ArrayList<String>(Arrays.asList(fieldName));
        }
        return null;
    }

    public static Action generate() {
        return new S2_Action();
    }

    public static ActionBuilder<S2_Action> getBuilder() {
        return new ActionBuilder<S2_Action>(S2_Action::new);
    }

}