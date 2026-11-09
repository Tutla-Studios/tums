---
title: "Creating your own language using Tusan"
summary: "How to use Tusan/Tums for creating your own language"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

It's very simple, you have two options in creating a language using Tusan:
- Using the Tusan defaults and extend them
- Creating a new language from scratch

## Creating from defaults

By using the defaults you will already have access to:
- Expression Evaluators
- Prebuilt functions
- Prebuilt types
- Prebuilt effects and structures
- Structure definitions

To get started create one like this:
```java
public class YourModInitializer implements ModInitializer {
    private Tusan language = Tusan.tusan(); // <- THIS!

	@Override
	public void onInitialize() {
        language.registerLexerRule(
            new LexerRule(MyTokenType.EFFECT, 
                WordedPattern.makeOneWordPattern("hello")
            )
        );

        language.registerNode(HelloNode.class);
	}
}
```
### What's the difference between this and using globals?

Using the global instance also lets other mods addon. By using the global you are adding it to the Tums mod, but by using your own you'll have to execute the scripts on your own.

## Creating from scratch

If you want to create a new language from scratch, follow the same method but instead of `Tusan.tusan()` use `new Tusan()` or `TumsAPI.blank()`. The main issue with using this method is that the main compile method is the same, so you'll have to make sure the token list you provide contains a `PrebuiltTusanTokenType.ENDSCRIPT` at the end. You will have to manually define every single lexer rule for strings, numbers, your keywords, etc.

```java
public class YourModInitializer implements ModInitializer {
    private Tusan language = TumsAPI.blank(); // <- THIS!

	@Override
	public void onInitialize() {
        language.registerLexerRule(
            new LexerRule(MyTokenType.EFFECT, 
                WordedPattern.makeOneWordPattern("hello")
            )
        );

        language.registerNode(HelloNode.class);
	}
}
```
!((warning You need to create your own version of StatementNode)[Read about it below])

### Creating your own statement node

This is a **VERY** important step, you have to implement a Node that acts as the head node for all other nodes. Once one statement is parsed it goes to the next one. So create similar in sense:

```java
public class Statement extends Node {
    public Statement(TusanContext ctx){
        super(ctx);
    }
    public Object create(){

        Object value = false;
        if (interpreter.end){
            return false;
        }

        if (Arrays.asList(PrebuiltTusanTokenType.IDENTIFIER, PrebuiltTusanTokenType.LEFT_CURLY, PrebuiltTusanTokenType.NUMBER, PrebuiltTusanTokenType.STRING, PrebuiltTusanTokenType.KEYWORD).contains(token.type)){ // not necessary but you can use this
                value = new Expression(ctx).create();
            
            if (token.group == PrebuiltTusanTokenType.EFFECT){
                value = new Effect(ctx).create(); // example of a group node usage
            } else if (token.group == PrebuiltTusanTokenGroup.STRUCTURE){
                AtomicBoolean execed = new AtomicBoolean(false);
                ctx.getTusan().getStatementNodeMap().getTokenMap()
                        .forEach((tok, node) -> {
                            if (token.type == tok){
                                // THIS SECTION IS NEEDED IF YOUR USING NODE CONFIGS
                                Node newNode = Node.instantiateNode(node, ctx);
                                if(!newNode.getNodeConfig().isDontSendNext()) {
                                    ctx.nextToken();
                                }
                                newNode.run(ctx);
                                execed.set(true);
                            }
                });
                if (!execed.get()){
                    interpreter.error("UnexpectedToken", "Structure " + token.type.name() + ":" + token.value + " has no definition", null);
                }
            } else if (Arrays.asList(PrebuiltTusanTokenType.IDENTIFIER, PrebuiltTusanTokenType.LEFT_CURLY, PrebuiltTusanTokenType.NUMBER, PrebuiltTusanTokenType.STRING, PrebuiltTusanTokenType.KEYWORD).contains(token.type)){ // running expressions at statement level allow "in-built" functions
                value = new Expression(ctx).create();
            }
        } else if (token.type == PrebuiltTusanTokenType.BREAKSTRUCTURE && token.value.equals("return")){
            new Return(ctx).create(); // you could implement your own
        } else {
            interpreter.error("UnexpectedToken", "Expected valid statement got "+token.type.name()+":"+token.value, null);
        }
        return value;
    }
}
```

It doesn't have to look like this, this is generally the best practice. You can look at the source code if you want to implement something similar to what we've implemented in the default Tums. 

Then once your done, register it in your mod initializer via: 
```java
language.setStatementNode(Statement.class) // or whatever your statement node is called
```

By using this method many features cannot be used, or you would have to implement your own version:
- Node Configs
- Return statements
- In-built functions
- Event handling
- It can also get confusing as many Tums features cannot be used since you'll have to implement them by yourself

*You might also want to implement your own [Context](/tums/developer/context)*

!((warning Add ENDSCRIPT to new executors)[When implementing something like functions or loops you might want to store a seperate list of tokens or initialize a new interpreter to create a "local environment" or something that only executes a part of your script. When creating these new ones make sure to add an ENDSCRIPT token (prebuilt token) to your new interpreter before running])

After this its almost the same, you register tokens in the lexer using [Lexer rules](/tums/developer/lexer/rules) and [Custom Nodes](/tums/developer/node/). 