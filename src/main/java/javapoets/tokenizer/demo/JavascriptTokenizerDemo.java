package javapoets.tokenizer.demo;

import java.util.List;
import javapoets.tokenizer.ast.*;
import javapoets.tokenizer.token.*;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.language.*;
import javapoets.tokenizer.lexer.Tokenizer;
import javapoets.tokenizer.parser.*;
import javapoets.tokenizer.stream.TokenStream;
import javapoets.tokenizer.visitor.*;

public class JavascriptTokenizerDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavascriptTokenizerDemo.class);
    private static final String EMPTY = "";

    public static void main(String[] args) {

        /*
         * JavaScript Test
         */
        log.debug(EMPTY);
        log.debug("\\n=== JavascriptLanguageDefinition ===");
        log.debug(EMPTY);

        //*
        String jsCode = """
            function greet(name) {
              const x = 42;
              console.log(`Hello ${name}`);
            }
            """;
        //*/
        //String jsCode = "function add(a,b){ return a+b; }";

        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);
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

    }
}