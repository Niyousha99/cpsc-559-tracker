package Tracker.Infrastructure.Utils.Parsing;

public class Token implements Grammer {
    private final TokenIdentifier identifier;
    private final String lexeme;

    public Token(TokenIdentifier identifier, String lexeme) {
        this.identifier = identifier;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public TokenIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public int hashCode() {
        return this.getIdentifier().hashCode();
    }  
}


