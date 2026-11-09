---
title: "Getting started"
summary: "How to use Tusan/Tums for developers"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

To start using Tusan/Tums for development follow the instructions in the homepage to install and set it up as dependancy in your mod.

!((warning This wiki is still under heavy development)[Anything could change])

## Basics

In your mod initializer (works both client and server) register your lexer rules (syntax) and nodes

```java
public class YourModInitializer implements ModInitializer {

	@Override
	public void onInitialize() {
        TumsAPI.getGlobalTusan().registerLexerRule(
            new LexerRule(MyTokenType.HELLO, 
                WordedPattern.makeOneWordPattern("hello")
            )
        );

        TumsAPI.getGlobalTusan().registerNode(HelloNode.class);
	}
}
```

Make sure to implement `MyTokenType` and `HelloNode`. Read more about it in the next pages

### What is Global Tusan?
Global Tusan is a singleton instance of [Tusan](/tusan) that is used to register lexer rules and nodes. It is used by Tums and is shared by all other mods. If you want to create your own language, create a new instance of Tusan and use it instead. Read more about it in [Creating your own Language](/tums/developer/creating-your-own)

### What is Tusan?
Tusan in this case is the instance of a language in Tums.