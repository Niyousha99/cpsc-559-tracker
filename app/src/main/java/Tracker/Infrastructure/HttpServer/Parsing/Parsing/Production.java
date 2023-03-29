package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import java.util.ArrayList;

interface Production extends Grammar
{
    ArrayList<Grammar> produce(Token token);

}
