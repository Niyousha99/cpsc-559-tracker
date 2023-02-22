package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import java.util.ArrayList;

interface Production extends Grammer {
   ArrayList<Grammer> produce(Token token); 

}
