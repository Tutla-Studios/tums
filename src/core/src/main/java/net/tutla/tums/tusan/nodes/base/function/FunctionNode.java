package net.tutla.tums.tusan.nodes.base.function;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;
import net.tutla.tums.tusan.Types;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tusan.interpreter.TokenManager;
import net.tutla.tums.tusan.lexer.Token;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenGroup;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;
import net.tutla.tums.tusan.nodes.expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Functions are a set of instructions that will be executed when called
///
/// ```lua
/// function IDENTIFIER [parameters] that
/// ...
/// end
/// ```
///
/// - `function IDENTIFIER` creates a function named `IDENTFIER`
/// - [paramaters] are not necessary but is used to give functions data before they run as a variable
///   -  a parameter must be a token name (read Token)
/// - A Function by default returns `true`, if you want it to return back a value you can use a return statement:
///   - `return [expression]`
///   - If you use it outside a function it will end the script because a function creates a seperate `Interpreter` (read Tokens)
///
/// Example:
/// ```lua
/// function addnumbers(num1, num2):
///     return num1 + num2
/// end
///
/// print addnumbers(3, 2)
/// ```
///
/// This will print `5` to the console.
///
/// @tums.title Functions
/// @tums.summary How to use functions in tums
public class FunctionNode extends Node { // named this way to avoid any conflicts
    private String name;
    public List<FunctionParameter> parameters = new ArrayList<>();
    public Interpreter functionInterpreter = new Interpreter(ctx.getTusan());

    public FunctionNode(TusanContext ctx){
        super(ctx);
    }

    public FunctionNode create(){
        // function name(a, b: NUMBER, c = 1):
        Token nameToken = interpreter.currentToken;
        if (nameToken.type != PrebuiltTusanTokenType.IDENTIFIER){
            interpreter.error("SyntaxError", "Expected the function's name after function, got " + describe(nameToken), null);
        }
        name = nameToken.value;
        if (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.LEFT_PAR){
            interpreter.error("SyntaxError", "Expected ( after the function name " + name + ", got " + describe(interpreter.tokenManager.getNextToken()), null);
        }
        interpreter.tokenManager.nextToken();

        boolean optionalSeen = false;
        if (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.RIGHT_PAR){
            while (true){
                Token parameterToken = interpreter.tokenManager.nextToken();
                if (parameterToken.type != PrebuiltTusanTokenType.IDENTIFIER){
                    interpreter.error("SyntaxError", "Expected a parameter name in " + name + "(), got " + describe(parameterToken), null);
                }
                String parameterName = parameterToken.value;
                if (parameters.stream().anyMatch(p -> p.name.equals(parameterName))){
                    interpreter.error("SyntaxError", name + "() has two parameters called " + parameterName, null);
                }
                FunctionParameter parameter = new FunctionParameter(parameterName);

                if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.COLON){
                    interpreter.tokenManager.nextToken();
                    parameter.setType(Utils.getTypeFromName(interpreter.tokenManager.nextToken()));
                } else {
                    parameter.setType(Types.ANY);
                }

                if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.EQUAL){
                    optionalSeen = true;
                    interpreter.tokenManager.nextToken();
                    interpreter.tokenManager.nextToken();
                    parameter.setFallback(new Expression(ctx).create().value);
                } else if (optionalSeen){
                    interpreter.error("SyntaxError", "You cannot use required parameters after optional parameters", Arrays.asList("Move the optional parameters to the end of the function"));
                } else {
                    parameter.setFallback(null);
                    parameter.setRequired(true);
                }
                parameters.add(parameter);

                Token after = interpreter.tokenManager.getNextToken();
                if (after.type == PrebuiltTusanTokenType.COMMA){
                    interpreter.tokenManager.nextToken();
                    if (interpreter.tokenManager.getNextToken().type == PrebuiltTusanTokenType.RIGHT_PAR){
                        interpreter.error("SyntaxError", "Expected another parameter after the last , in " + name + "()", List.of("Remove the , before the )"));
                    }
                } else if (after.type == PrebuiltTusanTokenType.RIGHT_PAR){
                    break;
                } else {
                    interpreter.error("SyntaxError", "Expected , or ) after the parameter " + parameterName + " in " + name + "(), got " + describe(after),
                            after.type == PrebuiltTusanTokenType.IDENTIFIER ? List.of("Separate parameters with a comma: " + parameterName + ", " + after.value) : null);
                }
            }
        }
        interpreter.tokenManager.nextToken(); // the )
        if (interpreter.tokenManager.getNextToken().type != PrebuiltTusanTokenType.COLON){
            interpreter.error("SyntaxError", "Expected : after the parameters of " + name + "(), got " + describe(interpreter.tokenManager.getNextToken()), List.of("function " + name + "(...):"));
        }
        interpreter.tokenManager.nextToken();

        List<Token> tokens = new ArrayList<>();
        int structures = 0;

        Token nextToken = interpreter.tokenManager.getNextToken();
        if (nextToken == null){
            interpreter.error("SyntaxError", "Unexpected end of file in function definition", Arrays.asList("Make sure your function has an 'end' token"));
            return this;
        }

        if (nextToken.type == PrebuiltTusanTokenType.ENDSTRUCTURE){
            interpreter.tokenManager.nextToken();
            tokens.add(new Token(PrebuiltTusanTokenType.ENDSCRIPT, "", interpreter));
        } else {
            while (true){
                nextToken = interpreter.tokenManager.nextToken();
                if (nextToken == null){
                    interpreter.error("SyntaxError", "Unexpected end of file in function definition", Arrays.asList("Make sure your function has an 'end' token"));
                    return this;
                }


                // if/while/loop/function have their own token types, only their group says STRUCTURE
                if (nextToken.group == PrebuiltTusanTokenGroup.STRUCTURE){
                    structures++;
                    tokens.add(nextToken);
                } else if (nextToken.type == PrebuiltTusanTokenType.ENDSTRUCTURE){
                    if (structures == 0){
                        break;
                    } else {
                        tokens.add(nextToken);
                        structures--;
                    }
                } else {
                    tokens.add(nextToken);
                }
            }
        }
        tokens.add(new Token(PrebuiltTusanTokenType.ENDSCRIPT, "function", interpreter));
        TokenManager e = new TokenManager(functionInterpreter);
        e.setTokens(tokens);
        functionInterpreter.setup(interpreter.data, e, null, null, interpreter.getCompileEnvironment());
        interpreter.data.funcs.put(name, new FunctionRegistry(parameters, functionInterpreter));

        return this;
    }

    private static String describe(Token token){
        if (token == null || token.type == PrebuiltTusanTokenType.ENDSCRIPT) return "the end of the script";
        return token.value.isEmpty() ? token.type.name() : token.value;
    }
}
