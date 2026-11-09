package net.tutla.tums.tums;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;

public class HelloNode extends Node {

    public HelloNode(TusanContext ctx) {
        super(ctx);
    }

    public HelloNode create(){
        
        return this;
    }
}
