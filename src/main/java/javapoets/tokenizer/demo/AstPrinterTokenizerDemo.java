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

public class AstPrinterTokenizerDemo {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AstPrinterTokenizerDemo.class);
    private static final String EMPTY = "";

    public static void main(String[] args) {

        /*
         * Parser > AstPrinterVisitor Test
         */

        log.debug(EMPTY);
        log.debug("\\n=== AstPrinterVisitor ===");
        log.debug(EMPTY);

        Tokenizer jsTokenizer = new Tokenizer(new JavascriptLanguageDefinition(), false, false);
        List<Token> tokens = jsTokenizer.tokenize("""
            let x = 10 + 5 * 2;
            let y = x + 3;
        """);
        LoggingTokenPrinter.print(tokens);

        TokenStream tokenStream = new TokenStream(tokens);
        Parser parser = new Parser(tokenStream);
        List<Statement> astStatements = parser.parseProgram();
        AstPrinterVisitor printer = new AstPrinterVisitor();
        for (Statement stmt : astStatements) {
            String output = stmt.accept(printer);
            log.debug(output);
        }
    }
}