package Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Token;

import java.util.ArrayList;

public interface Action
{

    boolean isDone();

    void act(Token token);

    ArrayList<String> collect();

}
