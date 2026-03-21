package javapoets.tokenizer;

import java.util.List;
import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.core.*;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.language.*;
import javapoets.tokenizer.parser.*;
import javapoets.tokenizer.visitor.*;

public class TokenizerDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TokenizerDemo.class);

    private static final String EMPTY = "";

    public static void main(String[] args) {

        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);

        List<Token> tokens = null;
        Parser parser = null;

        /*
         * Java Test
         */
        /*
        log.debug(EMPTY);
        log.debug("\\n=== JavaLanguageDefinition ===");
        log.debug(EMPTY);

        String javaCode = """
            public class Hello {
                public static void main(String[] args) {
                    int x = 42;
                    System.out.println("Hi");
                }
            }
            """;

        Tokenizer javaTokenizer = new Tokenizer(new JavaLanguageDefinition(), false, false);
        List<Token> javaTokens = javaTokenizer.tokenize(javaCode);

        log.debug("=== Java Tokens ===");
        LoggingTokenPrinter.print(javaTokens);
        //javaTokens.forEach(System.out::println);
        //javaTokens.forEach(token -> log.debug("{}", token));
        //*/

        /*
         * JavaScript Test
         */
        /*
        log.debug(EMPTY);
        log.debug("\\n=== JavascriptLanguageDefinition ===");
        log.debug(EMPTY);

        String jsCode = """
            function greet(name) {
              const x = 42;
              console.log(`Hello ${name}`);
            }
            """;

        List<Token> jsTokens = jsTokenizer.tokenize(jsCode);

        log.debug("\\n=== JavaScript Tokens ===");
        LoggingTokenPrinter.print(jsTokens);
        //*/
        //jsTokens.forEach(System.out::println);
        //jsTokens.forEach(token -> log.debug("{}", token));
        /*
        jsTokens.forEach(token -> {
            //if (token.type().equals("IDENTIFIER")) {
            if (token.type().equals(TokenType.IDENTIFIER)) {
               log.debug("{}", token);
            }
        });
        */

        /*
         * Parser Test
         */
        /*
        log.debug(EMPTY);
        log.debug("\\n=== Parser ===");
        log.debug(EMPTY);
        //Tokenizer tokenizer = new Tokenizer(new JavascriptLanguageDefinition());
        //List<Token> tokens = jsTokenizer.tokenize("let x = 10 + 5 * 2;");
        tokens = jsTokenizer.tokenize("let x = 10 + 5 * 2;");
        LoggingTokenPrinter.print(tokens);
        TokenStream stream = new TokenStream(tokens);
        //Parser parser = new Parser(stream);
        parser = new Parser(stream);
        List<Statement> ast = parser.parseProgram();
        //System.out.println(ast);
        for (Statement astStatement: ast) log.debug(astStatement.toString());
        //*/

        /*
         * Parser > PrettyPrinterVisitor Test
         */
        log.debug(EMPTY);
        log.debug("\\n=== PrettyPrinterVisitor ===");
        log.debug(EMPTY);

        //Tokenizer tokenizer = new Tokenizer(new JavaScriptLanguageDefinition());
        //List<Token> tokens = jsTokenizer.tokenize("""
        tokens = jsTokenizer.tokenize("""
            let x = 10 + 5 * 2;
            let y = x + 3;
        """);
        LoggingTokenPrinter.print(tokens);

        TokenStream tokenStream = new TokenStream(tokens);
        //Parser parser = new Parser(tokenStream);
        parser = new Parser(tokenStream);
        List<Statement> astStatements = parser.parseProgram();
        PrettyPrinterVisitor printer = new PrettyPrinterVisitor();
        for (Statement stmt : astStatements) {
            String output = stmt.accept(printer);
            //System.out.print(output);
            log.debug(output);
        }
    }
}