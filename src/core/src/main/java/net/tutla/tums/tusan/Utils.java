package net.tutla.tums.tusan;

import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.TokenManager;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;
import net.tutla.tums.tums.EventType;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {

    public Utils(){

    }
    public Object getType(Object token, TusanContext ctx) {
        Object e;
        Class<?> type_;

        if (token instanceof Token) {
            try {
                e = new Expression(ctx).create();
                type_ = e == null ? null : e.getClass();
            } catch (Exception ex) {
                return "<JAVA:ERROR>";
            }
        } else {
            type_ = token == null ? null : token.getClass();
        }

        if (type_ == null) {
            return Types.NOTHING;
        } else if (Number.class.isAssignableFrom(type_)) {
            return Types.NUMBER;
        } else if (type_ == String.class) {
            return Types.STRING;
        } else if (type_ == Boolean.class) {
            return Types.BOOL;
        } else if (type_.isArray() || List.class.isAssignableFrom(type_)) {
            return Types.LIST;
        } else if (Map.class.isAssignableFrom(type_)) {
            return Types.TSON;
        }

        return "<JAVA:" + type_.toString() + ">";
    }

    public Double isOrdinal(Token token){
        if (token.type == PrebuiltTusanTokenType.IDENTIFIER){
            String val = token.value.toLowerCase();
            if (val.endsWith("st") || val.endsWith("nd") || val.endsWith("rd") || val.endsWith("th")){
                try {
                    return Double.parseDouble(val.substring(0, val.length() - 2));
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public Boolean isValidIdentifier(String value, Interpreter interpreter){
        return interpreter.data.vars.containsKey(value) || interpreter.data.funcs.containsKey(value);
    }

    public static List<Integer> range(int stop) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < stop; i += 1) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> range(int start, int stop) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < stop; i += 1) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> range(int start, int stop, int step) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < stop; i += step) {
            list.add(i);
        }
        return list;
    }

    public static boolean isEventType(String value) {
        String lowerValue = value.toLowerCase();
        for (EventType e : EventType.values()) {
            if (e.name().toLowerCase().equals(lowerValue)) {
                return true;
            }
        }
        return false;
    }



    public static boolean hasField(Object obj, String fieldName) {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) return true;
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    public static List<Types> getTypes(){
        List<Types> types = new ArrayList<>();
        for (Types type : Types.values()){
            types.add(type);
        }
        return types;
    }

    public static List<String> getTypeNames(){
        List<String> types = new ArrayList<>();
        for (Types type : Types.values()){
            types.add(type.name().toUpperCase());
        }
        return types;
    }

    public static Types getTypeFromName(Token token){
        if (token.type == PrebuiltTusanTokenType.TYPE){
            for (Types type : Types.values()){
                if (token.value.equalsIgnoreCase(type.name())){
                    return type;
                }
            }
            token.interpreter.error("InvalidType",token.type.name()+":"+token.value+" is not a valid type", null);
            return Types.NOTHING;
        } else {
            token.interpreter.error("InvalidType",token.type.name()+":"+token.value+" is not a valid token type", Arrays.asList("This is most likely an interpreter issue"));
        }
        return Types.NOTHING;
    }

    public static Types getTypeOfValue(Object value){
        if (value instanceof Integer || value instanceof Double) {
            return Types.NUMBER;
        } else if (value instanceof String ){
            return Types.STRING;
        } else if (value instanceof Boolean ){
            return Types.BOOL;
        } else if (value instanceof List || value instanceof ArrayList<?>){
            return Types.LIST;
        } else if (value instanceof Map){
            return Types.TSON;
        } else if (value == null ){
            return Types.NOTHING;
        } else {
            return null;
        }
    }

    public static List<Token> collectInnerTokens(TokenManager tokenManager){
        Boolean end = false;
        int structures = 0;
        List<Token> tokens = new ArrayList<>();

        while (!end){
            Token nxt = tokenManager.nextToken();
            if (nxt.type == PrebuiltTusanTokenType.ENDSTRUCTURE){
                if (structures == 0){
                    end = true;
                } else {
                    tokens.add(nxt);
                    structures--;
                }
            } else {
                if (nxt.group == PrebuiltTusanTokenGroup.STRUCTURE) {
                    structures++;
                }
                tokens.add(nxt);
            }
        }

        return tokens;
    }
}
