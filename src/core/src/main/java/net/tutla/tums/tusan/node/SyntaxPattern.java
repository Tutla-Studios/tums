package net.tutla.tums.tusan.node;

public class SyntaxPattern {
    public String pattern;
    public SyntaxPattern(String pattern){
        this.pattern = pattern;
    }

    // TODO: Make this detect whole pattern sequences like "<TOKEN> and <TOKEN> if <TOKEN> (abcd|sum|regex|here)"
}
