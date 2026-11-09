package net.tutla.tums;

import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.InterpreterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InterpreterRegistry {
    public InterpreterData registry = new InterpreterData(null, null, null, null);

    /// The handlers listening to an event, as a list of their own, so events can read it while a script registers more
    public synchronized List<Interpreter> handlers(String event){
        List<Interpreter> list = registry.events.get(event);
        return list == null ? null : new ArrayList<>(list);
    }

    public synchronized void add(InterpreterData intr){
        for (Map.Entry<String, List<Interpreter>> entry : intr.events.entrySet()) {
            for (Interpreter eventExecutor : entry.getValue()){
                registry.events.get(entry.getKey()).add(eventExecutor);
            }
        }
    }

}
