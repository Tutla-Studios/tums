---
title: "Parsing"
summary: "Parsing code using Tusan in Tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

In order for your node to work, you have to write the logic (no shit). This is done within the `create` function of your node. The `create` function takes a [Context](/tums/context) and a Token as arguments. The context is a reference to the current state of the parser and contains important information. The token is the current token that the parser is working with.

When the create function is called, the __NEXT token__ is passed (unless your `NodeConfig` is different). 

Let's take a look at this example:
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

This is a simple `Print` node that prints the value of an expression. 

Now let's try making a very simple version of a `Print` node that accepts strings:

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
        String text = ctx.getCurrentToken().value;
        System.out.println(text);
        return this;
    }
}
```

*You can also use `expectTokenType(TokenType type)` for strings and more strictness if you're only targetting strings.*

In this example we use the context to get the current token and print its value. This is a very basic example and does not handle complex expressions. So we can use the in-built `ExpressionNode` to parse an expression and then extract the value from it (the expression node is unique as it has a `value` object that stores the computed expression).

Generally if you want to compute an expression, just use an `ExpressionNode`. It handles function calls, conditions, variables and all other expressions.

## Token Manager

The token manager lets you access the tokens in the current context. You can access it via `ctx.getInterpreter().tokenManager`. It has many features to access tokens:
- `getNextToken()` - Gets next token without consuming it
- `getCurrentToken()` - Gets the current token
- `nextToken()` - Returns the next token and consumes it
- `expectTokenType(TokenType type)` - Expect the next token to be of a specific type (not group). If it doesn't match it calls an error
- `expectToken(TokenType type, String value)` - Expect a token as is, if it doesnt match it calls an error
- `get(int pos)` - Get token at position
- `getPos()` - Returns current position

*It does include other features but these are the important/useful ones*

From here all you do is read the `value` of the tokens, and just keep parsing with the features in the TokenManager.