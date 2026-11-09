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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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

    /// Queues a script to compile and run on the Tums script thread, after the scripts queued before it, then registers its data. Returns straight away. See [Interpreter](https://wiki.tutla.net/tums/developer/interpreter)
    public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
        synchronized (SCRIPTS) {
            loading = loading.thenRunAsync(() -> {
                Interpreter interpreter = new Interpreter(tusan);
                try {
                    interpreter.setup(null, null, null, script, environment);
                    interpreter.compile(); // where every script in the dir gets executed
                } catch (Exception e){
                    e.printStackTrace();
                }
                if (interpreter.data != null) getRegister().add(interpreter.data);
            }, SCRIPTS);
        }
    }

    // Scripts load here, one after another, in the order they were queued. A daemon, so it never keeps
    // the game from closing.
    private static final ExecutorService SCRIPTS = Executors.newSingleThreadExecutor(daemon("Tums scripts"));

    // Done once every script queued so far has loaded. Events fired before then wait for it, so an event
    // fired straight after queuing the scripts (Paper's server_start) still finds their handlers.
    private static CompletableFuture<Void> loading = CompletableFuture.completedFuture(null);

    // Every event handler runs on a thread of its own, at the same time as the others
    private static final ExecutorService EVENTS = Executors.newCachedThreadPool(daemon("Tums event"));

    private static ThreadFactory daemon(String name){
        return task -> {
            Thread thread = new Thread(task, name);
            thread.setDaemon(true);
            return thread;
        };
    }

    /// Fires an event: every handler listening to it runs on its own thread with the given variables, on its own copy of its script, so nothing it does reaches the script, the registry or any other run. Returns straight away
    public static void executeEvent(String name, HashMap<String, Variable> variables, MinecraftEnvironment env){
        HashMap<String, Variable> eventVariables = variables == null ? new HashMap<>() : variables;
        CompletableFuture<Void> loaded;
        synchronized (SCRIPTS) {
            loaded = loading;
        }
        loaded.thenRun(() -> {
            List<Interpreter> handlers = Tums.getRegister().handlers(name.toUpperCase());
            if (handlers == null) return; // not an event any script can listen to
            for (Interpreter handler : handlers){
                EVENTS.execute(() -> runHandler(handler, eventVariables, env));
            }
        });
    }

    private static void runHandler(Interpreter handler, HashMap<String, Variable> variables, MinecraftEnvironment env){
        try {
            Interpreter run = handler.clone(); // tokens of its own
            run.setCompileEnvironment(env);
            run.data = handler.data.copy();    // variables of its own, copied from the script as it loaded
            run.data.vars.putAll(variables);
            run.compile();
        } catch (Exception e) {
            e.printStackTrace(); // one broken handler does not stop the others
        }
    }
}
