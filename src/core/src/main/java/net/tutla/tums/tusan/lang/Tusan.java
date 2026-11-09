package net.tutla.tums.tusan.lang;

import net.tutla.tums.tusan.lexer.util.WordedPattern;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.lexer.*;
import net.tutla.tums.tusan.node.Token2NodeMap;
import net.tutla.tums.tusan.node.TokenGroup2NodeMap;
import net.tutla.tums.tusan.nodes.base.Statement;
import net.tutla.tums.tusan.nodes.base.If;
import net.tutla.tums.tusan.nodes.base.Return;
import net.tutla.tums.tusan.nodes.base.function.FunctionNode;
import net.tutla.tums.tusan.nodes.base.loops.Loop;
import net.tutla.tums.tusan.nodes.base.loops.While;

import java.util.ArrayList;
import java.util.List;

/// It's very simple, you have two options in creating a language using Tusan:
/// - Using the Tusan defaults and extend them
/// - Creating a new language from scratch
///
/// ## Creating from defaults
///
/// By using the defaults you will already have access to:
/// - Expression Evaluators
/// - Prebuilt functions
/// - Prebuilt types
/// - Prebuilt effects and structures
/// - Structure definitions
///
/// To get started create one like this:
/// ```java
/// public class YourModInitializer implements ModInitializer {
///     private Tusan language = Tusan.tusan(); // <- THIS!
///
/// 	@Override
/// 	public void onInitialize() {
///         language.registerLexerRule(
///             new LexerRule(MyTokenType.EFFECT,
///                 WordedPattern.makeOneWordPattern("hello")
///             )
///         );
///
///         language.registerNode(HelloNode.class);
/// 	}
/// }
/// ```
/// ### What's the difference between this and using globals?
///
/// Using the global instance also lets other mods addon. By using the global you are adding it to the Tums mod, but by using your own you'll have to execute the scripts on your own.
///
/// ## Creating from scratch
///
/// If you want to create a new language from scratch, follow the same method but instead of `Tusan.tusan()` use `new Tusan()` or `TumsAPI.blank()`. The main issue with using this method is that the main compile method is the same, so you'll have to make sure the token list you provide contains a `PrebuiltTusanTokenType.ENDSCRIPT` at the end. You will have to manually define every single lexer rule for strings, numbers, your keywords, etc.
///
/// ```java
/// public class YourModInitializer implements ModInitializer {
///     private Tusan language = TumsAPI.blank(); // <- THIS!
///
/// 	@Override
/// 	public void onInitialize() {
///         language.registerLexerRule(
///             new LexerRule(MyTokenType.EFFECT,
///                 WordedPattern.makeOneWordPattern("hello")
///             )
///         );
///
///         language.registerNode(HelloNode.class);
/// 	}
/// }
/// ```
/// !((warning You need to create your own version of StatementNode)[Read about it below])
///
/// ### Creating your own statement node
///
/// This is a **VERY** important step, you have to implement a Node that acts as the head node for all other nodes. Once one statement is parsed it goes to the next one. So create similar in sense:
///
/// ```java
/// public class Statement extends Node {
///     public Statement(TusanContext ctx){
///         super(ctx);
///     }
///
///     public Object create(){
///         if (interpreter.end){
///             return false;
///         }
///
///         Object value = false;
///         if (token.group == PrebuiltTusanTokenGroup.STRUCTURE){
///             Class<? extends Node> node = ctx.getTusan().getStatementTokenNodeMap().getTokenMap().get(token.type);
///             if (node == null){
///                 interpreter.error("UnexpectedToken", "Structure " + token.type.name() + ":" + token.value + " has no definition", null);
///             }
///             // THIS SECTION IS NEEDED IF YOUR USING NODE CONFIGS
///             Node newNode = Node.instantiateNode(node, ctx);
///             if (!newNode.getNodeConfig().isDontSendNext()) {
///                 ctx.nextToken();
///             }
///             newNode.run();
///         } else if (token.group == PrebuiltTusanTokenGroup.EFFECT){
///             value = new Effect(ctx).run(); // example of a group node usage
///         } else if (token.type == PrebuiltTusanTokenType.BREAKSTRUCTURE && token.value.equals("return")){
///             new Return(ctx).create(); // you could implement your own
///         } else if (Arrays.asList(PrebuiltTusanTokenType.IDENTIFIER, PrebuiltTusanTokenType.LEFT_CURLY, PrebuiltTusanTokenType.NUMBER, PrebuiltTusanTokenType.STRING, PrebuiltTusanTokenType.KEYWORD).contains(token.type)){ // running expressions at statement level allow "in-built" functions
///             value = new Expression(ctx).create();
///         } else {
///             interpreter.error("UnexpectedToken", "Expected valid statement got "+token.type.name()+":"+token.value, null);
///         }
///         return value;
///     }
/// }
/// ```
///
/// It doesn't have to look like this, this is generally the best practice. You can look at the source code if you want to implement something similar to what we've implemented in the default Tums.
///
/// Then once your done, register it in your mod initializer via:
/// ```java
/// language.setStatementNode(Statement.class) // or whatever your statement node is called
/// ```
///
/// By using this method many features cannot be used, or you would have to implement your own version:
/// - Node Configs
/// - Return statements
/// - In-built functions
/// - Event handling
/// - It can also get confusing as many Tums features cannot be used since you'll have to implement them by yourself
///
/// *You might also want to implement your own [Context](https://wiki.tutla.net/tums/developer/context)*
///
/// !((warning Add ENDSCRIPT to new executors)[When implementing something like functions or loops you might want to store a seperate list of tokens or initialize a new interpreter to create a "local environment" or something that only executes a part of your script. When creating these new ones make sure to add an ENDSCRIPT token (prebuilt token) to your new interpreter before running])
///
/// After this its almost the same, you register tokens in the lexer using [Lexer rules](https://wiki.tutla.net/tums/developer/lexer/rules) and [Custom Nodes](https://wiki.tutla.net/tums/developer/node/).
///
/// ## Methods
///
/// A `Tusan` language has the following methods:
///
/// <!-- methods -->
///
/// @tums.title Creating your own language using Tusan
/// @tums.summary How to use Tusan/Tums for creating your own language
public class Tusan {
    private final TusanLanguageRules lang = new TusanLanguageRules();
    private final List<Class<? extends Node>> nodes = new ArrayList<>();

