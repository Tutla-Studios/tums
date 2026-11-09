package net.tutla.tums.tusan.nodes.effects;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Name;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.List;
import java.util.Map;

public class Set extends Node {
    private Utils utils = new Utils();
    public Set(TusanContext ctx){
        super(ctx);
    }

    public Set create(){
        Token name = interpreter.tokenManager.nextToken();
        Object num = utils.isOrdinal(name);
        if (num != null){ // TODO: this code is fucked so like ill fix it later
            int n = ((Double) num).intValue();
            n--;
            interpreter.tokenManager.expectTokenClassic("KEYWORD:item|KEYWORD:character");
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.LOGIC,"in");
            Object val;
            if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.IDENTIFIER){
                interpreter.tokenManager.nextToken();
                Name e = new Name(ctx).create();
                val = Name.name;
            } else {
                interpreter.tokenManager.nextToken();
                Object e = new Expression(ctx);
                val = e;
            }
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD,"to");

            interpreter.tokenManager.nextToken();
            Object exprValue = new Expression(ctx).create().value;

            if (val instanceof List) {
                ((List<Object>) val).set(n, exprValue);
            } else if (val instanceof Map) {
                ((Map<Object, Object>) val).put(n, exprValue);
            }
        } else {
            Name n = new Name(ctx).create();
            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD,"to");
            interpreter.tokenManager.nextToken();
            Object val = new Expression(ctx).create().value;
            if (val instanceof Variable){
                ((Variable) val).name = n.name;
                n.location.put(n.name, new Variable(n.name, val, null));
                interpreter.data.vars.put("it",new Variable("it", val, null));
                interpreter.data.vars.put("this", new Variable("this", val, null));
            } else {
                n.location.put(n.name, new Variable(n.name, val, null));
                interpreter.data.vars.put("it",new Variable("it", val, null));
                interpreter.data.vars.put("this", new Variable("this", val, null));
            }
        }
        return this;
    }
}
