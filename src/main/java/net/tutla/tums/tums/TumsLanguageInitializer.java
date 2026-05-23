package net.tutla.tums.tums;

import net.tutla.tums.TumsAPI;
import net.tutla.tums.tums.nodes.structure.On;
import net.tutla.tums.tums.nodes.effect.Wait;
import net.tutla.tums.tusan.lexer.LexerRule;
import net.tutla.tums.tusan.lexer.util.WordedPattern;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

public class TumsLanguageInitializer {
    public static void init(){
        TumsAPI.getGlobalTusan().registerLexerRule(
                new LexerRule(PrebuiltTusanTokenType.ON, WordedPattern.makeOneWordPattern("on"))
                        .setGroup(PrebuiltTusanTokenGroup.STRUCTURE));
        TumsAPI.getGlobalTusan().registerLexerRule(
                new LexerRule(TumsTokenType.WAIT, WordedPattern.makeOneWordPattern("wait"))
                        .setGroup(PrebuiltTusanTokenGroup.EFFECT));

        TumsAPI.getGlobalTusan().linkNodeToStatement(PrebuiltTusanTokenType.ON, On.class);
        TumsAPI.getGlobalTusan().linkNodeToStatement(TumsTokenType.WAIT, Wait.class);


        TumsAPI.getGlobalTusan().registerLexerRule(new LexerRule(TumsTokenType.HELLO, WordedPattern.makeOneWordPattern("hello")));
        TumsAPI.getGlobalTusan().registerNode(HelloNode.class);
    }
}

// emptiness....