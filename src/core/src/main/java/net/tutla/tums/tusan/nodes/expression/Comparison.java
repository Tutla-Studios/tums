package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;

import java.util.List;
import java.util.Objects;

/// `a == b`, `a is b`, `a != b`, `a < b`, `a > b`, `a <= b`, `a >= b`, `list contains a` and `a in list`.
/// The maths on either side is worked out first, so `n + 1 == 3` compares `n + 1` with `3`. Left to right.
public class Comparison extends Node {
    /// The result
    public Object value;

    /// Creates the node at the first token of its left side
    public Comparison(TusanContext ctx){
        super(ctx);
    }

    /// Reads one or more `Sum`s joined by comparisons, `contains` or `in`
    public Comparison create(){
        value = new Sum(ctx).create().value;
        while (true){
            Token op = interpreter.tokenManager.getNextToken();
            boolean comparison = op.type == PrebuiltTusanTokenType.COMPARISON;
            boolean membership = op.type == PrebuiltTusanTokenType.LOGIC && (op.value.equals("contains") || op.value.equals("in"));
            if (!comparison && !membership) return this;
            interpreter.tokenManager.nextToken(); // the operator
            interpreter.tokenManager.nextToken(); // the first token of the right side
            Object right = new Sum(ctx).create().value;
            value = comparison ? compare(op.value, value, right) : membership(op.value, value, right);
        }
    }

    private Object membership(String op, Object left, Object right){
        Object container = op.equals("contains") ? left : right;
        Object item = op.equals("contains") ? right : left;
        if (!(container instanceof List<?> list)){
            interpreter.error("TypeError", "Expected list for '" + op + "'", null);
            return null;
        }
        return list.contains(item);
    }

    // 0 (an Integer loop counter) and 0.0 (a number written in a script) are the same number
    private static boolean same(Object a, Object b){
        if (a instanceof Number x && b instanceof Number y) return x.doubleValue() == y.doubleValue();
        return Objects.equals(a, b);
    }

    private Object compare(String op, Object left, Object right){
        if (op.equals("==") || op.equals("is")) return same(left, right);
        if (op.equals("!=")) return !same(left, right);
        if (!(left instanceof Number a) || !(right instanceof Number b)){
            interpreter.error("TypeError", "Expected numbers for comparison " + op, null);
            return null;
        }
        return switch (op) {
            case "<"  -> a.doubleValue() <  b.doubleValue();
            case ">"  -> a.doubleValue() >  b.doubleValue();
            case "<=" -> a.doubleValue() <= b.doubleValue();
            case ">=" -> a.doubleValue() >= b.doubleValue();
            default -> {
                interpreter.error("InvalidComparison", "Invalid comparison " + op, null);
                yield null;
            }
        };
    }
}
