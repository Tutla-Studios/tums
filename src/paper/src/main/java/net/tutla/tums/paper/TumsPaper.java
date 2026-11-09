package net.tutla.tums.paper;

import net.tutla.tums.Tums;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.lang.Tusan;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

/// Tums also ships as a Spigot/Paper plugin, so other plugins can add syntax to it the same way mods do. Scripts go in `plugins/Tums/scripts/`.
///
/// ## Setting up
///
/// 1. Add the Tums jar as a `compileOnly` dependency in `build.gradle`, and put the same jar in your server's `plugins` folder.
/// ```cpp
/// dependencies {
///     compileOnly files("libs/tums-1.0.0.jar")
/// }
/// ```
/// 2. Depend on it in your `plugin.yml`, so Tums is loaded before your plugin:
/// ```yaml
/// depend: [Tums]
/// ```
/// 3. Register your syntax in `onEnable`:
/// ```java
/// public class YourPlugin extends JavaPlugin {
///
///     @Override
///     public void onEnable() {
///         TumsPaper.getTusan().registerLexerRule(
///             new LexerRule(MyTokenType.HELLO,
///                 WordedPattern.makeOneWordPattern("hello")
///             )
///         );
///
///         TumsPaper.getTusan().registerNode(HelloNode.class);
///         TumsPaper.getTusan().linkNodeToStatement(MyTokenType.HELLO, HelloNode.class);
///     }
/// }
/// ```
///
/// `TumsPaper.getTusan()` and `TumsAPI.getGlobalTusan()` return the same language, so everything in the rest of the developer docs works the same way in a plugin.
///
/// ## When scripts run
///
/// Tums loads before your plugin but waits for the first server tick to run the scripts in `plugins/Tums/scripts/`, after every plugin is enabled. So syntax registered in `onEnable` is always there in time. Once the scripts have run, the `SERVER_START` event fires.
///
/// Scripts on Spigot/Paper always run in the `SERVER` environment.
///
/// ## Methods
///
/// <!-- methods -->
///
/// @tums.title Spigot / Paper
/// @tums.summary Using Tums from a Spigot or Paper plugin
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

    /// Returns the running Tums plugin, or `null` while it is disabled
    public static TumsPaper getInstance() {
        return instance;
    }

    /// Returns the global language, the same one as `TumsAPI.getGlobalTusan()`. Register lexer rules and nodes on this
    public static Tusan getTusan() {
        return TumsAPI.getGlobalTusan();
    }

    /// Compiles and runs a single script with the global language, and registers its events
    public static void runScript(Path script) {
        TumsAPI.runScript(script, TumsAPI.getGlobalTusan(), MinecraftEnvironment.SERVER);
    }
}
