package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Expression extends Node {
    public Object value;

    public Expression(TusanContext ctx){
        super(ctx);
    }

    public Expression create(){

        boolean opposite = false;
        if (token.type == PrebuiltTusanTokenType.LOGIC && "not".equals(token.value)){
            opposite = true;
            interpreter.tokenManager.nextToken();
        }

        Term term1 = new Term(ctx).create();
        if (Arrays.asList(PrebuiltTusanTokenType.OPERATOR, PrebuiltTusanTokenType.COMPARISON).contains(interpreter.tokenManager.getNextToken().type)){
            Token op = interpreter.tokenManager.nextToken();
            interpreter.tokenManager.nextToken();
            Expression term2 = new Expression(ctx).create();
            if (op.type == PrebuiltTusanTokenType.OPERATOR){
                if (Objects.equals(op.value, "+")){
                    if (term1.value instanceof String && term2.value instanceof String){
                        value = (String) term1.value + (String) term2.value;
                    } else if (term1.value instanceof Number && term2.value instanceof Number){
                        value = (Double) term1.value + (Double) term2.value;
                    } else {
                        interpreter.error("TypeError","Invalid types provided to add", List.of("You can only add string+string and number+number"));
                    }
                } else if (Objects.equals(op.value, "-")) {
                    if (term1.value instanceof Number && term2.value instanceof Number){
                        value = (Double) term1.value - (Double) term2.value;
                    } else {
                        interpreter.error("TypeError","Invalid types provided to subtract", null);
                    }

                }
            } else if (op.type == PrebuiltTusanTokenType.COMPARISON){
                value = compare(op, term1, term2);
            }
        } else {
            value = term1.value;
        }

        while (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.LOGIC){
            String operator = interpreter.tokenManager.getNextToken().value;
            if (!List.of("and","&&","or","||","contains","in").contains(operator)) break;
            interpreter.tokenManager.nextToken();
            interpreter.tokenManager.nextToken();
            Expression right = new Expression(ctx).create();

            switch (operator) {
                case "and", "&&" -> {
                    if (!(value instanceof Boolean) || !(right.value instanceof Boolean))
                        interpreter.error("TypeError", "Expected boolean for 'and'", null);
                    value = (Boolean) value && (Boolean) right.value;
                }
                case "or", "||" -> {
                    if (!(value instanceof Boolean) || !(right.value instanceof Boolean))
                        interpreter.error("TypeError", "Expected boolean for 'or'", null);
                    value = (Boolean) value || (Boolean) right.value;
                }
                case "contains" -> {
                    if (!(value instanceof List))
                        interpreter.error("TypeError", "Expected list for 'contains'", null);
                    value = ((List<?>) value).contains(right.value);
                }
                case "in" -> {
                    if (!(right.value instanceof List))
                        interpreter.error("TypeError", "Expected list for 'in'", null);
                    value = ((List<?>) right.value).contains(value);
                }
            }
        }

        if (opposite){
            if (!(value instanceof Boolean))
                interpreter.error("TypeError", "Expected boolean after 'not'", null);
            value = !(Boolean) value;
        }


        return this;
    }

    private Object compare(Token op, Term term1, Expression term2) {
        switch (op.value) {
            case "==", "is" -> value = Objects.equals(term1.value, term2.value);
            case "!="       -> value = !Objects.equals(term1.value, term2.value);
            default -> {
                if (!(term1.value instanceof Number) || !(term2.value instanceof Number))
                    interpreter.error("TypeError", "Expected numbers for comparison " + op.value, null);
                double a = ((Number) term1.value).doubleValue();
                double b = ((Number) term2.value).doubleValue();
                switch (op.value) {
                    case "<"           -> value = a <  b;
                    case ">"           -> value = a >  b;
                    case "<="          -> value = a <= b;
                    case ">="          -> value = a >= b;
                    case null, default -> interpreter.error("InvalidComparison", "Invalid comparison " + op.value, null);
                }
            }
        }
        return value;
    }
}
