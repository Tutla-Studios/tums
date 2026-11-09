package net.tutla.tums.tums.nodes.effect;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.nodes.expression.Expression;

/// ### wait
/// ```wait [expression]```
///
/// - Expression must be a number
/// - Waits [expression] seconds, decimals too: `wait 0.5`
/// - Only this script, or this run of an event, waits: the game and every other event keep running. While the scripts load, a wait holds up the scripts after it
public class Wait extends Node {
    public Wait(TusanContext ctx){
        super(ctx);
    }

    public Wait create() {
        try {
            // the statement loop has already moved past "wait", so the current token starts the expression
            Object time = new Expression(ctx).create().value;
            if (!(time instanceof Number seconds)) {
                interpreter.error("TypeError", "wait needs a number of seconds", null);
                return this;
            }
            // scripts and event runs have threads of their own, so this pauses only this one
            Thread.sleep(Math.round(seconds.doubleValue() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

}
