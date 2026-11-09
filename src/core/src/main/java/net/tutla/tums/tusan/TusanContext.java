package net.tutla.tums.tusan;

import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.TokenManager;
import net.tutla.tums.tusan.lang.Tusan;
import net.tutla.tums.tusan.lexer.Token;

/// The `TusanContext` gives context to the current state of the parser. It contains quick access to utility features and more.
///
/// It has the following methods:
///
/// <!-- methods -->
///
/// ## Creating your own context
///
/// Let's say you're working on a scripting language that requires constant access to the Minecraft client. You can create one like this:
///
/// ```java
/// public class CustomContext extends TusanContext  {
///     public CustomContext(Interpreter interpreter) {
///         super(interpreter);
///     }
///
///     public Minecraft getClient(){
///         return Minecraft.getInstance();
///     }
/// }
/// ```
///
/// Make sure to call `interpreter.setContext(context)` before calling `interpreter.compile()`. This only works if you're the one compiling the script, doesn't work with addons *for now*.
///
/// @tums.title TusanContext
/// @tums.summary Context for parsing in tums
public class TusanContext {
    public Interpreter interpreter;
    public TokenManager tokenContext;
    public boolean isFunction;

    public MinecraftEnvironment environment = MinecraftEnvironment.SERVER;

    public TusanContext(Interpreter interpreter){
        this.interpreter = interpreter;
        this.tokenContext = interpreter.tokenManager;
    }

    // helpers

    /// Gets the current token
    public Token getCurrentToken(){
        return interpreter.currentToken;
    }

    /// Returns current position
    public int getPos(){
        return interpreter.pos;
    }

    /// Gets next token without consuming it
    public Token getNextToken(){
        return tokenContext.getNextToken();
    }

    /// Returns the next token and consumes it
    public Token nextToken(){
        return tokenContext.nextToken();
    }

    /// Returns current [interpreter](https://wiki.tutla.net/tums/developer/interpreter)
    public Interpreter getInterpreter(){
        return interpreter;
    }

    /// Returns the current language
    public Tusan getTusan(){
        return interpreter.getTusan();
    }

    public void setEnvironment(MinecraftEnvironment env){
        this.environment = env;
    }

    /// Returns the current environment of where the script is executing -> `CLIENT|SERVER`
    public MinecraftEnvironment getEnvironment(){
        return environment;
    }
}
