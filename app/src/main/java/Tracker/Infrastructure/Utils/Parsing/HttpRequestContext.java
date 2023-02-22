package Tracker.Infrastructure.Utils.Parsing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tracker.Infrastructure.Utils.Parsing.Actions.C_Action;
import Tracker.Infrastructure.Utils.Parsing.Actions.H_Action;
import Tracker.Infrastructure.Utils.Parsing.Actions.S2_Action;
import Tracker.Infrastructure.Utils.Parsing.Actions.S_Action;

public class HttpRequestContext {

    public static NonTerminal initialize() {
        NonTerminal S = new NonTerminal<S_Action>(S_Action.getBuilder());
        NonTerminal S2 = new NonTerminal<S2_Action>(S2_Action.getBuilder());
        NonTerminal H = new NonTerminal<H_Action>(H_Action.getBuilder());
        NonTerminal C = new NonTerminal<C_Action>(C_Action.getBuilder());

        // EOF, CRLF, COLON, WORD, WS;
        Token eof = new Token(TokenIdentifier.EOF, "");
        Token crlf = new Token(TokenIdentifier.CRLF, "");
        Token colon = new Token(TokenIdentifier.COLON, "");
        Token word = new Token(TokenIdentifier.WORD, "");
        Token sp = new Token(TokenIdentifier.SP, "");
        Token htab = new Token(TokenIdentifier.HTAB, "");
        Token es = new Token(TokenIdentifier.EOF, "");


        HashMap<TokenIdentifier, ArrayList<Grammer>> S_ = new HashMap<TokenIdentifier, ArrayList<Grammer>>();
        S_.put(word.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(word, sp, word, sp, word, crlf, S2)));
        S.setProductions(S_);

        HashMap<TokenIdentifier, ArrayList<Grammer>> S2_ = new HashMap<TokenIdentifier, ArrayList<Grammer>>();
        S2_.put(word.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(word, colon, C, crlf, H, crlf)));
        S2_.put(crlf.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(crlf)));
        S2.setProductions(S2_);

        HashMap<TokenIdentifier, ArrayList<Grammer>> H_ = new HashMap<TokenIdentifier, ArrayList<Grammer>>();
        H_.put(es.getIdentifier(), new ArrayList<Grammer>(Arrays.asList()));
        H_.put(word.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(word, colon, C, crlf, H)));
        H.setProductions(H_);

        HashMap<TokenIdentifier, ArrayList<Grammer>> C_ = new HashMap<TokenIdentifier, ArrayList<Grammer>>();
        C_.put(es.getIdentifier(), new ArrayList<Grammer>(Arrays.asList()));
        C_.put(sp.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(sp, C)));
        C_.put(htab.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(htab, C)));
        C_.put(word.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(word, C)));
        C_.put(colon.getIdentifier(), new ArrayList<Grammer>(Arrays.asList(colon, C)));
        C.setProductions(C_);
        
        return S;
    }

}
