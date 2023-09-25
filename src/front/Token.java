package front;

public class Token {
    private TokenType type;
    private String value;
    private Integer lineNumber;
    public Token(TokenType type, String value,Integer lineNumber) {
        this.lineNumber=lineNumber;
        this.type = type;
        this.value = value;
    }
    public Integer getLineNumber() {return lineNumber;}
    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }
}
