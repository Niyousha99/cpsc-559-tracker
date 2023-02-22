package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;
import java.util.Arrays;

import Tracker.Infrastructure.Utils.Parsing.Token;
import Tracker.Infrastructure.Utils.Parsing.TokenIdentifier;

public class S_Action implements Action {
    boolean done = false;
    String httpMethod = null;
    String path = null;
    String version = null;

    private S_Action() {}

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void act(Token token) {
        if (httpMethod == null) {
            httpMethod = token.getLexeme();
        }

        else if (path == null) {
            if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
                path = token.getLexeme();
            }
        }

        else if (version == null) {
            if (token.getIdentifier().equals(TokenIdentifier.WORD)) {
                version = token.getLexeme();
                done = true;
            }
        }
    }

    @Override
    public ArrayList<String> collect() {
        return new ArrayList<String>(Arrays.asList(httpMethod, path, version));
    }

    public static Action generate() {
        return new S_Action();
    }

    public static ActionBuilder<S_Action> getBuilder() {
        return new ActionBuilder<S_Action>(S_Action::new);
    }
}
