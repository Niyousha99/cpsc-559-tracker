package Tracker.Infrastructure.Utils.Parsing;

import java.util.ArrayList;

public interface Action {
    boolean isDone();
    void act(Token token);
    ArrayList<String> collect();
}
