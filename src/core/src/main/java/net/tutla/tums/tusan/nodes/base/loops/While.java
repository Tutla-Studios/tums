package net.tutla.tums.tusan.nodes.base.loops;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Statement;
import net.tutla.tums.tusan.nodes.expression.Expression;

/// ## While Loop
///
/// ```lua
/// while [condition] do
/// ...
/// end
/// ```
///
/// It will execute the code below it if the condition is true infinitly until the condition becomes false
public class While extends Node {
    public int currentPos;
    public int endPos;
    public boolean cond;

    public While(TusanContext ctx){
        super(ctx);
    }

    public While create(){
        currentPos = interpreter.pos;
        endPos = currentPos;
        check();

        // this method is inefficient but does it look like i fucking care
        boolean fned = false;
        int structures = 0;
        while (!fned){
            Token nxt = interpreter.tokenManager.nextToken();
            if (nxt.type== PrebuiltTusanTokenType.ENDSTRUCTURE){
                if (structures == 0){
                    endPos = interpreter.pos;
                    fned = true;
                } else {
                    structures--;
                }
            } else if (nxt.group == PrebuiltTusanTokenGroup.STRUCTURE) {
                structures++;
            }
        }
        interpreter.pos = currentPos;
        check();

        interpreter.loopDepth++;
        try {
            while (cond && !interpreter.end && !interpreter.caughtError){
                interpreter.tokenManager.nextToken();
                if (interpreter.pos >= endPos){
                    check(); // this while's own end: nested structures consume theirs in Statement
                } else {
                    new Statement(ctx).create();
                    if (interpreter.breaking){
                        interpreter.breaking = false; // break stops this loop only
                        break;
                    }
                    // a return (interpreter.end) or an error leaves the while condition alone: stop here
                }
            }
        } finally {
            interpreter.loopDepth--;
        }
        interpreter.pos = endPos;
        interpreter.currentToken = interpreter.tokenManager.get(endPos);

        return this;
    }

    private void check(){
        interpreter.pos = currentPos;
        interpreter.currentToken = interpreter.tokenManager.get(currentPos);
        Expression condition = new Expression(ctx).create();
        interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "do");
        if (!(condition.value instanceof Boolean e)){
            interpreter.error("TypeError", "Evaluated expression must return a boolean output", null);
            return;
        }
        cond = e;
    }
}
