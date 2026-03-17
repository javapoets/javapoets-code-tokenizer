package javapoets.tokenizer;

import java.util.List;
import javapoets.tokenizer.core.*;
import javapoets.tokenizer.debug.*;
import javapoets.tokenizer.language.*;

public class TokenizerDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TokenizerDemo.class);

    public static void main(String[] args) {
        String javaCode = """
            public class Hello {
                public static void main(String[] args) {
                    int x = 42;
                    System.out.println("Hi");
                }
            }
            """;

        String jsCode = """
            function greet(name) {
              const x = 42;
              console.log(`Hello ${name}`);
            }
            """;

        Tokenizer javaTokenizer = new Tokenizer(new JavaLanguageDefinition(), false, false);
        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);

        List<Token> javaTokens = javaTokenizer.tokenize(javaCode);
        List<Token> jsTokens = jsTokenizer.tokenize(jsCode);

        LoggingTokenPrinter.print(javaTokens);
        log.debug("=== Java Tokens ===");
        javaTokens.forEach(System.out::println);

        LoggingTokenPrinter.print(jsTokens);
        log.debug("\\n=== JavaScript Tokens ===");
        jsTokens.forEach(System.out::println);
    }
}