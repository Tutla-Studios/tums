package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Arrays;
import java.util.Objects;

/// ## Term
///
/// A Term could be a [factor] or a `[factor] (/|*|^|%|) [factor]`:
///
/// - [factor] (Look at the previous heading, everything under there comes here even expressions because of parenthesis)
/// - [factor] * [factor]: Multiplication
/// - [factor] / [factor]: Division
/// - [factor] ^ [factor] or [factor] % [factor]
public class Term extends Node {
    public Object value;
    public Term(TusanContext ctx){
        super(ctx);
    }

    // Any number works here: a loop counter is an Integer and a player's health a Float, not only Doubles
    private boolean numbers(Object left, Object right, String what){
        if (valueOf(left) instanceof Number && valueOf(right) instanceof Number) return true;
        interpreter.error("TypeError", "Expected numbers for " + what, null);
        value = null;
        return false;
    }

    private static Object valueOf(Object node){
        return node instanceof Factor f ? f.value : node instanceof Term t ? t.value : node;
    }

    private static double a(Object node){
        return ((Number) valueOf(node)).doubleValue();
    }

    public Term create(){
        Factor term1 = new Factor(ctx).create();
        if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.OPERATOR){
            Token op = interpreter.tokenManager.getNextToken();
            if (Arrays.asList("*","/","**","^", "%").contains(op.value)){ // TODO: Get list of ops from util
                interpreter.tokenManager.nextToken(); // skip op
                interpreter.tokenManager.nextToken(); // current one is already taken by term thru ctx
                Term term2 = new Term(ctx).create();

                switch (op.value) {
                    case "*" -> {
                        if (term1.value instanceof String s && term2.value instanceof Number times) {
                            value = s.repeat(times.intValue());
                        } else if (numbers(term1, term2, "multiplication")) {
                            value = a(term1) * a(term2);
                        }
                    }
                    case "/" -> {
                        if (numbers(term1, term2, "division")) {
                            if (a(term2) == 0) {
                                interpreter.error("ZeroDivisionError", "You cannot divide by 0", null);
                            } else {
                                value = a(term1) / a(term2);
                            }
                        }
                    }
                    case "^", "**" -> { // both are powers
                        if (numbers(term1, term2, "a power")) value = Math.pow(a(term1), a(term2));
                    }
                    case "%" -> {
                        if (numbers(term1, term2, "modulo")) value = a(term1) % a(term2);
                    }
                }
            } else {
                value = term1.value;
            }
        } else {
            value = term1.value;
        }
        return this;
    }
}
