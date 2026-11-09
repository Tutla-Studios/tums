package net.tutla.tums.tusan.lexer;

import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;

public class LexerRule {
    public final TokenType type;
    public final String regex;
    public TokenGroup group= PrebuiltTusanTokenGroup.NONE;

    public LexerRule(TokenType type, String regex) {
        this.type = type;
        this.regex = regex;
    }

    public LexerRule setGroup(TokenGroup e) {
        this.group = e;
        return this;
    }

    public TokenType type(){
        return type;
    }

    public String regex(){
        return regex;
    }
}
