package net.tutla.tums.tusan.lexer.util;

import java.util.List;

public class WordedPattern {
    public static String makeOneWordPattern(String word){
        return "\\b"+word+"\\b";
    }
    public static String makeWordedPattern(List<String> keywords){
        StringBuilder str = new StringBuilder("\\b(");

        int looper = 0;
        for (String e : keywords){
            str.append(e);
            if (looper+1 != keywords.size()){
                str.append("|");
            }
            looper++;
        }
        str.append(")\\b");
        return str.toString();
    }

    public static String makeSpecialCharacterPattern(char e){
        return "\\"+e;
    }
}
