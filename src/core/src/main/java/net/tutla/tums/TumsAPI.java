package net.tutla.tums;

import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lang.Tusan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/// To start using Tusan/Tums for development, follow the instructions on the [homepage](https://wiki.tutla.net/tums) to install it and set it up as a dependency in your mod or plugin. Tums runs on Fabric, NeoForge and Spigot/Paper and the API is the same on all of them. If you're writing a plugin, also read [Spigot / Paper](https://wiki.tutla.net/tums/developer/paper).
///
/// !((warning This wiki is still under heavy development)[Anything could change])
///
/// ## Basics
///
/// In your mod initializer (works both client and server) register your lexer rules (syntax) and nodes. On NeoForge do the same in your `@Mod` constructor, and on Spigot/Paper in your plugin's `onEnable`.
///
/// ```java
/// public class YourModInitializer implements ModInitializer {
///
/// 	@Override
/// 	public void onInitialize() {
///         TumsAPI.getGlobalTusan().registerLexerRule(
///             new LexerRule(MyTokenType.HELLO,
///                 WordedPattern.makeOneWordPattern("hello")
///             )
///         );
///
///         TumsAPI.getGlobalTusan().registerNode(HelloNode.class);
/// 	}
/// }
/// ```
///
/// Make sure to implement `MyTokenType` and `HelloNode`. Read more about it in the next pages
///
/// ### What is Global Tusan?
/// Global Tusan is a singleton instance of [Tusan](https://wiki.tutla.net/tusan) that is used to register lexer rules and nodes. It is used by Tums and is shared by all other mods. If you want to create your own language, create a new instance of Tusan and use it instead. Read more about it in [Creating your own Language](https://wiki.tutla.net/tums/developer/creating-your-own)
///
/// ### What is Tusan?
/// Tusan in this case is the instance of a language in Tums.
///
/// ## TumsAPI
///
/// `TumsAPI` is the entry point for everything above. It has the following methods:
///
/// <!-- methods -->
///
/// @tums.title Getting started
/// @tums.summary How to use Tusan/Tums for developers
public class TumsAPI {
    /// Returns the registry holding the data (variables, functions, events) of every script that has run
    public static InterpreterRegistry getRegister(){
        return Tums.getRegister();
    }

    private static final List<String> eventMappings = new ArrayList<>();
    /// Registers an event name that scripts can listen to with `on <name>`
    public static void addEvent(String name){
        eventMappings.add(name);
    }
    /// Returns every registered event name
    public static List<String> getEventMappings(){
        return eventMappings;
    }

    /// Returns a new, empty language: no lexer rules and no nodes, not even the Tusan defaults
    public static Tusan blank(){
        return new Tusan();
    }

    /// Returns the [global language](#what-is-global-tusan) that Tums and every addon share
    public static Tusan getGlobalTusan(){
        return Tums.getTusan();
    }

    /// Runs every script file in a folder
    public static void runAllScripts(Path scriptsDir, Tusan tusan, MinecraftEnvironment environment){
        try (Stream<Path> stream = Files.list(scriptsDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach((p) -> {runScript(p, tusan, environment);});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// Compiles and runs a single script, then registers its data. See [Interpreter](https://wiki.tutla.net/tums/developer/interpreter)
    public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
        Interpreter interpreter = new Interpreter(tusan);
        interpreter.setup(null,null,null, script, environment);

        try{
            interpreter.compile(); // where every script in the dir gets executed
        } catch (Exception e){
            e.printStackTrace();
        }

        getRegister().add(interpreter.data); // why are we doing this?
    }

    /// Fires an event, running every script listening to it with the given variables
    public static void executeEvent(String name, HashMap<String, Variable> variables, MinecraftEnvironment env){
        List<Interpreter> callback = Tums.getRegister().registry.events.get(name.toUpperCase());
        if (callback == null) return; // not an event any script can listen to
        if (variables == null){
            variables = new HashMap<>();
        }
        for (Interpreter executor : callback){
            Interpreter exec  = executor.clone();
            exec.setCompileEnvironment(env);
            exec.tokenManager.changeTokensParent(exec);
            exec.data.vars.putAll(variables);
            exec.compile(); // the clone, which is the one set up for this event
        }
    }
}
