package net.tutla.tums.tusan.nodes.effects;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class Print extends Node {
    public Print(TusanContext ctx){
        super(ctx);
    }

    public Print create(){
        System.out.println(new Expression(ctx).create().value);
        return this;
    }
}
