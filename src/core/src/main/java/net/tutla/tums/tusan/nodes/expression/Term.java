package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Arrays;
import java.util.Objects;

public class Term extends Node {
    public Object value;
    public Term(TusanContext ctx){
        super(ctx);
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
                        if (term1.value instanceof String && term2.value instanceof Double) {
                            value = ((String) term1.value).repeat(((Double) term2.value).intValue());
                        } else {
                            if (!(term1.value instanceof Double) || !(term2.value instanceof Double))
                                interpreter.error("TypeError", "Expected numbers for multiplication", null);
                            value = (Double) term1.value * ((Double) term2.value);
                        }
                    }
                    case "/" -> {
                        if (term2.value.equals(0.0)){
                            interpreter.error("ZeroDivisionError", "You cannot divide by 0", null);
                        }
                        value = (Double) term1.value / (Double) term2.value;
                    }
                    case "^" ->
                            value = (double) (((Double) term1.value).intValue() ^ ((Double) term2.value).intValue());
                    case "**" -> value = Math.pow((Double) term1.value, (Double) term2.value);
                    case "%" -> value = (Double) term1.value % (Double) term2.value;
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
