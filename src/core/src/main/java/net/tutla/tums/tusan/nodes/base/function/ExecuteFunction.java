package net.tutla.tums.tusan.nodes.base.function;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Types;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecuteFunction extends Node {
    public Object value;
    public FunctionRegistry originalFunction;
    public ExecuteFunction(TusanContext ctx){
        super(ctx);
    }

    public ExecuteFunction create(){
        String name = token.value;
        originalFunction = interpreter.data.funcs.get(name);
        interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.LEFT_PAR);

        Interpreter functionInterpreter = originalFunction.interpreter.clone();
        functionInterpreter.data = originalFunction.interpreter.data.child();

        List<FunctionParameter> required = new ArrayList<>();
        Map<String, FunctionParameter> optional = new LinkedHashMap<>();
        for (FunctionParameter parameter : originalFunction.parameters){
            if (parameter.required) required.add(parameter); else optional.put(parameter.name, parameter);
        }

        for (int i = 0; i < required.size(); i++){
            FunctionParameter parameter = required.get(i);
            if (i > 0) {
                if (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.COMMA) missing(name, required, i);
                interpreter.tokenManager.nextToken();
            }
            if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.RIGHT_PAR || isNamed()) missing(name, required, i);
            interpreter.tokenManager.nextToken();
            pass(functionInterpreter, parameter, new Expression(ctx).create().value, name);
        }

        Set<String> given = new HashSet<>();
        boolean first = required.isEmpty();
        while (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.RIGHT_PAR){
            if (!first) interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.COMMA);
            first = false;
            if (!isNamed()){
                interpreter.error("TypeError", name + "() takes " + required.size() + " argument(s) by position, the rest have to be named",
                        optional.isEmpty() ? null : List.of("Pass an optional parameter like " + optional.keySet().iterator().next() + " = value"));
            }
            String parameterName = interpreter.tokenManager.nextToken().value;
            FunctionParameter parameter = optional.get(parameterName);
            if (parameter == null){
                boolean isRequired = required.stream().anyMatch(p -> p.name.equals(parameterName));
                interpreter.error("TypeError", isRequired
                        ? parameterName + " is a required parameter of " + name + "(), pass it by position"
                        : name + "() has no optional parameter called " + parameterName, null);
            }
            if (!given.add(parameterName)){
                interpreter.error("TypeError", parameterName + " is given twice", null);
            }
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.EQUAL);
            interpreter.tokenManager.nextToken();
            pass(functionInterpreter, parameter, new Expression(ctx).create().value, name);
        }
        interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_PAR);

        for (FunctionParameter parameter : optional.values()){
            if (!given.contains(parameter.name)){
                functionInterpreter.data.vars.put(parameter.name, new Variable(parameter.name, parameter.fallback, new HashMap<>()));
            }
        }

        functionInterpreter.tokenManager.changeTokensParent(functionInterpreter);
        functionInterpreter.isFunction = true;
        functionInterpreter.compile();
        value = functionInterpreter.returned;
        return this;
    }

    // name = value coming up, rather than a value
    private boolean isNamed(){
        return interpreter.tokenManager.get(interpreter.pos + 1).type == PrebuiltTusanTokenType.IDENTIFIER
                && interpreter.tokenManager.get(interpreter.pos + 2).type == PrebuiltTusanTokenType.EQUAL;
    }

    private void missing(String function, List<FunctionParameter> required, int index){
        interpreter.error("TypeError", function + "() is missing its required argument " + required.get(index).name,
                List.of(function + "() needs " + required.size() + " argument(s) by position before any named ones"));
    }

    private void pass(Interpreter functionInterpreter, FunctionParameter parameter, Object val, String function){
        if (parameter.type != Types.ANY){
            Types valType = Utils.getTypeOfValue(val);
            if (valType != parameter.type){
                interpreter.error("TypeError", "Received type " + valType + " instead of " + parameter.type.name() + " for " + parameter.name + " in " + function + "()", null);
            }
        }
        functionInterpreter.data.vars.put(parameter.name, new Variable(parameter.name, val, new HashMap<>()));
    }
}
