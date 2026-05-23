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

public class TumsAPI {
    public static InterpreterRegistry getRegister(){
        return Tums.getRegister();
    }

    private static final List<String> eventMappings = new ArrayList<>();
    public static void addEvent(String name){
        eventMappings.add(name);
    }
    public static List<String> getEventMappings(){
        return eventMappings;
    }

    public static Tusan blank(){
        return Tusan.tusan();
    }

    public static Tusan getGlobalTusan(){
        return Tums.getTusan();
    }

    public static void runAllScripts(Path scriptsDir, Tusan tusan, MinecraftEnvironment environment){
        try (Stream<Path> stream = Files.list(scriptsDir)) {
            stream.filter(Files::isRegularFile)
                    .forEach((p) -> {runScript(p, tusan, environment);});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void executeEvent(String name, HashMap<String, Variable> variables, MinecraftEnvironment env){
        List<Interpreter> callback = Tums.getRegister().registry.events.get(name.toUpperCase());
        if (variables == null){
            variables = new HashMap<>();
        }
        for (Interpreter executor : callback){
            Interpreter exec  = executor.clone();
            exec.setCompileEnvironment(env);
            exec.tokenManager.changeTokensParent(exec);
            exec.data.vars.putAll(variables);
            executor.compile();
        }
    }
}
