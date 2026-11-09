package net.tutla.tums.tusan.lexer.util;

import java.util.List;

/// The pattern is the actual syntax that the lexer will use to identify tokens. You can either use raw regex by just providing a string or use the `WordedPattern` utility.
///
/// It comes with three methods to define simple patterns:
///
/// <!-- methods -->
///
/// @tums.title Patterns
/// @tums.summary How to create patterns for lexer rules
public class WordedPattern {
    /// Pattern for one word tokens
    public static String makeOneWordPattern(String word){
        return "\\b"+word+"\\b";
    }
    /// Pattern for multiple words for tokens
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

    /// Pattern for special characters, like `<`, `>` or `!` (or letters if your boring)
    public static String makeSpecialCharacterPattern(char e){
        return "\\"+e;
    }
}
