package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

import java.util.List;

/// `a + b` and `a - b`: numbers, or two strings joined with `+`. Left to right, so `10 - 2 - 3` is `5`.
public class Sum extends Node {
    /// The result
    public Object value;

    /// Creates the node at the first token of its left side
    public Sum(TusanContext ctx){
        super(ctx);
    }

    /// Reads one or more `Term`s joined by `+` or `-`
    public Sum create(){
        value = new Term(ctx).create().value;
        while (true){
            Token op = interpreter.tokenManager.getNextToken();
            if (op.type != PrebuiltTusanTokenType.OPERATOR || !(op.value.equals("+") || op.value.equals("-"))) return this;
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the right side
            Object right = new Term(ctx).create().value;
            if (op.value.equals("+")){
                if (value instanceof String a && right instanceof String b){
                    value = a + b;
                } else if (value instanceof Number a && right instanceof Number b){
                    value = a.doubleValue() + b.doubleValue();
                } else {
                    interpreter.error("TypeError", "Invalid types provided to add", List.of("You can only add string+string and number+number"));
                }
            } else if (value instanceof Number a && right instanceof Number b){
                value = a.doubleValue() - b.doubleValue();
            } else {
                interpreter.error("TypeError", "Invalid types provided to subtract", null);
            }
        }
    }
}
