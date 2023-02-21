package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Action;
import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class C_Action implements Action {
    boolean done = false;
    boolean firstTime = true;

    String fieldValue = null;
    boolean emptyexp = false;


    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
            fieldValue = token.getLexeme();
        }

        else if (token.getIdentifier().equals(TokenIdentifier.EOF)) {
            emptyexp = true;
        }
        done = true;
    }
    
    @Override
    public ArrayList<String> collect() {
        if (fieldValue != null) { 
            return new ArrayList<String>(Arrays.asList(fieldValue));
        }

        if (emptyexp) {
            return new ArrayList<String>(Arrays.asList("EOF"));
        }
        return null;
    }
    
}
