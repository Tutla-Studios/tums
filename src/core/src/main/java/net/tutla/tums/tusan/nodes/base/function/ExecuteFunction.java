package net.tutla.tums.tusan.nodes.base.function;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Types;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.HashMap;
import java.util.List;

public class ExecuteFunction extends Node {
    public Object value;
    public FunctionRegistry originalFunction;
    public ExecuteFunction(TusanContext ctx){
        super(ctx);
    }

    public ExecuteFunction create(){
        interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.LEFT_PAR);

        originalFunction = interpreter.data.funcs.get(token.value);
        String name = token.value;
        Interpreter functionInterpreter = originalFunction.interpreter.clone();
        List<FunctionParameter> parameters = originalFunction.parameters;

        HashMap<String, FunctionParameter> optionalParameters = new HashMap<>();
        Integer parameterPointer = 0;
        for (FunctionParameter parameter : parameters){ // checking the required ones
            if (parameter.required){
                interpreter.tokenManager.nextToken();
                Object val = new Expression(ctx).create().value;

                if (parameter.type != Types.ANY){
                    Types valType = Utils.getTypeOfValue(val);
                    if (valType != parameter.type){
                        interpreter.error("TypeError", "Received type "+valType+" instead of "+parameter.type.name()+" in function {token.value} ", null);
                    }
                }
                functionInterpreter.data.vars.put(parameter.name, new Variable(parameter.name, val, new HashMap<>()));
            } else { // putting the optional ones into a hashmap
                optionalParameters.put(parameter.name, parameter);
            }
            if(parameters.size()-1 != parameterPointer){
                interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.COMMA);
            }
            parameterPointer++;
        }

        while (optionalParameters.containsKey(interpreter.tokenManager.getNextToken().value)){ // evaluating the optional ones
            FunctionParameter parameter = optionalParameters.get(interpreter.tokenManager.getNextToken().value);
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.EQUAL);
            interpreter.tokenManager.nextToken();
            Object val = new Expression(ctx).create().value;
            if (parameter.type != Types.ANY){
                Types valType = Utils.getTypeOfValue(val);
                if (valType != parameter.type){
                    interpreter.error("TypeError", "Received type "+valType+" instead of "+parameter.type.name()+" in function {token.value} ", null);
                }
            }
            functionInterpreter.data.vars.put(parameter.name, new Variable(parameter.name, val, new HashMap<>()));
            optionalParameters.remove(interpreter.tokenManager.getNextToken().value); // the ones that remain will take the fallback value

            if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.COMMA){
                interpreter.tokenManager.nextToken();
            }
        }

        for (FunctionParameter parameter : optionalParameters.values()){
            functionInterpreter.data.vars.put(parameter.name, new Variable(parameter.name, parameter.fallback, new HashMap<>()));
        }

        interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_PAR);
        functionInterpreter.tokenManager.changeTokensParent(functionInterpreter);
        functionInterpreter.isFunction = true;
        functionInterpreter.compile();

        value = functionInterpreter.returned;

        return this;
    }
}
