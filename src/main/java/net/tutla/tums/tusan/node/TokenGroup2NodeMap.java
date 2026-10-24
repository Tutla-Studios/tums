package net.tutla.tums.tusan.node;

import net.tutla.tums.tusan.lexer.TokenGroup;
import net.tutla.tums.tusan.lexer.TokenType;

import java.util.HashMap;

public class TokenGroup2NodeMap {
    private final HashMap<TokenGroup, Class<? extends Node>> tokenMap = new HashMap<>();

    public void add(TokenGroup tokenTypeGroup, Class<? extends Node> node){
        tokenMap.put(tokenTypeGroup, node);
    }

    public HashMap<TokenGroup, Class<? extends Node>> getTokenMap() {
        return tokenMap;
    }
}
