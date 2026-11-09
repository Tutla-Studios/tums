package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Types;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.base.Effect;
import net.tutla.tums.tusan.nodes.base.Name;
import net.tutla.tums.tusan.nodes.base.function.ExecuteFunction;

import java.util.*;

public class Factor extends Node {
    public Object value;
    private final Utils util = new Utils();
    public Factor(TusanContext ctx){
        super(ctx);
    }

    public Factor create(){
        value = token.value;
        if (token.type == PrebuiltTusanTokenType.OPERATOR && "-".equals(token.value)) {
            interpreter.tokenManager.nextToken();
            value = -((Double) new Factor(ctx).create().value);
            return this;
        } else if (token.type == PrebuiltTusanTokenType.NUMBER){
            this.value = Double.parseDouble(token.value);
            if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.TIME){
                HashMap<String, Double> timeUnits = new HashMap<>();
                timeUnits.put("millisecond", 0.001);
                timeUnits.put("second", 1.0);
                timeUnits.put("minute", 60.0);
                timeUnits.put("hour", 3600.0);
                timeUnits.put("day", 86400.0);
                timeUnits.put("week", 604800.0);
                timeUnits.put("month", 2592000.0);
                timeUnits.put("year", 31536000.0);

                value = (Double) value*timeUnits.get(interpreter.tokenManager.nextToken().value);
            }
        } else if (token.type == PrebuiltTusanTokenType.STRING){
            value = token.value;
        } else if (token.type == PrebuiltTusanTokenType.BOOL){
            value = Objects.equals(token.value, "true");
        } else if (token.type == PrebuiltTusanTokenType.NOTHING) {
            value = null;
        } else if (token.type == PrebuiltTusanTokenType.TYPE){
            switch (token.value) {
                case "NUMBER" -> value = Types.NUMBER;
                case "STRING" -> value = Types.STRING;
                case "NOTHING" -> value = Types.NOTHING;
                case "BOOLEAN" -> value = Types.BOOL;
            }
        } else if (token.type == PrebuiltTusanTokenType.KEYWORD){
            // WIP
        } else if (token.type == PrebuiltTusanTokenType.EFFECT) {
            value = new Effect(ctx).create().value;
        } else if (token.type == PrebuiltTusanTokenType.IDENTIFIER){
            Object ordn = util.isOrdinal(token);
            if (ordn != null){
                int n = ((Double) ordn).intValue();
                n--;
                interpreter.tokenManager.expectTokenClassic("KEYWORD:character|KEYWORD:item");
                interpreter.tokenManager.expectToken(PrebuiltTusanTokenType.LOGIC, "in");
                interpreter.tokenManager.nextToken();
                Object list = new Factor(ctx).create().value;
                if (list instanceof List) {
                    if (((List<Object>) list).size() <= n){
                        interpreter.error("OutOfBoundsException", "Tried to index over array's items", null);
                    }
                    value = ((List<Object>) list).get(n);
                } else if (list instanceof Map) {
                    int i = 0;
                    for (Object v : ((Map<?, ?>) list).values()) {
                        if (i == n) value = v;
                        i++;
                    }
                }
            } else if (interpreter.data.vars.containsKey(token.value)){
                value = new Name(ctx).create().value;
            } else if (interpreter.data.funcs.containsKey(token.value)){
                value = new ExecuteFunction(ctx).create().value;
            } else {
                interpreter.error("UndefinedVariable",token.value+" was not defined", null);
            }
        } else if (token.type == PrebuiltTusanTokenType.LEFT_PAR) {
            interpreter.tokenManager.nextToken();
            value = new Expression(ctx).create().value;
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_PAR);
        } else if (token.type == PrebuiltTusanTokenType.LEFT_CURLY){ // TSON :fire: (Tutla's Object Notation or Tutla's Shitty Object Notation)
            LinkedHashMap<String, Object> dict = new LinkedHashMap<>();
            while (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.RIGHT_CURLY){
                Token e = interpreter.tokenManager.nextToken();
                if (e.type == PrebuiltTusanTokenType.STRING || e.type == PrebuiltTusanTokenType.IDENTIFIER){
                    String key = e.value;
                    interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.COLON);
                    interpreter.tokenManager.nextToken();
                    Object val = new Expression(ctx).create().value;
                    dict.put(key, val);
                } else {
                    interpreter.error("InvalidTSON", "TSON Key must either be a string or non-keyword (identifier) token", null);
                }
                if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.COMMA){
                    interpreter.tokenManager.nextToken();
                }
                // TODO: check if no ending RIGHT_CURLY
            }
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_CURLY);
            value = dict;
        } else if (token.type == PrebuiltTusanTokenType.LEFT_SQUARE){
            List<Object> list = new ArrayList<>();
            while (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.RIGHT_SQUARE){
                interpreter.tokenManager.nextToken();
                list.add(new Expression(ctx).create().value);
                if (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.COMMA && interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.RIGHT_SQUARE){
                    interpreter.error("InvalidList", "Invalid list, seperate items using a comma and close it in a square bracket", null);
                }
                if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.COMMA){
                    interpreter.tokenManager.nextToken();
                }
            }
            interpreter.tokenManager.expectTokenType(PrebuiltTusanTokenType.RIGHT_SQUARE);
            value = list;
            // TODO: check if no ending RIGHT_SQUARE
        } else {
            interpreter.error("InvalidFactor",value.toString()+" is not a valid factor",null);
        }

        if ((value instanceof String || value instanceof List || value instanceof Map) && interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.LEFT_SQUARE){
            // TODO should add indexing similar to native tusan
        }



        return this;
    }
}
