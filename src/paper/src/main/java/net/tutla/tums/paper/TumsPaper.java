package net.tutla.tums.paper;

import net.tutla.tums.Tums;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.lang.Tusan;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

/**
 * Spigot/Paper entrypoint for Tums.
 *
 * <p>Other plugins can use the interpreter/lexer by adding {@code depend: [Tums]} to their
 * {@code plugin.yml} and calling {@link TumsAPI} (or {@link #getTusan()}) directly. Custom syntax
 * should be registered in the dependent plugin's {@code onEnable}; scripts in
 * {@code plugins/Tums/scripts/} are only run on the first server tick, after every plugin is enabled.</p>
 */
public final class TumsPaper extends JavaPlugin {
    private static TumsPaper instance;

    @Override
    public void onLoad() {
        instance = this;
        Tums.init(getDataFolder().toPath());
    }

    @Override
    public void onEnable() {
        // Defer script execution so dependent plugins can register syntax in their onEnable first.
        getServer().getScheduler().runTask(this, () -> {
            Tums.runScripts(MinecraftEnvironment.SERVER);
            TumsAPI.executeEvent("SERVER_START", null, MinecraftEnvironment.SERVER);
        });
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static TumsPaper getInstance() {
        return instance;
    }

    /** The global Tums language instance; register lexer rules and nodes on this. */
    public static Tusan getTusan() {
        return TumsAPI.getGlobalTusan();
    }

    /** Runs (compiles and registers events for) a single script using the global language. */
    public static void runScript(Path script) {
        TumsAPI.runScript(script, TumsAPI.getGlobalTusan(), MinecraftEnvironment.SERVER);
    }
}
