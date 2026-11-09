package net.tutla.tums.tusan.node;

import net.tutla.tums.tusan.lexer.TokenType;

public class NodeConfig {
    private TokenType END;

    private boolean category = false;
    private boolean forceUseCustom = false; // for ppl who want their own node implementation on top of the template
    private boolean dontSendNext = false;

    private final Token2NodeMap effectiveTokens = new Token2NodeMap();

    private SyntaxPattern syntax;

    public NodeConfig(){
        // TODO: add various templates
    }

    public NodeConfig addCategoryToken(TokenType e,  Class<? extends Node> node){ // TODO: TokenType should be SyntaxPattern
        category = true;
        effectiveTokens.add(e, node);
        return this;
    }

    public Token2NodeMap getEffectiveTokens(){
        return effectiveTokens;
    }

    public boolean isCategory(){
        return category;
    }

    public NodeConfig setForceUseCustom(boolean custom){
        forceUseCustom = true;
        return this;
    }

    public boolean isUseCustom(){
        return forceUseCustom;
    }

    public boolean isDontSendNext() {
        return dontSendNext;
    }

    public NodeConfig setDontSendNextToken(boolean dontSendNext) {
        this.dontSendNext = dontSendNext;
        return this;
    }
}
