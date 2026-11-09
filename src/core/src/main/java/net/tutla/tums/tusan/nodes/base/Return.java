package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class Return extends Node {
    public Return(TusanContext ctx){
        super(ctx);
    }

    public Return create(){
        // the current token is "return": a value follows unless the block or script ends right here
        Token next = interpreter.tokenManager.getNextToken();
        if (next == null || next.type == PrebuiltTusanTokenType.ENDSTRUCTURE || next.type == PrebuiltTusanTokenType.ENDSCRIPT){
            this.interpreter.returned = null;
        } else {
            interpreter.tokenManager.nextToken();
            this.interpreter.returned = new Expression(ctx).create().value;
        }
        this.interpreter.end = true;
        return this;
    }
}
