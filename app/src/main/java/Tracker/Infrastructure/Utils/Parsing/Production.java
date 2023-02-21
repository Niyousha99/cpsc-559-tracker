package Tracker.Infrastructure.Utils.Parsing;

import java.util.ArrayList;

interface Production extends Grammer {
   ArrayList<Grammer> produce(Token token); 

}
