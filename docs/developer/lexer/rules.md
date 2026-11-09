---
title: "Lexer Rules"
summary: "How to create lexer rules in Tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

A lexer rule is connecting the user's code into a token that can be interpreted by the parser. Basically, it's your syntax.

To define a lexer rule, you need to create a new instance of `LexerRule` and pass it to the `registerLexerRule` function in the Tusan Language you are using.

Example:
```java
public class YourModInitializer implements ModInitializer {
    private Tusan language;
	@Override
	public void onInitialize() {
        language.registerLexerRule(
            new LexerRule(MyTokenType.HELLO, 
                WordedPattern.makeOneWordPattern("hello")
            )
        );
	}
}
```

## Tokens

In order to create a lexer rule you need to first define a token type. To create one, create an enum implementing `TokenType`. For example:
```java
import net.tutla.tums.tusan.lexer.TokenType;

public enum MyTokenType implements TokenType {
    HELLO
}
```

You can also optionally implement a group:
```java
import net.tutla.tums.tusan.lexer.TokenGroup;

public enum MyTokenGroup implements TokenGroup {
    KEYWORD,
    FUNCTION,
    OPERATOR
}
```

## Creating the rule

Once you have the token type and optionally a group, intialize it:
```java
language.registerLexerRule(
    new LexerRule(
        MyTokenType.HELLO, 
        WordedPattern.makeOneWordPattern("hello")
    )
);
```

The `TokenGroup` can be added by using `.setGroup()`.

In the place of `WordedPattern.makeOneWordPattern("hello")` you can use any regex pattern, to learn more about it read [Patterns](/tums/scripting/lexer/patterns). For example `"\\bhello\\b"`.