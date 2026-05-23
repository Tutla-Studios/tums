package net.tutla.tums.tusan.node;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.TokenType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Node {
    public Interpreter interpreter;
    public TusanContext ctx;
    private NodeConfig nodeConf = new NodeConfig();
    public Token token;

    public Node(TusanContext ctx){
        this.ctx = ctx;
        this.token = ctx.getCurrentToken();
        this.interpreter = ctx.interpreter;
    }

    public NodeConfig getNodeConfig(){
        return nodeConf;
    }

    public void setNodeConfig(NodeConfig conf){
        nodeConf = conf;
    }

    public void verify(){

    }

    public Node run(){
        boolean runCreate = true;
        if (nodeConf.isCategory()){
            nodeConf.getEffectiveTokens().getTokenMap().forEach((ttype, node) -> {
                if (ttype == ctx.getCurrentToken().type){
                    Node newNode = Node.instantiateNode(node, ctx);
                    if(!newNode.getNodeConfig().isDontSendNext()) {
                        ctx.nextToken();
                    }
                    newNode.run();
                }
            });

            runCreate = false;
        }

        if (runCreate && !nodeConf.isUseCustom()){
            create();
        }

        return this;
    }

    public Object create(){
        return this;
    }

    public static <T> T instantiateNode(Class<T> clazz, Object... args) { // for creating nodes with custom context.
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.getParameterCount() != args.length) continue;
            boolean matches = true;
            for (int i = 0; i < args.length; i++) {
                if (!ctor.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                ctor.setAccessible(true);
                try {
                    return clazz.cast(ctor.newInstance(args));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("No matching constructor found in " + clazz.getName());
    }
}
