package net.tutla.tums.tusan.nodes.base.loops;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Statement;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.Arrays;

public class Loop extends Node {
    private Integer pos;
    private Boolean end = false;
    private Boolean run = true;

    public String as;
    public Object times;

    public Loop(TusanContext ctx){
        super(ctx);
    }

    public Loop create(){
        if (token.type == PrebuiltTusanTokenType.NUMBER){
            times = ((Double) new Expression(ctx).create().value).intValue();
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "times");
        } else if (token.type == PrebuiltTusanTokenType.KEYWORD && token.value.equals("all")) {
            String target = interpreter.tokenManager.expectTokenClassic("KEYWORD:items|KEYWORD:characters").value;
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.LOGIC, "in");
            if (target.equals("characters") || target.equals("items")){
                interpreter.tokenManager.nextToken();
                times = new Expression(ctx).create().value;
            }
        } else {
            interpreter.error("TusanError", "Tusan does not support this iterable", Arrays.asList("This is caused due to the Tusan API limitations on the parent language. To fix it use the main branch (Python) or reimplement it."));
        }
        parseAs();
        loop();
        return this;
    }

    public void loop(){
        pos = interpreter.pos;
        Boolean run = true;

        if (times instanceof Integer){
            for (int i = 0; i <= ((int) times); i++) {
                loopExecute(new Variable(as, i, null));
            }
        } else {
            if (times instanceof Iterable) {
                for (Object i : (Iterable<?>) times) {
                    loopExecute(new Variable(as, i, null));
                }
            } else if (times instanceof Object[]) {
                for (Object i : (Object[]) times) {
                    loopExecute(new Variable(as, i, null));
                }
            } else if (times instanceof String){
                for (int i = 0; i < ((String) times).length(); i++) {
                    char c = ((String) times).charAt(i);
                    loopExecute(new Variable(as, i, null));
                }
            }else {
                interpreter.error("TusanError", "Tusan does not support this iterable", null);
            }
        }
    }

    public void loopExecute(Variable value){
        interpreter.data.vars.put(as, value);
        interpreter.pos = pos;
        boolean endBlock = false;

        while (!endBlock){
            Token nxt = interpreter.tokenManager.nextToken();
            if (nxt.type == PrebuiltTusanTokenType.ENDSTRUCTURE) {
                endBlock = true;
            } else if (nxt.type == PrebuiltTusanTokenType.BREAKSTRUCTURE){ // broken bcuz it won't work inside structures
                run = false;
            } else {
                if (run){
                    new Statement(ctx).create();
                }
            }
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
