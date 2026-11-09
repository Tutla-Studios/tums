---
title: "Interpreter"
summary: "Running your scripts in tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

The interpreter is by far the most important part in Tums. It is the instance that parses your script.

## Creating Interpreters

1. Define an interpreter using `new Interpreter(Tusan language)`. With the language being the language you have created by using the previous steps, you can use `TumsAPI.getGlobalTusan()` if you want to execute the global language.
2. Set it up using `setup(InterpreterData data, TokenManager tknmanager, String _text, Path file, MinecraftEnvironment env)`.

That looks big doesn't it? Let's break it down:
- InterpreterData: Contains all the generated data (variables, functions, events, etc)
    - Define using `InterpreterData(HashMap<String, Variable> vars, HashMap<String, FunctionRegistry> funcs, HashMap<String, Variable> local, List<Runnable> asyncTasks)`
    - If you want to provide it anything in advance, for example a variable called `me` that gets the current player, you can instantiate a new Hashmap and add one.
    - You can provide them all as `null` and it will instantiate an empty interpreter data. 
- TokenManager: You can provide a custom token manager if you want to execute a custom list of tokens
- String text: Optional, if you just want to execute a string of code, you can provide it here.
- String file: Optional, if you want to execute a file, you can provide it here.
- MinecraftEnvironment: This is the environment the script, by default `SERVER`
3. Optional: Set the context using `setContext(TusanContext ctx)`. This is applicable if you want to provide a custom [context](/tums/developer/context).

That's it! You're ready to start. Here's an example if you're still confused:

```java
public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
    Interpreter interpreter = new Interpreter(tusan);
    interpreter.setup(null,null,null, script, environment);
    interpreter.setContext(new TusanContext(interpreter)); // not necessary
}
```

## Executing Scripts

To execute a script just call `compile()` on the interpreter. __Or you could just use `TumsAPI.runScript(Path script, Tusan tusan, MinecraftEnvironment environment)` if you're not creating an interpreter__

```java
public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
    Interpreter interpreter = new Interpreter(tusan);
    interpreter.setup(null,null,null, script, environment);
    interpreter.setContext(new TusanContext(interpreter));

    try {
        interpreter.compile();
    } catch (Exception e){
        e.printStackTrace();
    }

    getRegister().add(interpreter.data);
}
```