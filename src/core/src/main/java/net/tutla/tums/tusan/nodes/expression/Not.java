package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

/// `not a`: the opposite of a boolean. Applies to the comparison right after it, so `not a and b` is `(not a) and b`.
public class Not extends Node {
    /// The result
    public Object value;

    /// Creates the node at its first token: `not`, or the start of a comparison
    public Not(TusanContext ctx){
        super(ctx);
    }

    /// Reads `not` followed by another `Not`, or else a `Comparison`
    public Not create(){
        if (token.type == PrebuiltTusanTokenType.LOGIC && token.value.equals("not")){
            interpreter.tokenManager.nextToken(); // onto what is negated
            Object operand = new Not(ctx).create().value;
            if (!(operand instanceof Boolean b)) {
                interpreter.error("TypeError", "Expected boolean after 'not'", null);
                return this;
            }
            value = !b;
        } else {
            value = new Comparison(ctx).create().value;
        }
        return this;
    }
}
