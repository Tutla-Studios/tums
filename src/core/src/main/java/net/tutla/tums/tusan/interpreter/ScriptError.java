package net.tutla.tums.tusan.interpreter;

public class ScriptError extends RuntimeException {
    public final String name;

    public ScriptError(String name, String detail) {
        super(name + " : " + detail, null, false, false);
        this.name = name;
    }
}
