package net.tutla.tums.tusan.interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.lang.Tusan;
import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.Utils;
import net.tutla.tums.tusan.lexer.*;
import net.tutla.tums.tusan.lexer.util.prebuilt.PrebuiltTusanTokenType;

/// The interpreter is by far the most important part in Tums. It is the instance that parses your script.
///
/// ## Creating Interpreters
///
/// 1. Define an interpreter using `new Interpreter(Tusan language)`. With the language being the language you have created by using the previous steps, you can use `TumsAPI.getGlobalTusan()` if you want to execute the global language.
/// 2. Set it up using `setup(InterpreterData data, TokenManager tknmanager, String _text, Path file, MinecraftEnvironment env)`.
///
/// That looks big doesn't it? Let's break it down:
/// - InterpreterData: Contains all the generated data (variables, functions, events, etc)
///     - Define using `InterpreterData(HashMap<String, Variable> vars, HashMap<String, FunctionRegistry> funcs, HashMap<String, Variable> local, List<Runnable> asyncTasks)`
///     - If you want to provide it anything in advance, for example a variable called `me` that gets the current player, you can instantiate a new Hashmap and add one.
///     - You can provide them all as `null` and it will instantiate an empty interpreter data.
/// - TokenManager: You can provide a custom token manager if you want to execute a custom list of tokens
/// - String text: Optional, if you just want to execute a string of code, you can provide it here.
/// - String file: Optional, if you want to execute a file, you can provide it here.
/// - MinecraftEnvironment: This is the environment the script, by default `SERVER`
/// 3. Optional: Set the context using `setContext(TusanContext ctx)`. This is applicable if you want to provide a custom [context](https://wiki.tutla.net/tums/developer/context).
///
/// That's it! You're ready to start. Here's an example if you're still confused:
///
/// ```java
/// public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
///     Interpreter interpreter = new Interpreter(tusan);
///     interpreter.setup(null,null,null, script, environment);
///     interpreter.setContext(new TusanContext(interpreter)); // not necessary
/// }
/// ```
///
/// ## Executing Scripts
///
/// To execute a script just call `compile()` on the interpreter. __Or you could just use `TumsAPI.runScript(Path script, Tusan tusan, MinecraftEnvironment environment)` if you're not creating an interpreter__
///
/// ```java
/// public static void runScript(Path script, Tusan tusan, MinecraftEnvironment environment){
///     Interpreter interpreter = new Interpreter(tusan);
///     interpreter.setup(null,null,null, script, environment);
///     interpreter.setContext(new TusanContext(interpreter));
///
///     try {
///         interpreter.compile();
///     } catch (Exception e){
///         e.printStackTrace();
///     }
///
///     getRegister().add(interpreter.data);
/// }
/// ```
///
/// ## Methods
///
/// <!-- methods -->
///
/// @tums.title Interpreter
/// @tums.summary Running your scripts in tums
public class Interpreter {

    private final Tusan tusan;
    private TusanLanguageRules lang;

    private TusanContext ctx = null;

    /// Helpers for working with values and identifiers
    public Utils util = new Utils();

    /// All the data the script has made: variables, functions, events, etc
    public InterpreterData data;
    /// The token manager, read about it in [Parsing](https://wiki.tutla.net/tums/developer/node/parsing)
    public TokenManager tokenManager = new TokenManager(this);

    /// The script's code
    public String text;
    /// The absolute path of the script file, or `<stdin>` when running code from a string
    public String file;
    /// The value a `return` gave back, when running a function
    public Object returned;
    /// Whether this interpreter is running a function's body
    public Boolean isFunction = false;

    private Path filePath;

    /// Whether the script has finished
    public Boolean end = false;
    /// Whether an error has been reported
    public Boolean caughtError = false;
    /// The position of the current token
    public int pos = 0;
    /// The current token
    public Token currentToken;
    /// How many loops the current statement is inside. `break` is only allowed when this is above 0
    public int loopDepth = 0;
    /// Set by `break` until the innermost loop sees it and stops
    public boolean breaking = false;

    private MinecraftEnvironment compileEnvironment = MinecraftEnvironment.SERVER;


    /// Creates an interpreter for a language
    public Interpreter(Tusan tusan) {
        this.tusan = tusan;
        this.lang = tusan.getLang();
    }

