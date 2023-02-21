package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Action;
import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class S2_Action implements Action {
    boolean done = false;
    String fieldName = null;
    String fieldValue = null;
    boolean firstTime = true;

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (firstTime && token.getIdentifier().equals(TokenIdentifier.CRLF)) {
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