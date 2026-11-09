package net.tutla.tums;

import net.tutla.tums.tums.EventType;
import net.tutla.tums.tums.TumsLanguageInitializer;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.lang.Tusan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Platform-agnostic bootstrap for Tums. Every platform entrypoint (Fabric, NeoForge, Paper/Spigot)
 * calls {@link #init(Path)} once with its own data directory, then {@link #runScripts(MinecraftEnvironment)}
 * once anything that wants to register custom syntax has had a chance to do so.
 */
public final class Tums {
    private static final Tusan tusan = Tusan.tusan(); // primary language

    public static InterpreterRegistry register = new InterpreterRegistry();

    private static Path tumsDir;
    private static Path scriptsDir;
    private static boolean initialized = false;

    private Tums() {}

    public static synchronized void init(Path dataDir) {
        if (initialized) return;
        tumsDir = dataDir;
        scriptsDir = dataDir.resolve("scripts");
        try {
            Files.createDirectories(scriptsDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        TumsLanguageInitializer.init();
        registerAllDefaultEvents();
        initialized = true;
    }

    public static void runScripts(MinecraftEnvironment environment) {
        TumsAPI.runAllScripts(scriptsDir, tusan, environment);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static InterpreterRegistry getRegister() {
        return register;
    }

    public static Tusan getTusan() {
        return tusan;
    }

    public static Path getTumsDir() {
        return tumsDir;
    }

    public static Path getScriptsDir() {
        return scriptsDir;
    }

    private static void registerAllDefaultEvents() {
        for (EventType name : EventType.values()) {
            TumsAPI.addEvent(name.name());
        }
    }
}
