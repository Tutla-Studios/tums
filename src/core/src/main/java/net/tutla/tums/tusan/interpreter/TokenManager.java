package net.tutla.tums.tusan.interpreter;

import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// In order for your node to work, you have to write the logic (no shit). This is done within the `create` function of your node. The `create` function takes a [Context](https://wiki.tutla.net/tums/developer/context) and a Token as arguments. The context is a reference to the current state of the parser and contains important information. The token is the current token that the parser is working with.
///
/// When the create function is called, the __NEXT token__ is passed (unless your `NodeConfig` is different).
///
/// Let's take a look at this example:
/// ```java
/// import net.tutla.tums.tusan.TusanContext;
/// import net.tutla.tums.tusan.node.Node;
/// import net.tutla.tums.tusan.lexer.Token;
/// import net.tutla.tums.tusan.nodes.expression.Expression;
///
/// public class Print extends Node {
///     public Print(TusanContext ctx){
///         super(ctx);
///     }
///
///     public Print create(){
///         System.out.println(new Expression(ctx).create().value);
///         return this;
///     }
/// }
/// ```
///
/// This is a simple `Print` node that prints the value of an expression.
///
/// Now let's try making a very simple version of a `Print` node that accepts strings:
///
/// ```java
/// import net.tutla.tums.tusan.TusanContext;
/// import net.tutla.tums.tusan.node.Node;
/// import net.tutla.tums.tusan.lexer.Token;
/// import net.tutla.tums.tusan.nodes.expression.Expression;
///
/// public class Print extends Node {
///     public Print(TusanContext ctx){
///         super(ctx);
///     }
///
///     public Print create(){
///         String text = ctx.getCurrentToken().value;
///         System.out.println(text);
///         return this;
///     }
/// }
/// ```
///
/// *You can also use `expectTokenType(TokenType type)` for strings and more strictness if you're only targetting strings.*
///
/// In this example we use the context to get the current token and print its value. This is a very basic example and does not handle complex expressions. So we can use the in-built `ExpressionNode` to parse an expression and then extract the value from it (the expression node is unique as it has a `value` object that stores the computed expression).
///
/// Generally if you want to compute an expression, just use an `ExpressionNode`. It handles function calls, conditions, variables and all other expressions.
///
/// ## Token Manager
///
/// The token manager lets you access the tokens in the current context. You can access it via `ctx.getInterpreter().tokenManager`. It has many features to access tokens:
///
/// <!-- methods -->
///
/// From here all you do is read the `value` of the tokens, and just keep parsing with the features in the TokenManager.
///
/// @tums.title Parsing
/// @tums.summary Parsing code using Tusan in Tums
public class TokenManager {
    Interpreter interpreter;
    private List<Token> tokens;

    /// Creates a token manager for an interpreter. Every interpreter already has one in `interpreter.tokenManager`
    public TokenManager(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    /// Gets next token without consuming it
    public Token getNextToken(){
        if (interpreter.pos >= tokens.toArray().length-1){
            return null;
        } else {
            return tokens.get(interpreter.pos+1);
        }
    }

    /// Gets the current token
    public Token getCurrentToken(){
        return interpreter.currentToken;
    }

    /// Returns the next token and consumes it
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

    /// Expect the next token to be of a specific type (not group). If it doesn't match it calls an error
    public Token expectTokenType(TokenType token){
        Token nxt = nextToken();
        if (nxt.type == token){
            return nxt;
        } else {
            interpreter.error("UnexpectedToken", "Expected "+token.name()+" got "+nxt.type.name(), null);
            return null;
        }
    }

    /// Expect a token as is, if it doesnt match it calls an error
    public Token expectToken(TokenType token, String name){
        Token nxt = nextToken();
        if (nxt.type == token && nxt.value.equals(name)){
            return nxt;
        } else {
            interpreter.error("UnexpectedToken", "Expected "+token.name()+":"+name+" got "+nxt.type.name()+":"+nxt.value, null);
            return null;
        }
    }

    /// Expect the next token to match one of the `|` separated options, each a token type like `IDENTIFIER` or a value like `KEYWORD:item`. If none match it calls an error
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

    /// Replaces the list of tokens
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    /// Moves the tokens and this token manager over to another interpreter
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
    /// Get token at position
    public Token get(int pos){
        return tokens.get(pos);
    }

    /// Returns every token
    public List<Token> getAll(){
        return tokens;
    }

    /// Returns the number of tokens
    public int length(){
        return tokens.toArray().length;
    }

    /// Returns current position
    public int getPos(){
        return interpreter.pos;
    }
}
