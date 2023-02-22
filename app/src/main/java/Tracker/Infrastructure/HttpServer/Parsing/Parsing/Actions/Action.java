package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import java.util.ArrayList;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;

public interface Action  {

    abstract boolean isDone();
    
    abstract void act(Token token);
    
    abstract ArrayList<String> collect();
    
}
