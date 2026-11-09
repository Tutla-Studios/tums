package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/// Expressions are a bit complicated but once you understand them it will all be a piece of cake. There are 4 types of Expressions (its not exactly types, they form a tree going from root to shoot below) below:
///
/// - [factor]
/// - [term]
/// - [expression]
/// - [condition]
///
/// <!-- include Factor -->
///
/// <!-- include Term -->
///
/// ## Expression
///
/// Expressions are used everywhere in the language, they could be a:
///
/// - [term]
/// - [term] + [expression]
/// - [term] - [expression]
/// - [term] COMAPARISON [expression]
///   - COMPARISION could be is, >, < =, ==, !=
///   - returns a boolean (true/false)
///
/// ## Condition
///
/// Conditions return true or false depending on what you give them
///
/// - `not` [condition]: returns the opposite of the condition, for example if it was true it will return false
/// - [expression] where it is a BOOL (true/false)
/// - [expression] LOGIC [expression]
///   - LOGIC can be and, or, contains, in
///   - [expression] `and` [expression]: returns true if both are true
///   - [expression] `or` [expression]: returns true if either are true
///   - [expression] contains [expression]: returns true if expression2 is in expression1
///   - [expression] in [expression]: returns true if expression1 is in expression2
///
/// @tums.title Expressions
/// @tums.summary How to use expressions in tums
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
                    } else if (term1.value instanceof Number a && term2.value instanceof Number b){
                        value = a.doubleValue() + b.doubleValue();
                    } else {
                        interpreter.error("TypeError","Invalid types provided to add", List.of("You can only add string+string and number+number"));
                    }
                } else if (Objects.equals(op.value, "-")) {
                    if (term1.value instanceof Number a && term2.value instanceof Number b){
                        value = a.doubleValue() - b.doubleValue();
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

    // 0 (an Integer loop counter) and 0.0 (a number written in a script) are the same number
    private static boolean same(Object a, Object b) {
        if (a instanceof Number x && b instanceof Number y) return x.doubleValue() == y.doubleValue();
        return Objects.equals(a, b);
    }

    private Object compare(Token op, Term term1, Expression term2) {
        switch (op.value) {
            case "==", "is" -> value = same(term1.value, term2.value);
            case "!="       -> value = !same(term1.value, term2.value);
            default -> {
                if (!(term1.value instanceof Number) || !(term2.value instanceof Number)) {
                    interpreter.error("TypeError", "Expected numbers for comparison " + op.value, null);
                    return value = null;
                }
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
