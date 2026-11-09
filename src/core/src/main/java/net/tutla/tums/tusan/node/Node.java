package net.tutla.tums.tusan.node;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.TokenType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/// Nodes execute your code. They run in a chain, the statement node calls (for example) the effect node, which called the print node which calls the expression node and so on.
///
/// To create a node, you need to extend the `Node` class and override the `create` method. An example:
///
/// ```java
/// import net.tutla.tums.tusan.TusanContext;
/// import net.tutla.tums.tusan.node.Node;
/// import net.tutla.tums.tusan.lexer.Token;
/// import net.tutla.tums.tusan.nodes.expression.Expression;
///
/// public class Print extends Node {
///     public Print(TusanContext ctx){
///         super(ctx);
///     }
///
///     public Print create(){
///         System.out.println(new Expression(ctx).create().value);
///         return this;
///     }
/// }
/// ```
///
/// This is a simple example of a node that prints the value of an expression. So using it would look like:
/// ```lua
/// print 5+5
/// ```
///
/// With how brackets work in Tums (check the source of Expression Node), you can also do this:
/// ```lua
/// print("Hi world!")
/// ```
///
/// To actually work on the functionality, read [Parsing](https://wiki.tutla.net/tums/developer/node/parsing)
///
/// ### Category Nodes
///
/// These are nodes that behave as categories, like all one for structures, one for effects, etc. This uses a `Token2NodeMap` to map tokens to nodes. To create a category node use:
/// ```java
/// import net.tutla.tums.tusan.TusanContext;
/// import net.tutla.tums.tusan.node.Node;
/// import net.tutla.tums.tusan.lexer.Token;
/// import net.tutla.tums.tusan.nodes.expression.Expression;
///
/// public class EffectNode extends Node {
///     public EffectNode(TusanContext ctx){
///         super(ctx);
///         NodeConfig conf = new NodeConfig();
///         conf.addCategoryToken(MyTokenType.PRINT, Print.class); // Add for every token you want to map
///         this.setNodeConfig(conf);
///     }
///
///     public EffectNode create(){
///         return this;
///     }
/// }
/// ```
///
/// !((important Category nodes are not yet supported in the Tusan)[This is currently only used in the internal code])
///
/// ## Linking nodes
/// You have a node, but when does it run?
///
/// To make the node run in the main statement loop, use `linkNodeToStatement(TokenType tokenType, Class<? extends Node> node)`
///
/// Example:
/// ```java
/// public class YourModInitializer implements ModInitializer {
///
/// 	@Override
/// 	public void onInitialize() {
///         TumsAPI.getGlobalTusan().registerLexerRule(
///             new LexerRule(MyTokenType.PRINT,
///                 WordedPattern.makeOneWordPattern("print")
///             )
///         );
///
///         TumsAPI.getGlobalTusan().registerNode(Print.class);
///         TumsAPI.getGlobalTusan().linkNodeToStatement(MyTokenType.PRINT, Print.class);
///         // category nodes don't have support to be linked YET
/// 	}
/// }
/// ```
///
/// ## Methods
///
/// <!-- methods -->
///
/// @tums.title Node
/// @tums.summary Node creation in Tums
public class Node {
    /// The interpreter running this node
    public Interpreter interpreter;
    /// The [context](https://wiki.tutla.net/tums/developer/context) this node was created with
    public TusanContext ctx;
    private NodeConfig nodeConf = new NodeConfig();
    /// The token that was current when this node was created
    public Token token;

    /// Creates the node at the current token. Every node needs a constructor taking its context
    public Node(TusanContext ctx){
        this.ctx = ctx;
        this.token = ctx.getCurrentToken();
        this.interpreter = ctx.interpreter;
    }

    /// Returns how this node is run
    public NodeConfig getNodeConfig(){
        return nodeConf;
    }

    /// Changes how this node is run, for example to make it a category node
    public void setNodeConfig(NodeConfig conf){
        nodeConf = conf;
    }

    /// Does nothing by default. Override it to check your node before it runs
    public void verify(){

    }

    /// Runs the node: a category node runs the node mapped to the current token, any other node calls `create()` unless its `NodeConfig` says to use a custom implementation
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

    /// Your node's logic. Override this, read [Parsing](https://wiki.tutla.net/tums/developer/node/parsing)
    public Object create(){
        return this;
    }

    /// Creates a node from its class, finding the constructor that fits the arguments
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
