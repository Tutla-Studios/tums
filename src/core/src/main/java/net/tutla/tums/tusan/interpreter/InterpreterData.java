package net.tutla.tums.tusan.interpreter;

import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.nodes.base.function.FunctionRegistry;
import net.tutla.tums.tums.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /// The outermost scope: the script's (or the event run's) own variables, under any function call scopes
    public InterpreterData root() {
        InterpreterData scope = this;
        while (scope.parent != null) scope = scope.parent;
        return scope;
    }

    /// A deep copy for an event run: its own variables (lists, TSON and properties copied too, so nothing it changes reaches this one) and its own function table. It registers no events
    public InterpreterData copy() {
        IdentityHashMap<Object, Object> seen = new IdentityHashMap<>();
        HashMap<String, Variable> varsCopy = new HashMap<>();
        for (Map.Entry<String, Variable> entry : vars.entrySet()) {
            varsCopy.put(entry.getKey(), (Variable) copyValue(entry.getValue(), seen));
        }
        return new InterpreterData(varsCopy, new HashMap<>(funcs), null, new ArrayList<>());
    }

    // copies what a script can change in place; anything else (strings, numbers, Minecraft objects) is
    // shared, which is fine as a script cannot change it. `seen` keeps shared and self references as they are
    private static Object copyValue(Object value, IdentityHashMap<Object, Object> seen) {
        if (value == null) return null;
        if (seen.containsKey(value)) return seen.get(value);
        if (value instanceof Variable v) {
            Variable copy = new Variable(v.name, null, null);
            seen.put(v, copy);
            copy.value = copyValue(v.value, seen);
            if (v.properties != null) {
                for (Map.Entry<String, Variable> property : v.properties.entrySet()) {
                    copy.properties.put(property.getKey(), (Variable) copyValue(property.getValue(), seen));
                }
            }
            return copy;
        }
        if (value instanceof List<?> list) {
            List<Object> copy = new ArrayList<>();
            seen.put(list, copy);
            for (Object item : list) copy.add(copyValue(item, seen));
            return copy;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            seen.put(map, copy);
            for (Map.Entry<?, ?> entry : map.entrySet()) copy.put(entry.getKey(), copyValue(entry.getValue(), seen));
            return copy;
        }
        return value;
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