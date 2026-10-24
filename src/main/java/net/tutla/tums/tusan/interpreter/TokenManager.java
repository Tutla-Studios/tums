package net.tutla.tums.tusan.interpreter;

import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenManager {
    Interpreter interpreter;
    private List<Token> tokens;

    public TokenManager(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public Token getNextToken(){
        if (interpreter.pos >= tokens.toArray().length-1){
            return null;
        } else {
            return tokens.get(interpreter.pos+1);
        }
    }

    public Token getCurrentToken(){
        return interpreter.currentToken;
    }

    public Token nextToken(){
        Token nxt = getNextToken();
        if (nxt != null){
            interpreter.pos++;
            interpreter.currentToken = tokens.get(interpreter.pos);
            return nxt;
        } else {
            interpreter.error("UnfinishedExpression", "Unfinished expression at ENDSCRIPT", null);
        }
        return null;
    }

    public Token expectTokenType(TokenType token){
        Token nxt = nextToken();
        if (nxt.type == token){
            return nxt;
        } else {
            interpreter.error("UnexpectedToken", "Expected "+token.name()+" got "+nxt.type.name(), null);
            return null;
        }
    }

    public Token expectToken(TokenType token, String name){
        Token nxt = nextToken();
        if (nxt.type == token && nxt.value.equals(name)){
            return nxt;
        } else {
            interpreter.error("UnexpectedToken", "Expected "+token.name()+":"+name+" got "+nxt.type.name()+":"+nxt.value, null);
            return null;
        }
    }

    public Token expectTokenClassic(String tokenTypes) {
        String[] types = tokenTypes.replace(" ", "").split("\\|");
        Token nextTkn = getNextToken();

        for (String t : types) {
            if (t.contains(":")) {
                String expectedValue = t.split(":")[1];
                if (expectedValue.equals(nextTkn.value)) {
                    return nextToken();
                }
            } else {
                if (nextTkn.type.toString().equalsIgnoreCase(t)) {
                    return nextToken();
                }
            }
        }

        if (Arrays.asList(types).contains("IDENTIFIER")) {
            interpreter.error("UnexpectedToken", "Expected token " + Arrays.toString(types) + ", got " + nextTkn,
                    List.of("Possible Fix: You might have entered a keyword as a variable name, try renaming it"));
        } else {
            interpreter.error("UnexpectedToken", "Expected token " + Arrays.toString(types) + ", got " + nextTkn, null);
        }

        return null;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> changeTokensParent(Interpreter interpreter){
        List<Token> s = new ArrayList<>();
        for (Token token : this.tokens){
            token.interpreter = interpreter;
            s.add(token);
        }
        this.interpreter = interpreter;
        return s;
    }

    // helper
    public Token get(int pos){
        return tokens.get(pos);
    }

    public List<Token> getAll(){
        return tokens;
    }

    public int length(){
        return tokens.toArray().length;
    }

    public int getPos(){
        return interpreter.pos;
    }
}
