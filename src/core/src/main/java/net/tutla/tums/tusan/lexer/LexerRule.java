package net.tutla.tums.tusan.lexer;

import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;

/// A lexer rule is connecting the user's code into a token that can be interpreted by the parser. Basically, it's your syntax.
///
/// To define a lexer rule, you need to create a new instance of `LexerRule` and pass it to the `registerLexerRule` function in the Tusan Language you are using.
///
/// Example:
/// ```java
/// public class YourModInitializer implements ModInitializer {
///     private Tusan language;
/// 	@Override
/// 	public void onInitialize() {
///         language.registerLexerRule(
///             new LexerRule(MyTokenType.HELLO,
///                 WordedPattern.makeOneWordPattern("hello")
///             )
///         );
/// 	}
/// }
/// ```
///
/// ## Tokens
///
/// In order to create a lexer rule you need to first define a token type. To create one, create an enum implementing `TokenType`. For example:
/// ```java
/// import net.tutla.tums.tusan.lexer.TokenType;
///
/// public enum MyTokenType implements TokenType {
///     HELLO
/// }
/// ```
///
/// You can also optionally implement a group:
/// ```java
/// import net.tutla.tums.tusan.lexer.TokenGroup;
///
/// public enum MyTokenGroup implements TokenGroup {
///     KEYWORD,
///     FUNCTION,
///     OPERATOR
/// }
/// ```
///
/// ## Creating the rule
///
/// Once you have the token type and optionally a group, intialize it:
/// ```java
/// language.registerLexerRule(
///     new LexerRule(
///         MyTokenType.HELLO,
///         WordedPattern.makeOneWordPattern("hello")
///     )
/// );
/// ```
///
/// The `TokenGroup` can be added by using `.setGroup()`.
///
/// In the place of `WordedPattern.makeOneWordPattern("hello")` you can use any regex pattern, to learn more about it read [Patterns](https://wiki.tutla.net/tums/developer/lexer/patterns). For example `"\\bhello\\b"`.
///
/// ## Methods
///
/// <!-- methods -->
///
/// @tums.title Lexer Rules
/// @tums.summary How to create lexer rules in Tums
public class LexerRule {
    public final TokenType type;
    public final String regex;
    public TokenGroup group= PrebuiltTusanTokenGroup.NONE;

    /// Creates a rule that turns text matching the regex into a token of this type
    public LexerRule(TokenType type, String regex) {
        this.type = type;
        this.regex = regex;
    }

    /// Puts the token in a group, and returns the rule so it can be chained
    public LexerRule setGroup(TokenGroup e) {
        this.group = e;
        return this;
    }

    public TokenType type(){
        return type;
    }

    public String regex(){
        return regex;
    }
}
