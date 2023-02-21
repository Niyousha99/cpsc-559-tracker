package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Action;
import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class H_Action implements Action {
    boolean done = false;
    boolean firstTime = true;

    String fieldName = null;
    String fieldValue = null;


    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (firstTime && token.getIdentifier().equals(TokenIdentifier.EOF)) {
            done = true;
            return;
        }

        firstTime = false;

        if (fieldName == null)  {
            if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
                fieldName = token.getLexeme();
            }
        }

        else if (fieldValue == null) {
            if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
                fieldValue = token.getLexeme();
                done = true;
            }
        }

    }

    @Override
    public ArrayList<String> collect() {
        if (fieldName != null && fieldValue != null) { 
            return new ArrayList<String>(Arrays.asList(fieldName, fieldName));
        }
        return null;
    }
    
}
