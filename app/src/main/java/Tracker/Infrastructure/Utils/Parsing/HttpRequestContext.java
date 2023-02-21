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
    // S --> Startline H CRLF         [ Message-Body ]
    // Startline --> Request-Line 
    // Request-Line --> Method SP Request-Target SP Http-Version CRLF
    // Http-Version => http-version 
    // Request-Target => request-target 
    // Method = token-word (GET, POST, PUT, etc)
    // X -> (Header-Field CRLF)
    
    // H -> es | X H
    // H => es 
    //  |=> X H
    //   => Header-Field CRLF H
    //   => field-name ":" OWS Field-Value OWS CRLF H
    // H -> es | field-name ":" OWS Field-Value OWS CRLF H


    // H => (Header-Field CRLF)*
    // Header-Field --> field-name ":" OWS Field-Value OWS
    // field-name = token
    // Field-Value = (Field-Content | Obs-Fold)*
    // Field-Content = Field-VChar [ 1*( SP | HTAB) Field-Vchar]
    // Field-VChar = VCHAR | obs-text
    // Obs-Fold --> CRLF 1*(SP | HTAB)
    // OWS = ( SP | HTAB )*
    // Y -> ( SP | HTAB )
     // Y -> sp | htab
     // OWS -> es | Y OWS
     // OWS -> es | ( (sp | htab) OWS )

     // OWS => es
     //   | => (sp | htab) OWS 
     //     => sp OWS
    //    | => htab OWS 
    // OWS -> es | sp OWS | htab OWS

    // S => Startline H CRLF  
    //   => Request-Line H CRLF 
    //   => Method SP Request-Target SP Http-Version CRLF H CRLF 
    //   => method SP Request-Target SP Http-Version CRLF H CRLF 
    //   => method sp Request-Target SP Http-Version CRLF H CRLF 
    //   => method sp request-target SP Http-Version CRLF H CRLF 
    //   => method sp request-target sp Http-Version CRLF H CRLF 
    //   => method sp request-target sp http-version CRLF H CRLF 
    //   => method sp request-target sp http-version crlf H CRLF        
    //   => method sp request-target sp http-version crlf H CRLF  
    //   => method sp request-target sp http-version crlf CRLF
    //   => method sp request-target sp http-version crlf crlf

    // ||=> method sp request-target sp http-version crlf field-name ":" OWS Field-Value OWS CRLF H CRLF  

    // S -> method sp request-target sp http-version crlf crlf | method sp request-target sp http-version crlf field-name ":" OWS Field-Value OWS CRLF H CRLF 
    // OWS -> es | sp OWS | htab OWS
    // H -> es | field-name ":" OWS Field-Value OWS CRLF H
    // CRLF -> crlf


    // S -> method sp request-target sp http-version crlf S'
    // S' -> crlf | field-name ":" OWS Field-Value OWS CRLF H CRLF 
    // OWS -> es | sp OWS | htab OWS
    // H -> es | field-name ":" OWS Field-Value OWS CRLF H
    // CRLF -> crlf

    // Field-Value = (Field-Content | Obs-Fold)*
    // Field-Content = Field-VChar [ 1*( SP | HTAB) Field-Vchar]
    // Field-VChar = VCHAR | obs-text
    // Obs-Fold --> CRLF 1*(SP | HTAB)

    public static NonTerminal initialize() {
        NonTerminal S = new NonTerminal();
        NonTerminal S2 = new NonTerminal();
        NonTerminal OWS = new NonTerminal();
        NonTerminal H = new NonTerminal();
        NonTerminal CRLF = new NonTerminal();
        NonTerminal FieldValue = new NonTerminal();

        NonTerminal ObsFold = new NonTerminal();
        NonTerminal SP = new NonTerminal();
        NonTerminal HTAB = new NonTerminal();
        NonTerminal RequestTarget = new NonTerminal();
        NonTerminal HttpVersion = new NonTerminal();
        NonTerminal Colon = new NonTerminal();
        NonTerminal C = new NonTerminal();

        // EOF, CRLF, COLON, WORD, WS;
        Token eof = new Token(TokenIdentifier.EOF, "");
        Token crlf = new Token(TokenIdentifier.CRLF, "");
        Token colon = new Token(TokenIdentifier.COLON, "");
        Token word = new Token(TokenIdentifier.WORD, "");
        Token sp = new Token(TokenIdentifier.SP, "");
        Token htab = new Token(TokenIdentifier.HTAB, "");
        Token es = new Token(TokenIdentifier.EOF, "");


        Map<Token, ArrayList<Grammer>> S_ = new HashMap<Token, ArrayList<Grammer>>();
        S_.put(word, new ArrayList<Grammer>(Arrays.asList(SP, RequestTarget, SP, HttpVersion, CRLF, S2)));
        S.setProductions(S_);
        S.setAction(new S_Action());

        Map<Token, ArrayList<Grammer>> S2_ = new HashMap<Token, ArrayList<Grammer>>();
        S2_.put(word, new ArrayList<Grammer>(Arrays.asList(Colon, OWS, FieldValue, OWS, CRLF, H, CRLF)));
        S2_.put(crlf, new ArrayList<Grammer>(Arrays.asList()));
        S2.setProductions(S2_);
        S2.setAction(new S2_Action());

        Map<Token, ArrayList<Grammer>> OWS_ = new HashMap<Token, ArrayList<Grammer>>();
        OWS_.put(es, new ArrayList<Grammer>(Arrays.asList()));
        OWS_.put(sp, new ArrayList<Grammer>(Arrays.asList(OWS)));
        OWS_.put(htab, new ArrayList<Grammer>(Arrays.asList(OWS)));
        OWS.setProductions(OWS_);

        Map<Token, ArrayList<Grammer>> CRLF_ = new HashMap<Token, ArrayList<Grammer>>();
        CRLF_.put(crlf, new ArrayList<Grammer>(Arrays.asList()));
        CRLF.setProductions(CRLF_);

        Map<Token, ArrayList<Grammer>> H_ = new HashMap<Token, ArrayList<Grammer>>();
        H_.put(es, new ArrayList<Grammer>(Arrays.asList()));
        H_.put(word, new ArrayList<Grammer>(Arrays.asList(Colon, FieldValue, C, CRLF, H)));
        H.setProductions(H_);
        H.setAction(new H_Action());

        Map<Token, ArrayList<Grammer>> C_ = new HashMap<Token, ArrayList<Grammer>>();
        C_.put(es, new ArrayList<Grammer>(Arrays.asList()));
        C_.put(sp, new ArrayList<Grammer>(Arrays.asList(C)));
        C_.put(htab, new ArrayList<Grammer>(Arrays.asList(C)));
        C_.put(word, new ArrayList<Grammer>(Arrays.asList(C)));
        C.setProductions(C_);
        C.setAction(new C_Action());

        Map<Token, ArrayList<Grammer>> FieldValue_ = new HashMap<Token, ArrayList<Grammer>>();
        FieldValue_.put(word, new ArrayList<Grammer>(Arrays.asList()));
        FieldValue.setProductions(FieldValue_);


    // S -> method sp request-target sp http-version crlf S'
    // S' -> crlf | field-name ":" OWS Field-Value OWS CRLF H CRLF 
    // OWS -> es | sp OWS | htab OWS
    // H -> es | field-name ":" OWS Field-Value OWS CRLF H
    // CRLF -> crlf

    // H -> es | field-name ":" FieldValue C CRLF H
    // C -> (OWS | word)*
    // C -> es | sp C | htab C | word C

    // Field-Value = Field-Value

        return S;
    }

}
