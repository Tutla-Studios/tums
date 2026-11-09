package net.tutla.tums.common;

import net.tutla.tums.Tums;
import net.tutla.tums.tusan.MinecraftEnvironment;

import java.nio.file.Path;

/** Shared mod-loader initialisation, called from the Fabric and NeoForge entrypoints. */
public final class TumsCommon {
    public static final String MOD_ID = "tums";

    private TumsCommon() {}

    /**
     * @param environment CLIENT on a game client, SERVER on a dedicated server: the side the scripts load on
     */
    public static void init(Path gameDir, MinecraftEnvironment environment) {
        Tums.init(gameDir.resolve("tums"));
        Tums.runScripts(environment);
    }
}
