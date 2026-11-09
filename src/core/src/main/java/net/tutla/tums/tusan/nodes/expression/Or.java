package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

/// `a or b`, also written `a || b`. The loosest operator: `a or b and c` is `a or (b and c)`. Left to right.
public class Or extends Node {
    /// The result
    public Object value;

    /// Creates the node at the first token of its left side
    public Or(TusanContext ctx){
        super(ctx);
    }

    /// Reads one or more `And`s joined by `or`
    public Or create(){
        value = new And(ctx).create().value;
        while (true){
            Token op = interpreter.tokenManager.getNextToken();
            if (op.type != PrebuiltTusanTokenType.LOGIC || !(op.value.equals("or") || op.value.equals("||"))) return this;
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the right side
            Object right = new And(ctx).create().value;
            value = bool(value) | bool(right);
        }
    }

    private boolean bool(Object value){
        if (!(value instanceof Boolean)) interpreter.error("TypeError", "Expected boolean for 'or'", null);
        return (Boolean) value;
    }
}
