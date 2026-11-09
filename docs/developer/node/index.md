---
title: "Node"
summary: "Node creation in Tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

Nodes execute your code. They run in a chain, the statement node calls (for example) the effect node, which called the print node which calls the expression node and so on.

To create a node, you need to extend the `Node` class and override the `create` method. An example:

```java
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class Print extends Node {
    public Print(TusanContext ctx){
        super(ctx);
    }

    public Print create(){
        System.out.println(new Expression(ctx).create().value);
        return this;
    }
}
```

This is a simple example of a node that prints the value of an expression. So using it would look like:
```lua
print 5+5
```

With how brackets work in Tums (check the source of Expression Node), you can also do this:
```lua
print("Hi world!")
```

To actually work on the functionality, read [Parsing](/tums/developer/node/parsing)

### Category Nodes

These are nodes that behave as categories, like all one for structures, one for effects, etc. This uses a `Token2NodeMap` to map tokens to nodes. To create a category node use:
```java
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.nodes.expression.Expression;

public class EffectNode extends Node {
    public EffectNode(TusanContext ctx){
        super(ctx);
        NodeConfig conf = new NodeConfig();
        conf.addCategoryToken(MyTokenType.PRINT, Print.class); // Add for every token you want to map
        this.setNodeConfig(conf);
    }

    public EffectNode create(){
        return this;
    }
}
```

!((important Category nodes are not yet supported in the Tusan)[This is currently only used in the internal code])

## Linking nodes
You have a node, but when does it run?

To make the node run in the main statement loop, use `linkNodeToStatement(TokenType tokenType, Class<? extends Node> node)`

Example:
```java
public class YourModInitializer implements ModInitializer {

	@Override
	public void onInitialize() {
        TumsAPI.getGlobalTusan().registerLexerRule(
            new LexerRule(MyTokenType.PRINT, 
                WordedPattern.makeOneWordPattern("print")
            )
        );

        TumsAPI.getGlobalTusan().registerNode(Print.class);
        TumsAPI.getGlobalTusan().linkNodeToStatement(MyTokenType.PRINT, Print.class);
        // category nodes don't have support to be linked YET
	}
}
```

