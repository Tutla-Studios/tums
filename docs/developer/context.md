---
title: "TusanContext"
summary: "Context for parsing in tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

The `TusanContext` gives context to the current state of the parser. It contains quick access to utility features and more.

It has the following methods:
- `getNextToken()` - Gets next token without consuming it
- `getCurrentToken()` - Gets the current token
- `nextToken()` - Returns the next token and consumes it
- `getPos()` - Returns current position
- `getInterpreter()` - Returns current [interpreter](/tums/developer/interpreter)
- `getTusan()` - Returns the current language
- `getEnvironment()` - Returns the current environment of where the script is executing -> `CLIENT|SERVER`

## Creating your own context

Let's say you're working on a scripting language that requires constant access to the Minecraft client. You can create one like this:

```java
public class CustomContext extends TusanContext  {
    public CustomContext(Interpreter interpreter) {
        super(interpreter);
    }

    public Minecraft getClient(){
        return Minecraft.getInstance();
    }
}
```

Make sure to call `interpreter.setContext(context)` before calling `interpreter.compile()`. This only works if you're the one compiling the script, doesn't work with addons *for now*.
