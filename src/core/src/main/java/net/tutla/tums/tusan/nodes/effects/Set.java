package net.tutla.tums.tusan.nodes.effects;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Name;
import net.tutla.tums.tusan.nodes.expression.Expression;
import net.tutla.tums.tusan.nodes.expression.Factor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// ### set
/// ```set [variable name] to [expression]```
///
/// - Variable Name could be a variable or a property (read Variable)
/// - This sets the variable name to the expression to be used later on
/// - Read about it in [Variables](https://wiki.tutla.net/tums/scripting/basic/variable)
public class Set extends Node {
    private Utils utils = new Utils();
    public Set(TusanContext ctx){
        super(ctx);
    }

    public Set create(){
        // the statement loop has already moved past "set": the current token is the name (or an ordinal)
        Token name = ctx.getCurrentToken();
        Object num = utils.isOrdinal(name);
        if (num != null){
            setItem(((Double) num).intValue() - 1, name.value);
        } else {
            Name n = new Name(ctx).create();
            if (interpreter.caughtError) return this;
            String target = n.name;
            Map<String, Variable> location = n.location;

            interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD,"to");
            interpreter.tokenManager.nextToken();
            Object val = new Expression(ctx).create().value;

            // a value that already is a Variable (a player, an entity) is stored as itself, so its
            // properties stay reachable through the new name
            Variable stored = val instanceof Variable v ? v : new Variable(target, val, null);
            location.put(target, stored);
            interpreter.data.vars.put("it", new Variable("it", val, null));
            interpreter.data.vars.put("this", new Variable("this", val, null));
        }
        return this;
    }

    // set 2nd item in mylist to "x"
    private void setItem(int n, String ordinal){
        interpreter.tokenManager.expectTokenClassic("KEYWORD:item|KEYWORD:character");
        interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.LOGIC, "in");
        interpreter.tokenManager.nextToken();
        Object container = new Factor(ctx).create().value;
        interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.KEYWORD, "to");
        interpreter.tokenManager.nextToken();
        Object exprValue = new Expression(ctx).create().value;

        int size = container instanceof List<?> l ? l.size() : container instanceof Map<?, ?> m ? m.size() : -1;
        if (size < 0){
            interpreter.error("TypeError", "You can only change an item of a list or a TSON", container instanceof String ? List.of("Strings cannot be changed, build a new one instead") : null);
        } else if (n < 0 || n >= size){
            interpreter.error("OutOfBoundsException", "There is no " + ordinal + " item, it only has " + size, null);
        } else if (container instanceof List) {
            ((List<Object>) container).set(n, exprValue);
        } else {
            Map<Object, Object> map = (Map<Object, Object>) container;
            Object key = new ArrayList<>(map.keySet()).get(n);
            map.put(key, exprValue);
        }
    }
}
