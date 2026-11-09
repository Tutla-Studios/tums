package net.tutla.tums.tusan.node;

import net.tutla.tums.tusan.lexer.TokenType;

import java.util.HashMap;

public class Token2NodeMap {
    private final HashMap<TokenType, Class<? extends Node>> tokenMap = new HashMap<>();

    public void add(TokenType tokenType, Class<? extends Node> node){
        tokenMap.put(tokenType, node);
    }

    public HashMap<TokenType, Class<? extends Node>> getTokenMap() {
        return tokenMap;
    }
}
