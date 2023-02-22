package Tracker.Infrastructure.Utils.Parsing.Actions;

import java.util.ArrayList;

import Tracker.Infrastructure.Utils.Parsing.Token;

public interface Action  {

    abstract boolean isDone();
    
    abstract void act(Token token);
    
    abstract ArrayList<String> collect();
    
}
