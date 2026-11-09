package net.tutla.tums.tums.nodes.structure;

import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.TokenManager;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.NodeConfig;

import java.util.List;

public class On extends Node {
    private Integer structures = 0;

    public On(TusanContext ctx){
        super(ctx);
        setNodeConfig(
                new NodeConfig()
                        .setDontSendNextToken(true)
        );
    }

    public On create(){
        String eventOriginal = interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.EVENT).value.toUpperCase();

        if (TumsAPI.getEventMappings().contains(eventOriginal)){
            List<Token> tokens = Utils.collectInnerTokens(interpreter.tokenManager);
            tokens.add(new Token(PrebuiltTusanTokenType.ENDSCRIPT, "event", interpreter));

            Interpreter intr = new Interpreter(ctx.getTusan());
            TokenManager e = new TokenManager(intr);
            e.setTokens(tokens);
            intr.setup(interpreter.data,e,
                    null, null, interpreter.getCompileEnvironment());
            ((List) interpreter.data.events.get(eventOriginal)).add(intr);
        } else {
            interpreter.error("NoEventImplementation", "There seems to be no implementation to "+eventOriginal, null);
        }
        return this;
    }
}
