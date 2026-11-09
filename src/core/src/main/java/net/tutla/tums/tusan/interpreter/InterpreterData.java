package net.tutla.tums.tusan.interpreter;

import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.nodes.base.function.FunctionRegistry;
import net.tutla.tums.tums.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterpreterData {
    public final HashMap<String, Variable> vars;
    public final HashMap<String, FunctionRegistry> funcs;
    public final HashMap<String, Variable> local;
    public final List<Runnable> asyncTasks;
    public final HashMap<String, List<Interpreter>> events;

    public InterpreterData(HashMap<String, Variable> vars, HashMap<String, FunctionRegistry> funcs, HashMap<String, Variable> local, List<Runnable> asyncTasks) {
        this.vars = vars != null ? vars : new HashMap<>();
        this.funcs = funcs != null ? funcs : new HashMap<>();
        this.local = local != null ? local : new HashMap<>();
        this.asyncTasks = asyncTasks != null ? asyncTasks : new ArrayList<>();
        this.events = getEventNames();
    }

    private HashMap<String, List<Interpreter>> getEventNames() {
        HashMap<String, List<Interpreter>> interpreterEventMappings = new HashMap<>();
        for (EventType e : EventType.values()) {
            interpreterEventMappings.put(e.toString(), new ArrayList<>());
        }
        return interpreterEventMappings;
    }
}