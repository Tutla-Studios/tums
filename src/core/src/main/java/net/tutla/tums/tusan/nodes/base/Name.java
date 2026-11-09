package net.tutla.tums.tusan.nodes.base;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

import java.util.Map;

/// - Variables are like boxes, they store things. You can read `set` in `effects` to see how to declare one.
/// - Variables can also have *properties*. You can declare/call a property by saying `IDENTIFIER's IDENTIFIER`
///   - The first identifier is the name of the variable to which property you are going to set and the second on is the name of the property
///
/// - [variable name] is either a property (read the second point above) or a variable ( `IDENTIFIER` )
///
/// @tums.title Variables
/// @tums.summary How to use variables in tums
public class Name extends Node {
    public static String name;
    public Object value;
    public Map<String, Variable> location = interpreter.data.vars;
    public Name(TusanContext ctx){
        super(ctx);
    }

    public Name create() { // fuck ass logic made me take 1 hour to understand
        name = token.value;
        if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.PROPERTY) {
            if (!(location.get(name) instanceof Variable holder)) {
                interpreter.error("UndefinedVariable", name + " was not defined, so it has no properties", null);
                return this;
            }
            location = holder.properties;
            while (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.PROPERTY) {
                interpreter.tokenManager.nextToken();
                name = interpreter.tokenManager.nextToken().value;

                if (!location.containsKey(name)) break;

                if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.PROPERTY) {
                    location = ((Variable) location.get(name)).properties;
                } else {
                    break;
                }
            }

        }

        if (location.containsKey(name)) {
            Object val = location.get(name);
            if (!(val instanceof Variable)) {
                value = val;
            } else {
                value = ((Variable) val).getValue();
            }
        }

        return this;
    }

    public Object getValue() {
        return value;
    }
}
