package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class If extends Node {
    public Expression condition;
    public Boolean end = false;
    public Boolean success = false;
    public Integer structures = 0;

    private Boolean runIf;
    private Boolean runElse;

    public If(TusanContext ctx){
        super(ctx);
    }

    public If create(){
        this.condition = new Expression(ctx).create();
        interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "then");

        if (!(condition.value instanceof Boolean runIf)){
            interpreter.error("TypeError", "Evaluated expression must return a boolean output", null);
            return this;
        }
        boolean runElse = false;

        while (!end){
            Token nxt = interpreter.tokenManager.getNextToken();
            if (nxt.type== PrebuiltTusanTokenType.ENDSTRUCTURE){
                if (structures == 0){
                    interpreter.tokenManager.nextToken();
                    end = true;
                    break;
                } else {
                    structures--;
                    interpreter.tokenManager.nextToken();
                }
            } else if ((nxt.type == PrebuiltTusanTokenType.KEYWORD && nxt.value.equals("elseif")) && structures == 0) {
                interpreter.tokenManager.nextToken();
                if (!runIf){
                    interpreter.tokenManager.nextToken();
                    condition = new Expression(ctx).create();
                    if ((boolean) condition.value){
                        interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "then");
                        runIf = true;
                        runElse = false;
                        success = true;
                    } else {
                        runIf = false;
                        runElse = false;
                    }
                } else {
                    success = true;
                    runIf = false;
                    runElse = false;
                }
            } else if((nxt.type == PrebuiltTusanTokenType.KEYWORD && nxt.value.equals("else")) && structures == 0){
                interpreter.tokenManager.nextToken();
                if (!runIf && success == false){
                    runElse = true;
                } else {
                    runElse = false;
                    runIf = false;
                    success = true;
                }
            } else {
                Token e = interpreter.tokenManager.nextToken();

                if (runIf || runElse){
                    new Statement(ctx).create();
                } else {
                    if (nxt.type== PrebuiltTusanTokenType.STRUCTURE){
                        structures++;
                    }
                }
            }
        }


        return this;
    }
}
