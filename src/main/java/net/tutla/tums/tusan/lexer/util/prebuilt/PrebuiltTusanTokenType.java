package net.tutla.tums.tusan.lexer.util.prebuilt;

import net.tutla.tums.tusan.lexer.TokenType;

public enum PrebuiltTusanTokenType implements TokenType { // these are currently all groups, TODO: make these all individual keyword tokens
    // types
    STRING,
    NUMBER,
    BOOL,
    NOTHING,
    TYPE,
    EVENT,

    PROPERTY,
    OPERATOR,
    LOGIC,
    COMPARISON,
    EQUAL,

    IDENTIFIER, // undefined keywords basically
    ENDSCRIPT, // end of script
    // keywords
    KEYWORD,
    STRUCTURE,
    EFFECT,

    TIME,
    BREAKSTRUCTURE,
    ENDSTRUCTURE,

    // symbols
    LEFT_CURLY,
    RIGHT_CURLY,
    LEFT_PAR,
    RIGHT_PAR,
    LEFT_SQUARE,
    RIGHT_SQUARE,
    SEMICOLON,
    COLON,
    COMMA,

    IF,
    WHILE,
    ON,
    LOOP,
    FUNCTION,

    // effects
    PRINT
}