    /// Prepares the interpreter to run a script. Any argument can be `null`
    public void setup(InterpreterData data, TokenManager tknmanager, String _text, Path file, MinecraftEnvironment env){
        this.data = data != null ? data : new InterpreterData(null, null, null, null);

        if (_text != null) {
            this.text = _text;
        }
        if (file != null) {
            this.text = readFileContents(file);
            this.file = String.valueOf(file.toAbsolutePath());
            this.filePath = file;
        }
        else {
            this.file = "<stdin>";
        }

        if (tknmanager == null){
            Lexer lexer = new Lexer(this.text, this, lang);
            tokenManager.setTokens(lexer.classify());
        } else {
            tokenManager.setTokens(tknmanager.getAll());
            tokenManager.changeTokensParent(this);
        }

        if (env != null){
            compileEnvironment = env;
        }

        this.currentToken = tokenManager.get(this.pos);
    }

    /// Runs the script
    public void compile() {
        tokenManager.changeTokensParent(this);
        end = false;
        caughtError = false;
        pos = 0;
        currentToken = tokenManager.get(pos);

        /*for (Token t : tokenManager.getAll()){
            System.out.print(t.type);
            System.out.print(":");
            System.out.print(t.value);
            System.out.print("\n");
        }*/

        if (ctx == null){
            setContext(new TusanContext(this));
        }
        if (compileEnvironment == MinecraftEnvironment.CLIENT){
            ctx.setEnvironment(MinecraftEnvironment.CLIENT);
        }
        tusan.compile(ctx);
    }

    /// Uses a custom [context](https://wiki.tutla.net/tums/developer/context) instead of the default one
    public void setContext(TusanContext ctx){
        this.ctx = ctx;
    }

    // utils




    /// Reports an error with the position it happened at, and stops the script
    public void error(String name, String detail, List<String> notes){
        if (!caughtError){
            System.out.println("================ ERROR ================");
            System.out.println(name+" : "+detail);
            System.out.println("============== POSITION ===============");
            System.out.println(arrowsAtPosition());
            if (notes != null){
                System.out.println("================ NOTES ================");
                for (String note : notes){
                    System.out.println(note);
                }
            }
            System.out.println("=======================================");
            meetEnd();
        }
        caughtError = true;
    }

    /// Returns the code around the current position, with arrows pointing at the current token. Used by `error`
    public String arrowsAtPosition(){
        StringBuilder recreated = new StringBuilder();
        StringBuilder arrows = new StringBuilder();
        int npos = 0;
        String target = "NOTHING:UNKNOWN";
        for (Token i : tokenManager.getAll()){
            npos++;
            if (npos >= pos-2 && npos <= pos+4){
                String tokenStr;
                int width;
                if (i.type == PrebuiltTusanTokenType.STRING){
                    tokenStr = " \"" + i.value + "\"";
                    width = i.value.length() + 3;
                } else {
                    tokenStr = " "+i.value;
                    width = i.value.length()+1;
                }

                recreated.append(tokenStr);
                String tt;
                if (npos == pos+1){
                    tt = "^";
                    target = i.value;
                } else {
                    tt = " ";
                }
                arrows.append(tt.repeat(width));
            }
        }
        recreated.append("\t\t<----- "+target).append("\n").append(arrows);
        return recreated.toString();
    }

    /// Reads a whole script file
    public static String readFileContents(Path path) {
        try {
            return Files.readString(path);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }



    /// Marks the script as finished, so nothing after the current token runs
    public void meetEnd(){
        this.end = true;
        if (!isFunction){
            System.out.println("=======================================");
        }
        // System.exit(0);
    }

    /// Returns a new interpreter set up with the same data and tokens
    public Interpreter clone(){
        Interpreter intr = new Interpreter(tusan);
        intr.setup(data, tokenManager, text, filePath, intr.getCompileEnvironment());
        return intr;
    }

    /// Replaces the lexer rules this interpreter reads the script with
    public void setLanguage(TusanLanguageRules lang){
        this.lang = lang;
    }

    /// Returns the language this interpreter runs
    public Tusan getTusan(){
        return tusan;
    }

    /// Returns the environment the script runs in
    public MinecraftEnvironment getCompileEnvironment(){
        return compileEnvironment;
    }

    /// Sets the environment the script runs in, `SERVER` when `null`
    public void setCompileEnvironment(MinecraftEnvironment env){
        if (env == null){
            env = MinecraftEnvironment.SERVER;
        }
        this.compileEnvironment = env;
    }

}
