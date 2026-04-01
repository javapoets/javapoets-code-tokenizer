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

public class ParserDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ParserDemo.class);

    private static final String EMPTY = "";

    public static void main(String[] args) {

        /*
         * Parser Test
         */
        log.debug(EMPTY);
        log.debug("\\n=== Parser ===");
        log.debug(EMPTY);

        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);
        List<Token> tokens = jsTokenizer.tokenize("let x = 10 + 5 * 2;");
        LoggingTokenPrinter.print(tokens);
        TokenStream stream = new TokenStream(tokens);
        Parser parser = new Parser(stream);
        List<Statement> ast = parser.parseProgram();
        for (Statement astStatement: ast) log.debug(astStatement.toString());
    }
}