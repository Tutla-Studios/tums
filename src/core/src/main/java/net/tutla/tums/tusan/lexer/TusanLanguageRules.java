package net.tutla.tums.tusan.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TusanLanguageRules { // this is the syntax register
    private final List<LexerRule> RULES = new ArrayList<>();
    private Pattern MASTER = null;

    public void registerRule(LexerRule rule) {
        RULES.add(rule);
        MASTER = null;
    }

    private Pattern buildPattern() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RULES.size(); i++) {
            LexerRule rule = RULES.get(i);
            if (!sb.isEmpty()) sb.append("|");
            sb.append("(?<G").append(i).append(">(?:").append(rule.regex()).append("))");
        }
        sb.append("|(?<IDENTIFIER>[A-Za-z_][A-Za-z0-9_]*)");
        sb.append("|(?<WHITESPACE>[ \\t\\r\\n]+)");
        return Pattern.compile(sb.toString());
    }

    public Matcher match(CharSequence input) {
        if (MASTER == null) MASTER = buildPattern();
        return MASTER.matcher(input);
    }

    public List<LexerRule> getRules(){
        return RULES;
    }
}
