package net.tutla.tums.tusan;

import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.TokenManager;
import net.tutla.tums.tusan.lang.Tusan;
import net.tutla.tums.tusan.lexer.Token;

public class TusanContext {
    public Interpreter interpreter;
    public TokenManager tokenContext;
    public boolean isFunction;

    public MinecraftEnvironment environment = MinecraftEnvironment.SERVER;

    public TusanContext(Interpreter interpreter){
        this.interpreter = interpreter;
        this.tokenContext = interpreter.tokenManager;
    }

    // helpers

    public Token getCurrentToken(){
        return interpreter.currentToken;
    }

    public int getPos(){
        return interpreter.pos;
    }

    public Token getNextToken(){
        return tokenContext.getNextToken();
    }

    public Token nextToken(){
        return tokenContext.nextToken();
    }

    public Interpreter getInterpreter(){
        return interpreter;
    }

    public Tusan getTusan(){
        return interpreter.getTusan();
    }

    public void setEnvironment(MinecraftEnvironment env){
        this.environment = env;
    }

    public MinecraftEnvironment getEnvironment(){
        return environment;
    }
}
