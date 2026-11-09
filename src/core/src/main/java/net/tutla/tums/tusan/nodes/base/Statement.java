package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Statement extends Node {
    private final List<PrebuiltTusanTokenType> validTypes = Arrays.asList(PrebuiltTusanTokenType.STRUCTURE, PrebuiltTusanTokenType.KEYWORD, PrebuiltTusanTokenType.IDENTIFIER, PrebuiltTusanTokenType.EFFECT, PrebuiltTusanTokenType.LEFT_CURLY, PrebuiltTusanTokenType.NUMBER, PrebuiltTusanTokenType.STRING); // TODO: get from util or lang
    public Statement(TusanContext ctx){
        super(ctx);
    }
    public Object create(){

        Object value = false;
        if (interpreter.end){
            return false;
        }

        if (token.type == PrebuiltTusanTokenType.LEFT_CURLY){ // TODO: make this an overridable rule
            interpreter.tokenManager.nextToken();
            new Statement(ctx).create();
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_CURLY);
            return value;
        }
        if (token.group == PrebuiltTusanTokenGroup.STRUCTURE || ctx.getTusan().getStatementTokenNodeMap().getTokenMap().containsKey(token.type) || ctx.getTusan().getStatementTokenGroupNodeMap().getTokenMap().containsKey(token.group)){
            AtomicBoolean execed = new AtomicBoolean(false);
            ctx.getTusan().getStatementTokenNodeMap().getTokenMap()
                    .forEach((tok, node) -> {
                        if (token.type == tok){
                            // TODO: full node execution based on config
                            instantiateNode(node, ctx);
                            execed.set(true);
                        }
                    });
            ctx.getTusan().getStatementTokenGroupNodeMap().getTokenMap()
                    .forEach((tokGrp, node) -> {
                        if (token.group == tokGrp){
                            // TODO: full node execution based on config
                            instantiateNode(node, ctx);
                            execed.set(true);
                        }
                    });
            if (!execed.get()){
                interpreter.error("UnexpectedToken", "Structure " + token.type.name() + ":" + token.value + " has no definition", null);
            }
        } else if (validTypes.contains(token.type) || token.group == PrebuiltTusanTokenGroup.STRUCTURE || token.group == PrebuiltTusanTokenGroup.EFFECT){
            if (token.type == PrebuiltTusanTokenType.EFFECT || token.group == PrebuiltTusanTokenGroup.EFFECT){ // TODO: Remove type and make it only group
                value = new Effect(ctx).run();
            } else if (Arrays.asList(PrebuiltTusanTokenType.IDENTIFIER, PrebuiltTusanTokenType.LEFT_CURLY, PrebuiltTusanTokenType.NUMBER, PrebuiltTusanTokenType.STRING, PrebuiltTusanTokenType.KEYWORD).contains(token.type)){
                value = new Expression(ctx).create();
            } else if (token.type ==  PrebuiltTusanTokenType.BREAKSTRUCTURE){
                value = new Return(ctx).create();
            } else {
                interpreter.error("UnexpectedToken", "Expected valid statement got "+token.type.name()+":"+token.value, null);
            }
        } else if (token.type == PrebuiltTusanTokenType.BREAKSTRUCTURE && token.value.equals("return")){
            new Return(ctx).create();
        } else {
            interpreter.error("UnexpectedToken", "Expected valid statement got "+token.type.name()+":"+token.value, null);
        }
        return value;
    }

    private void instantiateNode(Class<? extends Node> node, TusanContext ctx){
        Node newNode = Node.instantiateNode(node, ctx);
        if(!newNode.getNodeConfig().isDontSendNext()) {
            ctx.nextToken();
        }
        newNode.run();
    }
}
