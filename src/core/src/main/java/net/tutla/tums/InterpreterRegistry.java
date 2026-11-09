package net.tutla.tums;

import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.InterpreterData;

import java.util.List;
import java.util.Map;

public class InterpreterRegistry {
    public InterpreterData registry = new InterpreterData(null, null, null, null);

    public void add(InterpreterData intr){
        for (Map.Entry<String, List<Interpreter>> entry : intr.events.entrySet()) {
            for (Interpreter eventExecutor : entry.getValue()){
                registry.events.get(entry.getKey()).add(eventExecutor);
            }
        }
    }

}
