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

public class JavaTokenizerDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavaTokenizerDemo.class);
    private static final String EMPTY = "";

    public static void main(String[] args) {

        /*
         * Java Test
         */
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
    }

}