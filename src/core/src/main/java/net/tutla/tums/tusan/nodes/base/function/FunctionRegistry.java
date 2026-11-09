package net.tutla.tums.tusan.nodes.base.function;

import net.tutla.tums.tusan.Types;
import net.tutla.tums.tusan.interpreter.Interpreter;

import java.util.List;

public class FunctionRegistry {
    public List<FunctionParameter> parameters;
    public Interpreter interpreter;
    public FunctionRegistry(List<FunctionParameter> paramaters, Interpreter interpreter){
        this.interpreter = interpreter;
        this.parameters = paramaters;
    }
}
