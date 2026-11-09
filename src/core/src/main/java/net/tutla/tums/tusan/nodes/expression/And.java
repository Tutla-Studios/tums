package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

/// `a and b`, also written `a && b`. Binds tighter than `or`, looser than `not`. Left to right.
public class And extends Node {
    /// The result
    public Object value;

    /// Creates the node at the first token of its left side
    public And(TusanContext ctx){
        super(ctx);
    }

    /// Reads one or more `Not`s joined by `and`
    public And create(){
        value = new Not(ctx).create().value;
        while (true){
            Token op = interpreter.tokenManager.getNextToken();
            if (op.type != PrebuiltTusanTokenType.LOGIC || !(op.value.equals("and") || op.value.equals("&&"))) return this;
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the right side
            Object right = new Not(ctx).create().value;
            value = bool(value) & bool(right);
        }
    }

    private boolean bool(Object value){
        if (!(value instanceof Boolean)) interpreter.error("TypeError", "Expected boolean for 'and'", null);
        return (Boolean) value;
    }
}
