package net.tutla.tums;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.tutla.tums.tums.EventType;
import net.tutla.tums.tums.TumsLanguageInitializer;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lang.Tusan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Tums implements ModInitializer {
    private static final Tusan tusan = Tusan.tusan(); // primary language

    public static InterpreterRegistry register = new InterpreterRegistry();
    public static InterpreterRegistry getRegister(){
        return register;
    }

    Path gameDir = FabricLoader.getInstance().getGameDir();
    Path scriptsDir = gameDir.resolve("tums/scripts");
    Path tumsDir = gameDir.resolve("tums");

    @Override
    public void onInitialize() {
        resolveTumsDir();

        TumsLanguageInitializer.init();
        registerAllDefaultEvents();

        TumsAPI.runAllScripts(scriptsDir, TumsAPI.getGlobalTusan());
    }

    public void resolveTumsDir(){
        try {
            Files.createDirectories(tumsDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Tusan getTusan(){
        return tusan;
    }

    public void registerAllDefaultEvents(){
        for (EventType name : EventType.values()){
            TumsAPI.addEvent(name.name());
        }
    }
}
