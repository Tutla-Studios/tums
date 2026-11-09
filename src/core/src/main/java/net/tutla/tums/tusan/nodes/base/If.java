package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

/// ```lua
/// if [condition] then
/// ...
/// elseif [conditon] then
/// ...
/// else
/// ...
/// end
/// ```
///
/// - The Statement checks if the [condition](https://wiki.tutla.net/tums/scripting/basic/expressions) is true, if it is  it will then execute the code after then, if it is false then the Interpreter skips it and goes to `elseif`, `else` or `end`.
/// - Else & Elseif are not necessary, they are fall backs to the if statement. If the if statement is false it will then skip and execute all the elseif statements ( there can be infinite elseif statements ) until one is right or it could reach an else if an elseif is not present and it will execute it.
/// - `end` Closes the structure
///
/// @tums.title If Statements
/// @tums.summary How to use if statements in tums
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
