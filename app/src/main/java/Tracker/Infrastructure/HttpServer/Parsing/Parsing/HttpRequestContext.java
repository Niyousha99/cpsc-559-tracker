package Tracker.Infrastructure.HttpServer.Parsing.Parsing;

import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.C_Action;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.H_Action;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.S2_Action;
import Tracker.Infrastructure.HttpServer.Parsing.Parsing.Actions.S_Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HttpRequestContext
{

    public static NonTerminal initialize()
    {
        NonTerminal S = new NonTerminal<>(S_Action.getBuilder());
        NonTerminal S2 = new NonTerminal<>(S2_Action.getBuilder());
        NonTerminal H = new NonTerminal<>(H_Action.getBuilder());
        NonTerminal C = new NonTerminal<>(C_Action.getBuilder());

        // EOF, CRLF, COLON, WORD, WS;
        Token eof = new Token(TokenIdentifier.EOF, "");
        Token crlf = new Token(TokenIdentifier.CRLF, "");
        Token colon = new Token(TokenIdentifier.COLON, "");
        Token word = new Token(TokenIdentifier.WORD, "");
        Token sp = new Token(TokenIdentifier.SP, "");
        Token htab = new Token(TokenIdentifier.HTAB, "");
        Token es = new Token(TokenIdentifier.EOF, "");


        HashMap<TokenIdentifier, ArrayList<Grammar>> S_ = new HashMap<>();
        S_.put(word.identifier(), new ArrayList<>(Arrays.asList(word, sp, word, sp, word, crlf, S2)));
        S.setProductions(S_);

        HashMap<TokenIdentifier, ArrayList<Grammar>> S2_ = new HashMap<>();
        S2_.put(word.identifier(), new ArrayList<>(Arrays.asList(word, colon, C, crlf, H, crlf)));
        S2_.put(crlf.identifier(), new ArrayList<>(List.of(crlf)));
        S2.setProductions(S2_);

        HashMap<TokenIdentifier, ArrayList<Grammar>> H_ = new HashMap<>();
        H_.put(es.identifier(), new ArrayList<>(List.of()));
        H_.put(word.identifier(), new ArrayList<>(Arrays.asList(word, colon, C, crlf, H)));
        H.setProductions(H_);

        HashMap<TokenIdentifier, ArrayList<Grammar>> C_ = new HashMap<>();
        C_.put(es.identifier(), new ArrayList<>(List.of()));
        C_.put(sp.identifier(), new ArrayList<>(Arrays.asList(sp, C)));
        C_.put(htab.identifier(), new ArrayList<>(Arrays.asList(htab, C)));
        C_.put(word.identifier(), new ArrayList<>(Arrays.asList(word, C)));
        C_.put(colon.identifier(), new ArrayList<>(Arrays.asList(colon, C)));
        C.setProductions(C_);

        return S;
    }

}
