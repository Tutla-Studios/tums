package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

/// A factor raised to a power: `2 ^ 3`, or `2 ** 3`. Binds tighter than `*` and `/`, and reads right to
/// left like in maths, so `2 ^ 3 ^ 2` is `2 ^ 9`.
public class Power extends Node {
    /// The result
    public Object value;

    /// Creates the node at the first token of the base
    public Power(TusanContext ctx){
        super(ctx);
    }

    /// Reads the base and, if a `^` or `**` follows, the power
    public Power create(){
        value = new Factor(ctx).create().value;
        Token op = interpreter.tokenManager.getNextToken();
        if (op.type == PrebuiltTusanTokenType.OPERATOR && (op.value.equals("^") || op.value.equals("**"))){
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the power
            Object exponent = new Power(ctx).create().value; // right to left
            if (!(value instanceof Number base) || !(exponent instanceof Number power)){
                interpreter.error("TypeError", "Expected numbers for a power", null);
                return this;
            }
            value = Math.pow(base.doubleValue(), power.doubleValue());
        }
        return this;
    }
}
