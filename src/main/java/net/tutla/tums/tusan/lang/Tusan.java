package net.tutla.tums.tusan.lang;

import net.tutla.tums.tusan.lexer.util.WordedPattern;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.*;
import net.tutla.tums.tusan.node.Token2NodeMap;
import net.tutla.tums.tusan.nodes.base.Statement;
import net.tutla.tums.tusan.nodes.base.If;
import net.tutla.tums.tusan.nodes.base.Return;
import net.tutla.tums.tusan.nodes.base.function.FunctionNode;
import net.tutla.tums.tusan.nodes.base.loops.Loop;
import net.tutla.tums.tusan.nodes.base.loops.While;

import java.util.ArrayList;
import java.util.List;

public class Tusan {
    private final TusanLanguageRules lang = new TusanLanguageRules();
    private final List<Class<? extends Node>> nodes = new ArrayList<>();

    private final Token2NodeMap statementNodeMap = new Token2NodeMap();
    private Class<? extends Node> statementNode = Statement.class;

    private static final List<LexerRule> DEFAULT_LEXER_RULES = List.of( // ORDER MATTERS!
            new LexerRule(PrebuiltTusanTokenType.PROPERTY, "'s(?!['\"])"),
            new LexerRule(PrebuiltTusanTokenType.STRING, "\"(?:[^\"\\\\]|\\\\.)*\"|'(?:[^'\\\\]|\\\\.)*'"),
            new LexerRule(PrebuiltTusanTokenType.NUMBER, "\\d+(\\.\\d+)?"),
            new LexerRule(PrebuiltTusanTokenType.BOOL, WordedPattern.makeWordedPattern(List.of("true", "false")) ),
            new LexerRule(PrebuiltTusanTokenType.NOTHING, WordedPattern.makeOneWordPattern("nothing")),

            new LexerRule(PrebuiltTusanTokenType.OPERATOR, "[+\\-*/%]"),
            new LexerRule(PrebuiltTusanTokenType.LOGIC, "(?:\\b(?:and|or|not|contains|in)\\b|\\|\\||&&)"),
            new LexerRule(PrebuiltTusanTokenType.COMPARISON, ">=|<=|==|!=|>|<|\\bis\\b"),
            new LexerRule(PrebuiltTusanTokenType.EQUAL, "="),

            new LexerRule(PrebuiltTusanTokenType.LEFT_CURLY, WordedPattern.makeSpecialCharacterPattern('{')),
            new LexerRule(PrebuiltTusanTokenType.RIGHT_CURLY, WordedPattern.makeSpecialCharacterPattern('}')),
            new LexerRule(PrebuiltTusanTokenType.LEFT_PAR, WordedPattern.makeSpecialCharacterPattern('(')),
            new LexerRule(PrebuiltTusanTokenType.RIGHT_PAR, WordedPattern.makeSpecialCharacterPattern(')')),
            new LexerRule(PrebuiltTusanTokenType.LEFT_SQUARE, WordedPattern.makeSpecialCharacterPattern('[')),
            new LexerRule(PrebuiltTusanTokenType.RIGHT_SQUARE, WordedPattern.makeSpecialCharacterPattern(']')),
            new LexerRule(PrebuiltTusanTokenType.SEMICOLON, ";"),
            new LexerRule(PrebuiltTusanTokenType.COLON, ":"),
            new LexerRule(PrebuiltTusanTokenType.COMMA, ","),

            new LexerRule(PrebuiltTusanTokenType.IF, WordedPattern.makeOneWordPattern("if"))
                    .setGroup(PrebuiltTusanTokenGroup.STRUCTURE),
            new LexerRule(PrebuiltTusanTokenType.WHILE, WordedPattern.makeOneWordPattern("while"))
                    .setGroup(PrebuiltTusanTokenGroup.STRUCTURE),
            new LexerRule(PrebuiltTusanTokenType.LOOP, WordedPattern.makeOneWordPattern("loop"))
                    .setGroup(PrebuiltTusanTokenGroup.STRUCTURE),
            new LexerRule(PrebuiltTusanTokenType.FUNCTION, WordedPattern.makeOneWordPattern("function"))
                    .setGroup(PrebuiltTusanTokenGroup.STRUCTURE),

            new LexerRule(PrebuiltTusanTokenType.PRINT, WordedPattern.makeOneWordPattern("print")).setGroup(PrebuiltTusanTokenGroup.EFFECT)
    );

    public Tusan(){

    }

    public static Tusan tusan(){
        Tusan tsn = new Tusan();
        tsn.registerAllDefaultRules();
        tsn.registerAllDefaultNodes();
        return tsn;
    }

    ////////////////// registering NODES
    public void registerNode(Class<? extends Node> node){
        // TODO: Verify if node already exists
        // TODO: Nodes should be able to be linked to: 1) the main statement loop and its direct descendants (Effects/Structures) 2) Expressions/Terms/Factors
        nodes.add(node);
    }

    public List<Class<? extends Node>> getNodes(){
        return nodes;
    }

    public void linkNodeToStatement(TokenType tokenType, Class<? extends Node> node){ // TODO: links for category nodes aswell
        statementNodeMap.add(tokenType, node);
    }

    public Token2NodeMap getStatementNodeMap(){
        return statementNodeMap;
    }

    /// /////////////////////////////

    public void registerLexerRule(LexerRule rule){
        lang.registerRule(rule);
    }

    private void registerAllDefaultRules(){
        DEFAULT_LEXER_RULES.forEach(this::registerLexerRule);
    }

    private void registerAllDefaultNodes(){
        linkNodeToStatement(PrebuiltTusanTokenType.IF, If.class);
        linkNodeToStatement(PrebuiltTusanTokenType.WHILE, While.class);
        linkNodeToStatement(PrebuiltTusanTokenType.LOOP, Loop.class);
        linkNodeToStatement(PrebuiltTusanTokenType.FUNCTION, FunctionNode.class);
    }

    /// //////////////////////////////

    public void compile(TusanContext ctx){
        if (!ctx.isFunction){
            System.out.println("================ OUTPUT ===============");
        }
        while (ctx.getPos()<= ctx.tokenContext.length()-1){
            Token currentToken = ctx.getCurrentToken();
            if (ctx.interpreter.end){
                return;
            }
            if (currentToken.type == PrebuiltTusanTokenType.ENDSCRIPT){ // how did you get here?
                return;
            } else if (currentToken.type == PrebuiltTusanTokenType.BREAKSTRUCTURE && currentToken.value.equals("return")){
                ctx.tokenContext.nextToken();
                new Return(ctx).create();
            } else {
                Node stmtNode = Node.instantiateNode(statementNode, ctx);
                stmtNode.create();
            }
            if (ctx.tokenContext.getNextToken() == null){
                ctx.interpreter.meetEnd();
            } else {
                Token e = ctx.tokenContext.nextToken();
                if (e.type == PrebuiltTusanTokenType.ENDSCRIPT){
                    ctx.interpreter.meetEnd();
                }
            }

        }
        ctx.interpreter.meetEnd();
    }

    public TusanLanguageRules getLang(){
        return lang;
    }

    public Class<? extends Node> getStatementNode() {
        return statementNode;
    }

    public void setStatementNode(Class<? extends Node> statementNode) {
        this.statementNode = statementNode;
    }
}
