package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.node.NodeConfig;
import net.tutla.tums.tusan.nodes.effects.Print;
import net.tutla.tums.tusan.nodes.effects.Set;

public class Effect extends Node {
    public Object value;
    public Effect(TusanContext ctx){
        super(ctx);
        NodeConfig conf = new NodeConfig();
        conf.addCategoryToken(PrebuiltTusanTokenType.PRINT, Print.class);
        this.setNodeConfig(conf);
//        if (token.value.equals("print")){
//            interpreter.tokenManager.nextToken();
//            new Print(ctx).create();
//        } else if (token.value.equals("set")) {
//            new Set(ctx).create();
//        } else if (token.value.equals("wait")){
//            //new Wait(token).create();
//        }
    }
    public Effect create(){

        value = null;
        return this;
    }
}