    private final Token2NodeMap statementTokenNodeMap = new Token2NodeMap();
    private final TokenGroup2NodeMap statementTokenGroupNodeMap = new TokenGroup2NodeMap();
    private Class<? extends Node> statementNode = Statement.class;

    private static final List<LexerRule> DEFAULT_LEXER_RULES = List.of( // ORDER MATTERS!
            // 's right after a name (box's size), so a string like 'single' is not read as a property
            new LexerRule(PrebuiltTusanTokenType.PROPERTY, "(?<=[A-Za-z0-9_)\\]])'s(?![A-Za-z0-9_'\"])"),
            new LexerRule(PrebuiltTusanTokenType.STRING, "\"(?:[^\"\\\\]|\\\\.)*\"|'(?:[^'\\\\]|\\\\.)*'"),
            new LexerRule(PrebuiltTusanTokenType.IDENTIFIER, "\\b\\d+(?:st|nd|rd|th)\\b").setGroup(PrebuiltTusanTokenGroup.IDENTIFIER), // ordinals: 1st, 2nd, 3rd...
            new LexerRule(PrebuiltTusanTokenType.NUMBER, "\\d+(\\.\\d+)?"),
            new LexerRule(PrebuiltTusanTokenType.BOOL, WordedPattern.makeWordedPattern(List.of("true", "false")) ),
            new LexerRule(PrebuiltTusanTokenType.NOTHING, WordedPattern.makeOneWordPattern("nothing")),

            new LexerRule(PrebuiltTusanTokenType.OPERATOR, "\\*\\*|[+\\-*/%^]"),
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

            new LexerRule(PrebuiltTusanTokenType.PRINT, WordedPattern.makeOneWordPattern("print")).setGroup(PrebuiltTusanTokenGroup.EFFECT),
            new LexerRule(PrebuiltTusanTokenType.SET, WordedPattern.makeOneWordPattern("set")).setGroup(PrebuiltTusanTokenGroup.EFFECT)
    );

    /// Creates an empty language, with no lexer rules or nodes
    public Tusan(){

    }

    /// Creates a language with the Tusan defaults already registered
    public static Tusan tusan(){
        Tusan tsn = new Tusan();
        tsn.registerAllDefaultRules();
        tsn.registerAllDefaultNodes();
        return tsn;
    }

    // ---------------------------------------------------------------- nodes
    /// Registers a [node](https://wiki.tutla.net/tums/developer/node/) with the language
    public void registerNode(Class<? extends Node> node){
        // TODO: Verify if node already exists
        // TODO: Nodes should be able to be linked to: 1) the main statement loop and its direct descendants (Effects/Structures) 2) Expressions/Terms/Factors
        nodes.add(node);
    }

    /// Returns every registered node
    public List<Class<? extends Node>> getNodes(){
        return nodes;
    }

    /// Runs a node from the main statement loop whenever a token of this type starts a statement
    public void linkNodeToStatement(TokenType tokenType, Class<? extends Node> node){ // TODO: links for category nodes aswell
        statementTokenNodeMap.add(tokenType, node);
    }

    /// Runs a node from the main statement loop whenever a token of this group starts a statement
    public void linkNodeToStatement(TokenGroup tokenGroup, Class<? extends Node> node){ // TODO: links for category nodes aswell
        statementTokenGroupNodeMap.add(tokenGroup, node);
    }

    /// Returns the token types linked to nodes with `linkNodeToStatement`
    public Token2NodeMap getStatementTokenNodeMap(){
        return statementTokenNodeMap;
    }

    /// Returns the token groups linked to nodes with `linkNodeToStatement`
    public TokenGroup2NodeMap getStatementTokenGroupNodeMap(){
        return statementTokenGroupNodeMap;
    }

    // ---------------------------------------------------------------- lexer rules

    /// Adds a [lexer rule](https://wiki.tutla.net/tums/developer/lexer/rules), your syntax
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

    // ---------------------------------------------------------------- compiling

    /// Runs the main statement loop over the interpreter's tokens. Called by `Interpreter.compile()`
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

    /// Returns the lexer rules of this language
    public TusanLanguageRules getLang(){
        return lang;
    }

    /// Returns the node every statement starts from
    public Class<? extends Node> getStatementNode() {
        return statementNode;
    }

    /// Replaces the node every statement starts from
    public void setStatementNode(Class<? extends Node> statementNode) {
        this.statementNode = statementNode;
    }
}
