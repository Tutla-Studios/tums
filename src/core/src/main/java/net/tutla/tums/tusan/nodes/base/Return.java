package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class Return extends Node {
    public Return(TusanContext ctx){
        super(ctx);
    }

    public Return create(){
        this.interpreter.returned = new Expression(ctx).create().value;
        this.interpreter.end = true;
        return this;
    }
}
