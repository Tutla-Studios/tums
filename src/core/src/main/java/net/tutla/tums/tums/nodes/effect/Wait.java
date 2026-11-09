package net.tutla.tums.tums.nodes.effect;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.nodes.expression.Expression;

/// ### wait
/// ```wait [expression]```
///
/// - Expression must be a number
/// - Waits [expression] seconds
public class Wait extends Node {
    public Wait(TusanContext ctx){
        super(ctx);
    }

    public Wait create() {
        try {
            interpreter.tokenManager.nextToken();
            Double time = (Double) new Expression(ctx).create().value;
            Thread.sleep(time.intValue()* 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

}
