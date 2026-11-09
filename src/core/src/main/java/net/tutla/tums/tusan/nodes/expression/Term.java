package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Arrays;

/// ## Term
///
/// A Term could be a [factor] or a `[factor] (*|/|%) [factor]`:
///
/// - [factor] (Look at the previous heading, everything under there comes here even expressions because of parenthesis)
/// - [factor] * [factor]: Multiplication
/// - [factor] / [factor]: Division
/// - [factor] % [factor]: Remainder
/// - [factor] ^ [factor] or [factor] ** [factor]: Power, worked out before `*`, `/` and `%`
///
/// Operators of the same kind are worked out left to right, so `8 / 2 / 2` is `2`. Powers are the exception and go right to left, like in maths: `2 ^ 3 ^ 2` is `2 ^ 9`. A minus in front applies to the whole power, so `-2 ^ 2` is `-4`.
public class Term extends Node {
    public Object value;
    public Term(TusanContext ctx){
        super(ctx);
    }

    public Term create(){
        // left to right: 8 / 2 / 2 is (8 / 2) / 2
        value = new Power(ctx).create().value;
        while (true){
            Token op = interpreter.tokenManager.getNextToken();
            if (op.type != PrebuiltTusanTokenType.OPERATOR || !Arrays.asList("*", "/", "%").contains(op.value)) return this;
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the right side
            Object right = new Power(ctx).create().value;
            switch (op.value) {
                case "*" -> {
                    if (value instanceof String s && right instanceof Number times) {
                        value = s.repeat(times.intValue());
                    } else {
                        value = number(value, "multiplication") * number(right, "multiplication");
                    }
                }
                case "/" -> {
                    double divisor = number(right, "division");
                    if (divisor == 0) interpreter.error("ZeroDivisionError", "You cannot divide by 0", null);
                    value = number(value, "division") / divisor;
                }
                case "%" -> value = number(value, "modulo") % number(right, "modulo");
            }
        }
    }

    // Any number works here: a loop counter is an Integer and a player's health a Float, not only Doubles
    private double number(Object value, String what){
        if (!(value instanceof Number n)) {
            interpreter.error("TypeError", "Expected numbers for " + what, null);
            return 0;
        }
        return n.doubleValue();
    }
}
