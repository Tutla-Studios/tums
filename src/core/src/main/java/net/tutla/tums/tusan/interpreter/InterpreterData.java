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
    /// The scope this one falls back on for variables it does not have: the script's, for a function call or an event run. `null` for a script itself
    public InterpreterData parent;

    public InterpreterData(HashMap<String, Variable> vars, HashMap<String, FunctionRegistry> funcs, HashMap<String, Variable> local, List<Runnable> asyncTasks) {
        this.vars = vars != null ? vars : new HashMap<>();
        this.funcs = funcs != null ? funcs : new HashMap<>();
        this.local = local != null ? local : new HashMap<>();
        this.asyncTasks = asyncTasks != null ? asyncTasks : new ArrayList<>();
        this.events = getEventNames();
    }

    /// A scope for a function call or an event run: variables of its own, falling back on this one's, and this one's functions
    public InterpreterData child() {
        InterpreterData scope = new InterpreterData(null, funcs, null, asyncTasks);
        scope.parent = this;
        return scope;
    }

    /// The variables of the nearest scope, this one first, that has a variable by this name, or `null` if none does
    public HashMap<String, Variable> scopeOf(String name) {
        for (InterpreterData scope = this; scope != null; scope = scope.parent) {
            if (scope.vars.containsKey(name)) return scope.vars;
        }
        return null;
    }

    /// Whether this scope or one it falls back on has a variable by this name
    public boolean hasVar(String name) {
        return scopeOf(name) != null;
    }

    private HashMap<String, List<Interpreter>> getEventNames() {
        HashMap<String, List<Interpreter>> interpreterEventMappings = new HashMap<>();
        for (EventType e : EventType.values()) {
            interpreterEventMappings.put(e.toString(), new ArrayList<>());
        }
        return interpreterEventMappings;
    }
}