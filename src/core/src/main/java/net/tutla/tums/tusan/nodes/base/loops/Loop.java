package net.tutla.tums.tusan.nodes.base.loops;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Statement;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/// Loops are things that go around and come around, similarly a loop here will be repition.
///
/// ## For loop
///
/// This is a basic loop which has 2 types
///
/// - Looping all items/characters in a list/string
///   - `loop all (items|characters) in [expression]`
///   - This will loop all the characters/items in [expression] which is a string or list
/// - Looping a number of times
///   - `loop [expression] times`
///   - It will loop the code [expression] (as a number) times
///
/// You want to access the current item on the loop?
/// - TuSK has a default iter called `loop_item`
/// - If you want to use your on variable for this you can use `as [variable name]` after declaring the loop for example: `loop 10 times as x` and x will be the loop iteration
///
/// <!-- include While -->
///
/// @tums.title Loops
/// @tums.summary How to use loops in tums
public class Loop extends Node {
    private Integer pos;
    private int bodyEnd;
    private int iterations = 0;

    public String as;
    public Object times;

    public Loop(TusanContext ctx){
        super(ctx);
    }

    public Loop create(){
        if (token.type == PrebuiltTusanTokenType.KEYWORD && token.value.equals("all")) {
            String target = interpreter.tokenManager.expectTokenClassic("KEYWORD:items|KEYWORD:characters").value;
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.LOGIC, "in");
            if (target.equals("characters") || target.equals("items")){
                interpreter.tokenManager.nextToken();
                times = new Expression(ctx).create().value;
            }
        } else {
            // loop <expression> times: a number, a variable, a sum...
            Object count = new Expression(ctx).create().value;
            if (interpreter.caughtError) return this;
            if (!(count instanceof Number n)){
                interpreter.error("TypeError", "loop ... times needs a number", null);
                return this;
            }
            times = n.intValue();
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "times");
        }
        if (interpreter.caughtError) return this;
        parseAs();
        loop();
        return this;
    }

    public void loop(){
        pos = interpreter.pos;          // the last token of the header: the body starts after it
        bodyEnd = findBodyEnd();        // this loop's own "end"

        List<Object> values = new ArrayList<>();
        if (times instanceof Integer count){
            for (int i = 0; i < count; i++) values.add((double) i); // a Double, like every other number
        } else if (times instanceof Iterable<?> items) {
            for (Object i : items) values.add(i);
        } else if (times instanceof Object[] items) {
            values.addAll(Arrays.asList(items));
        } else if (times instanceof Map<?, ?> map) {
            values.addAll(map.values());
        } else if (times instanceof String text){
            for (int i = 0; i < text.length(); i++) values.add(String.valueOf(text.charAt(i)));
        } else {
            interpreter.error("TusanError", "Tusan does not support this iterable", null);
        }

        interpreter.loopDepth++;
        try {
            for (Object value : values) {
                loopExecute(new Variable(as, value, null));
                if (interpreter.breaking){
                    interpreter.breaking = false; // break stops this loop only
                    break;
                }
                if (interpreter.end || interpreter.caughtError) break;
            }
        } finally {
            interpreter.loopDepth--;
        }
        // however the body finished (all of it, break, return, or not at all) carry on after its end
        interpreter.pos = bodyEnd;
        interpreter.currentToken = interpreter.tokenManager.get(bodyEnd);
    }

    // The position of the end that closes this loop, counting nested structures on the way
    private int findBodyEnd(){
        interpreter.pos = pos;
        int structures = 0;
        while (true){
            Token nxt = interpreter.tokenManager.nextToken();
            if (nxt == null || nxt.type == PrebuiltTusanTokenType.ENDSCRIPT) return interpreter.pos;
            if (nxt.type == PrebuiltTusanTokenType.ENDSTRUCTURE){
                if (structures == 0) return interpreter.pos;
                structures--;
            } else if (nxt.group == PrebuiltTusanTokenGroup.STRUCTURE){
                structures++;
            }
        }
    }

    public void loopExecute(Variable value){
        iterations++;
        interpreter.data.vars.put(as, value);
        interpreter.pos = pos;
        interpreter.currentToken = interpreter.tokenManager.get(pos);

        while (true){
            interpreter.tokenManager.nextToken();
            if (interpreter.pos >= bodyEnd) return; // reached this loop's end; nested ones are run by Statement
            new Statement(ctx).create();
            if (interpreter.end || interpreter.breaking || interpreter.caughtError) return;
        }
    }

    public void setAs(String val){
        as = val;
        interpreter.data.vars.put(as, null);
    }

    public void parseAs(){
        if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.KEYWORD && interpreter.tokenManager.getNextToken().value.equals("as")){
            interpreter.tokenManager.nextToken();
            Token var = interpreter.tokenManager.nextToken();
            if (var.type == PrebuiltTusanTokenType.IDENTIFIER){
                setAs(var.value);
            }
        } else {
            setAs("loop_item");
        }
    }
}
