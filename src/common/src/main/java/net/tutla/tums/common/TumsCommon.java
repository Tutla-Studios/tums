package net.tutla.tums.common;

import net.tutla.tums.Tums;
import net.tutla.tums.tusan.MinecraftEnvironment;

import java.nio.file.Path;

/** Shared mod-loader initialisation, called from the Fabric and NeoForge entrypoints. */
public final class TumsCommon {
    public static final String MOD_ID = "tums";

    private TumsCommon() {}

    public static void init(Path gameDir) {
        Tums.init(gameDir.resolve("tums"));
        Tums.runScripts(MinecraftEnvironment.SERVER);
    }
}
